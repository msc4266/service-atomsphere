package com.manywho.services.atomsphere.identity;

import com.manywho.sdk.api.AuthorizationType;
import com.manywho.sdk.api.run.elements.config.Authorization;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataRequest;
import com.manywho.sdk.api.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.sdk.api.security.AuthenticatedWhoResult;
import com.manywho.sdk.api.security.AuthenticationCredentials;
import com.manywho.sdk.services.configuration.ConfigurationParser;
import com.manywho.sdk.services.controllers.AbstractIdentityController;
import com.manywho.sdk.services.types.TypeBuilder;
import com.manywho.sdk.services.types.system.$User;
import com.manywho.sdk.services.types.system.AuthorizationAttribute;
import com.manywho.services.atomsphere.ServiceConfiguration;
import com.manywho.services.atomsphere.database.AtomsphereAPI;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class IdentityController extends AbstractIdentityController {
    private Logger logger;

    private final Provider<AuthenticatedWho> authenticatedWhoProvider;
    private final ConfigurationParser configurationParser;
    private final TypeBuilder typeBuilder;
    private String token;

    @Context
    private HttpHeaders httpHeaders;

    @Context
    private UriInfo uriInfo;

    @Inject
    public IdentityController(Provider<AuthenticatedWho> authenticatedWhoProvider, ConfigurationParser applicationConfigurationProvider, TypeBuilder typeBuilder) {
        logger = Logger.getLogger(this.getClass().getName());
        this.authenticatedWhoProvider = authenticatedWhoProvider;
        this.configurationParser = applicationConfigurationProvider;
        this.typeBuilder = typeBuilder;
    }

    @Path("/authentication")
    @POST
    @Override
    public AuthenticatedWhoResult authentication(AuthenticationCredentials authenticationCredentials) throws Exception {
        logger.fine("authentication");
        AuthenticatedWhoResult authenticatedWhoResult = new AuthenticatedWhoResult();
        ServiceConfiguration configuration = (ServiceConfiguration)configurationParser.from(authenticationCredentials);
        token = AtomsphereAPI.buildAuthToken(authenticationCredentials.getUsername(), authenticationCredentials.getPassword());
        configuration.setUseIDPCredentials(true);
        String status = this.verifyCredentialsWithAtomAPI(configuration, token);
        if (!"200".contentEquals(status)) {
            authenticatedWhoResult.setStatus(AuthenticatedWhoResult.AuthenticationStatus.AccessDenied);
            return authenticatedWhoResult;
        }

        authenticatedWhoResult.setDirectoryId("Boomi Atomsphere Directory ID");
        authenticatedWhoResult.setDirectoryName("Boomi Atomsphere Directory Name");
        authenticatedWhoResult.setEmail(authenticationCredentials.getUsername());
        authenticatedWhoResult.setFirstName("Boomi");
        authenticatedWhoResult.setIdentityProvider("Boomi Atomsphere");
        authenticatedWhoResult.setLastName("User");
        authenticatedWhoResult.setStatus(AuthenticatedWhoResult.AuthenticationStatus.Authenticated);
        authenticatedWhoResult.setTenantName(configuration.getAccount());
        authenticatedWhoResult.setToken(token);
        authenticatedWhoResult.setUserId(authenticationCredentials.getUsername());
        authenticatedWhoResult.setUsername(authenticationCredentials.getUsername());

        return authenticatedWhoResult;
    }

    /**
     * We allow authenticate using username_password
     *     *
     * @param objectDataRequest
     * @return
     * @throws Exception
     */
    @Path("/authorization")
    @POST
    @Override
    public ObjectDataResponse authorization(ObjectDataRequest objectDataRequest) throws Exception {
    	AuthenticatedWho authenticatedWho=null;
    	try {
    		authenticatedWho = authenticatedWhoProvider.get();
    	}
        catch(Exception e)
        {
        	throw new Exception("Unable to authorize user");
        }
        logger.fine("authorization");

        $User userObject;
        ServiceConfiguration configuration = configurationParser.from(objectDataRequest);
        String status = getUserAuthorizationStatus(objectDataRequest.getAuthorization(), authenticatedWho);
        if (status.equals("401")) {
            userObject = new $User();
            userObject.setDirectoryId("Boomi Atomsphere");
            userObject.setDirectoryName("Boomi Atomsphere ("+configuration.getAccount()+")");
            userObject.setAuthenticationType(AuthorizationType.UsernamePassword);
//            userObject.setLoginUrl(getFakeIdProviderUrl(configuration));
            userObject.setStatus("401");
            userObject.setUserId(UUID.randomUUID().toString());
        } else {
            userObject = new $User();
            userObject.setDirectoryId("Boomi Atomsphere");
            userObject.setDirectoryName("Boomi Atomsphere ("+configuration.getAccount()+")");
            userObject.setAuthenticationType(AuthorizationType.UsernamePassword);
            userObject.setLoginUrl("");
//            userObject.setPrimaryGroupId("7");
//            userObject.setPrimaryGroupName("S Club 7");
//            userObject.setRoleId("123");
//            userObject.setRoleName("Party Creators");
            userObject.setStatus("200");
            userObject.setUserId(UUID.randomUUID().toString());
        }

        return new ObjectDataResponse(typeBuilder.from(userObject));
    }

    @Path("/authorization/group/attribute")
    @POST
    @Override
    public ObjectDataResponse groupAttributes(ObjectDataRequest objectDataRequest) throws Exception {
        logger.fine("groupAttributes");
        AuthorizationAttribute attribute = new AuthorizationAttribute("users", "Users");

        return new ObjectDataResponse(typeBuilder.from(attribute));
    }

    @Path("/authorization/group")
    @POST
    @Override
    public ObjectDataResponse groups(ObjectDataRequest objectDataRequest) throws Exception {
        logger.fine("groups");
        List<MObject> groupsToReturn = new ArrayList<>();

        return createResponse(groupsToReturn, false);
    }

    @Path("/authorization/user/attribute")
    @POST
    @Override
    public ObjectDataResponse userAttributes(ObjectDataRequest objectDataRequest) throws Exception {
        logger.fine("userAttributes");
        AuthorizationAttribute authorizationAttribute = new AuthorizationAttribute("accountId", "Account ID");

        return new ObjectDataResponse(typeBuilder.from(authorizationAttribute));
    }

    @Path("/authorization/user")
    @POST
    @Override
    public ObjectDataResponse users(ObjectDataRequest objectDataRequest) throws Exception {
        logger.fine("users");
        List<MObject> usersToReturn = new ArrayList<>();
 
        return createResponse(usersToReturn, false);
    }

    public String getUserAuthorizationStatus(Authorization authorization, AuthenticatedWho user) {
        logger.fine("getUserAuthorizationStatus");
        switch (authorization.getGlobalAuthenticationType()) {
            case Public:
                return "401";
            case AllUsers:
                return "200";
            case Specified:
            default:
                return "401";
        }
    }

    private ObjectDataResponse createResponse(List<MObject> objectCollection, boolean hasMore) {
        logger.fine("createResponse");
        ObjectDataResponse objectDataResponse =  new ObjectDataResponse(objectCollection);
        objectDataResponse.setHasMoreResults(hasMore);

        return objectDataResponse;
    }
    
    private String verifyCredentialsWithAtomAPI(ServiceConfiguration configuration, String token)
    {
    	String status="200";
    	try {
    		String body = "{\"QueryFilter\": {\"expression\": {\"argument\": [\"XXXX\"],\"operator\": \"EQUALS\",\"property\": \"name\"}}}";
    		JSONObject response = AtomsphereAPI.executeAPI(configuration, token, "Atom", "POST", "query", body, false);
    	} catch (Exception e)
    	{
   			status="401";
    		logger.fine("verifyCredentialsWithAtomAPI:" + e.getMessage());
    	}
    	return status;
    }
}