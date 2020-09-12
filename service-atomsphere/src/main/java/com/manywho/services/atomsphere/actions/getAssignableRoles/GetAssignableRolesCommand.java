package com.manywho.services.atomsphere.actions.getAssignableRoles;

import javax.inject.Inject;

import org.json.JSONObject;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.atomsphere.database.AtomsphereAPI;
import com.manywho.services.atomsphere.ServiceConfiguration;

public class GetAssignableRolesCommand implements ActionCommand<ServiceConfiguration, GetAssignableRoles, GetAssignableRoles.Inputs, GetAssignableRoles.Outputs>{

	AuthenticatedWho user;
    @Inject
    public GetAssignableRolesCommand(AuthenticatedWho user) 
    {
    	this.user=user;
    }
    
	@Override
	public ActionResponse<GetAssignableRoles.Outputs> execute(ServiceConfiguration configuration, ServiceRequest request,
			GetAssignableRoles.Inputs input) {
		JSONObject roles=AtomsphereAPI.executeAPI(configuration, user.getToken(), "getAssignableRoles", "POST", null, null, false);

		return new ActionResponse<>(new GetAssignableRoles.Outputs(roles));
	}
}
