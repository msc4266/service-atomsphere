package com.manywho.services.atomsphere.database;

import com.google.common.collect.Lists;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.sdk.api.run.elements.type.Property;
import com.manywho.sdk.services.database.RawDatabase;
import com.manywho.services.atomsphere.ServiceConfiguration;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.SAXException;
import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Database implements RawDatabase<ServiceConfiguration> {
    private ServiceMetadata serviceMetadata;

    private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);


    @Inject
    public Database() throws SAXException, IOException, ParserConfigurationException {
        serviceMetadata = new ServiceMetadata();
    }

    @Override
    public MObject create(ServiceConfiguration configuration, MObject object) {
    	if (!this.serviceMetadata.supportsCreate(object.getDeveloperName()))
            throw new RuntimeException("Create not supported for " + object.getDeveloperName());
    	JSONObject body = mObjectToJson(object, object.getDeveloperName());
 		JSONObject response = executeAPI(configuration, object.getDeveloperName(), "POST", null, body);
		return jsonToMObject(response, object.getDeveloperName());
    }

    @Override
    public List<MObject> create(ServiceConfiguration configuration, List<MObject> objects) {
        throw new RuntimeException("Bulk Create Not Supported");
    }

    @Override
    public void delete(ServiceConfiguration configuration, MObject object) {
    	if (!this.serviceMetadata.supportsDelete(object.getDeveloperName()))
            throw new RuntimeException("Delete not supported for " + object.getDeveloperName());
		executeAPI(configuration, object.getDeveloperName(), "DELETE", object.getExternalId(), null);
    }

    @Override
    public void delete(ServiceConfiguration configuration, List<MObject> objects) {
        throw new RuntimeException("Bulk Delete Not Supported");
    }

    @Override
    public MObject find(ServiceConfiguration configuration, ObjectDataType objectDataType, String id) {
    	//https://api.boomi.com/api/rest/v1/boomi_davehock-T9DOG4/Process/095b2e9f-71ab-43aa-ae4b-9d521b61e0f4
    	if (!this.serviceMetadata.supportsGet(objectDataType.getDeveloperName()))
            throw new RuntimeException("Get not supported for " + objectDataType.getDeveloperName());
		JSONObject response = executeAPI(configuration, objectDataType.getDeveloperName(), "GET", id, null);
		return jsonToMObject(response, objectDataType.getDeveloperName());
    }

    @Override
    public List<MObject> findAll(ServiceConfiguration configuration, ObjectDataType objectDataType, ListFilter filter) {
    	//https://api.boomi.com/api/rest/v1/boomi_davehock-T9DOG4/Process/query
    	//TODO how can we maintain the queryToken for QueryMore to go beyond 100 items in first page?
    	if (!this.serviceMetadata.supportsQuery(objectDataType.getDeveloperName()))
            throw new RuntimeException("Query not supported for " + objectDataType.getDeveloperName());
		List<MObject> mObjects = Lists.newArrayList();
		JSONObject response = executeAPI(configuration, objectDataType.getDeveloperName(), "POST", "query", null);
		JSONArray results = response.getJSONArray("result");
		for (int index=filter.getOffset(); index<filter.getOffset()+filter.getLimit() && index<results.length(); index++)
		{				
			JSONObject jObj = (JSONObject) results.get(index);
			mObjects.add(jsonToMObject(jObj, objectDataType.getDeveloperName()));
		}
		return mObjects;
    }

    @Override
    public MObject update(ServiceConfiguration configuration, MObject object) {
    	if (!this.serviceMetadata.supportsUpdate(object.getDeveloperName()))
            throw new RuntimeException("Update not supported for " + object.getDeveloperName());
    	JSONObject body = mObjectToJson(object, object.getDeveloperName());
		JSONObject response = executeAPI(configuration, object.getDeveloperName(), "POST", object.getExternalId(), body);
		return jsonToMObject(response, object.getDeveloperName());
    }

    @Override
    public List<MObject> update(ServiceConfiguration configuration, List<MObject> objects) {
        throw new RuntimeException("Bulk Update Not Supported");
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
       	String primaryKey = serviceMetadata.getPrimaryKey(entityName);
       	if (primaryKey!=null && primaryKey.length()>0)
       		mObject.setExternalId(body.getString(primaryKey));
       	else
       		mObject.setExternalId(UUID.randomUUID().toString());
       	for (String key:body.keySet())
       	{
    		//TODO Datatypes 
           	Property property=null;
           	
           	//TODO field.toString
           	//TODO setContentType(field.getTyoe)
           	
           	//TODO we can do a TypeElement typeElement = getTypeElement(entityName) but that requires a hit on xsd 
           	property = new Property();
           	String value = body.get(key).toString();
           	//TODO Hack to fix long types returned as ["long",####]
           	if (value.startsWith("[\"Long"))
           	{
           		String split[] = value.split(",");
           		if (split.length==2)
           			value = split[1].replace("]", "");
           	}
           		
          	property.setContentValue(value);
 //              	property.setContentType(ContentType.String);
//               	property.setContentType(contentType);
           	property.setDeveloperName(key);
           	properties.add(property);
       	}
    	return mObject;
    }
    
	public static JSONObject executeAPI(ServiceConfiguration configuration, String entityName, String method, String resource, JSONObject payload) 
	{
		if (resource!=null)
			resource="/"+resource;
        StringBuffer response= new StringBuffer();
        HttpURLConnection conn=null;
		
        try {
    		URL url = new URL(String.format("https://api.boomi.com/api/rest/v1/%s/%s%s", configuration.getAccount(), entityName, resource));
    		LOGGER.info(url.toString());
            conn = (HttpURLConnection) url.openConnection();
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
	        
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream())) ;
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
			    response.append(responseLine.trim());
			}
		} catch (ProtocolException e1) {
			throw new RuntimeException(e1);
		} catch (IOException e) {
			try {
				if (conn!=null)
				{
					response.append("Error code: " + conn.getResponseCode());
					response.append(" " + conn.getResponseMessage() + " ");
					BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream())) ;
					String responseLine = null;
					while ((responseLine = br.readLine()) != null) {
					    response.append(responseLine.trim());
					}
					throw new RuntimeException(response.toString());
				} else throw new RuntimeException(e);
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
		}
	    LOGGER.info(response.toString());
		return new JSONObject(response.toString());
	}

}