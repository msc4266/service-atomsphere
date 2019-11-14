package com.manywho.services.atomsphere;

import com.google.common.collect.Lists;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.sdk.api.run.elements.type.Property;
import com.manywho.sdk.services.database.RawDatabase;
import com.manywho.services.atomsphere.ServiceConfiguration;

import org.json.JSONObject;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;

public class Database implements RawDatabase<ServiceConfiguration> {
    private ServiceMetadata serviceMetadata;
    private Logger logger;

//    private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);


    @Inject
    public Database() throws SAXException, IOException, ParserConfigurationException {
        serviceMetadata = new ServiceMetadata();
        logger = Logger.getLogger(this.getClass().getName());
    }

    @Override
    public MObject create(ServiceConfiguration configuration, MObject object) {
    	//https://api.boomi.com/partner/api/rest/v1/accountId/Account/
        throw new RuntimeException("Not Supported");
    }

    @Override
    public List<MObject> create(ServiceConfiguration configuration, List<MObject> objects) {
        throw new RuntimeException("Not Supported");
    }

    @Override
    public void delete(ServiceConfiguration configuration, MObject object) {
    	if (!this.serviceMetadata.supportsDelete(object.getDeveloperName()))
            throw new RuntimeException("Not Supported");
    	try {
			this.executeAPI(configuration, object.getDeveloperName(), "DELETE", object.getExternalId(), null);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }

    @Override
    public void delete(ServiceConfiguration configuration, List<MObject> objects) {
        throw new RuntimeException("Not Supported");
    }

    @Override
    public MObject find(ServiceConfiguration configuration, ObjectDataType objectDataType, String id) {
    	//https://api.boomi.com/api/rest/v1/boomi_davehock-T9DOG4/Process/095b2e9f-71ab-43aa-ae4b-9d521b61e0f4
    	if (!this.serviceMetadata.supportsGet(objectDataType.getDeveloperName()))
            throw new RuntimeException("Not Supported");
    	try {
			JSONObject response = this.executeAPI(configuration, objectDataType.getDeveloperName(), "POST", id, null);
			return jsonToMObject(response, objectDataType.getDeveloperName());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
//        throw new RecordNotFoundException();
    }

    @Override
    public List<MObject> findAll(ServiceConfiguration configuration, ObjectDataType objectDataType, ListFilter filter) {
    	//https://api.boomi.com/api/rest/v1/boomi_davehock-T9DOG4/Process/query
    	if (!this.serviceMetadata.supportsQuery(objectDataType.getDeveloperName()))
            throw new RuntimeException("Not Supported");
    	try {
    		List<MObject> mObjects = Lists.newArrayList();
			JSONObject response = this.executeAPI(configuration, objectDataType.getDeveloperName(), "POST", "query", null);
			for (Object obj : response.getJSONArray("result"))
			{
				JSONObject jObj = (JSONObject) obj;
				mObjects.add(jsonToMObject(jObj, objectDataType.getDeveloperName()));
			}
			return mObjects;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }

    @Override
    public MObject update(ServiceConfiguration configuration, MObject object) {
    	if (!this.serviceMetadata.supportsUpdate(object.getDeveloperName()))
            throw new RuntimeException("Not Supported");
    	//https://api.boomi.com/api/rest/v1/boomi_davehock-T9DOG4/Process/095b2e9f-71ab-43aa-ae4b-9d521b61e0f4
        throw new RuntimeException("Not Supported");
    }

    @Override
    public List<MObject> update(ServiceConfiguration configuration, List<MObject> objects) {
        throw new RuntimeException("Not Supported");
    }
    
    //Build request body
    JSONObject mObjectToJson(MObject mObject, String entityName)
    {
    	JSONObject body = new JSONObject();
    	body.put("@type", entityName);
    	for (Property property : mObject.getProperties())
    	{
    		//TODO Datatypes getContentValue only returns String so convert
    		body.put(property.getDeveloperName(), property.getContentValue());
    	}
    	return body;
    }
    
    //Process Response
    MObject jsonToMObject(JSONObject body, String entityName)
    {
    	MObject mObject = new MObject();
    	
    	List<Property> properties = Lists.newArrayList();
       	mObject.setProperties(properties);
       	mObject.setDeveloperName(entityName);
       	serviceMetadata.getPrimaryKey(entityName);
       	mObject.setExternalId(body.getString(serviceMetadata.getPrimaryKey(entityName)));
       	for (String key:body.keySet())
       	{
    		//TODO Datatypes 
           	Property property = new Property();
           	Object field = body.get(key);
           	
           	//TODO field.toString
           	//TODO setContentType(field.getTyoe)
           	
           	
           	if (field instanceof String)
           	{
               	property.setContentValue(body.getString(key));
               	property.setDeveloperName(key);
 //              	property.setContentType(ContentType.String);
//               	property.setContentType(contentType);
               	properties.add(property);
           	}
       	}
    	return mObject;
    }
    
	public JSONObject executeAPI(ServiceConfiguration configuration, String entityName, String method, String resource, JSONObject payload) throws IOException
	{
		String operation = "query";
		URL url = new URL(String.format("https://api.boomi.com/api/rest/v1/%s/%s/%s", configuration.getAccount(), entityName, resource));
		logger.info(url.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
    	String userpass = configuration.getUsername() + ":" + configuration.getPassword();
    	String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
    	conn.setRequestProperty ("Authorization", basicAuth);
        String str = "".toString();
        byte[] input = str.getBytes();
 		conn.setRequestProperty("Content-Length", ""+input.length);
 
        OutputStream os = conn.getOutputStream();
        os.write(input, 0, input.length);  
        
        StringBuffer response= new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream())) ;
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
			    response.append(responseLine.trim());
			}
//	    System.out.println(response.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			conn.getResponseCode();
			conn.getResponseMessage();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream())) ;
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
			    response.append(responseLine.trim());
			}
			throw new IOException(response.toString());
		}
	    logger.info(response.toString());
		return new JSONObject(response.toString());
	}

}