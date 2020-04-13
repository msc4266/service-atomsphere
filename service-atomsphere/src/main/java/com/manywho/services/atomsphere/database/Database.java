package com.manywho.services.atomsphere.database;

import com.google.common.collect.Lists;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.api.CriteriaType;
import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.sdk.api.draw.elements.type.TypeElementProperty;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.ListFilterWhere;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Database implements RawDatabase<ServiceConfiguration> {
    private ServiceMetadata serviceMetadata;
    private boolean doWhitelistOperationSupportedCheck=true;

    private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);


    @Inject
    public Database() throws SAXException, IOException, ParserConfigurationException {
        serviceMetadata = new ServiceMetadata();
    }

    @Override
    public MObject create(ServiceConfiguration configuration, MObject object) {
    	if (this.doWhitelistOperationSupportedCheck && !this.serviceMetadata.supportsCreate(object.getDeveloperName()))
            throw new RuntimeException("Create not supported for " + object.getDeveloperName());
    	JSONObject body = mObjectToJson(object, object.getDeveloperName());
 		JSONObject response = executeAPI(configuration, object.getDeveloperName(), "POST", "", body);
		return jsonToMObject(response, object.getDeveloperName());
    }

    @Override
    public List<MObject> create(ServiceConfiguration configuration, List<MObject> objects) {
        throw new RuntimeException("Bulk Create Not Supported");
    }

    @Override
    public MObject find(ServiceConfiguration configuration, ObjectDataType objectDataType, String id) {
    	//https://api.boomi.com/api/rest/v1/boomi_davehock-T9DOG4/Process/095b2e9f-71ab-43aa-ae4b-9d521b61e0f4
    	if (this.doWhitelistOperationSupportedCheck && !this.serviceMetadata.supportsGet(objectDataType.getDeveloperName()))
            throw new RuntimeException("Get not supported for " + objectDataType.getDeveloperName());
		JSONObject response = executeAPI(configuration, objectDataType.getDeveloperName(), "GET", id, null);
		return jsonToMObject(response, objectDataType.getDeveloperName());
    }

    @Override
    public List<MObject> findAll(ServiceConfiguration configuration, ObjectDataType objectDataType, ListFilter filter) {
    	//https://api.boomi.com/api/rest/v1/boomi_davehock-T9DOG4/Process/query
    	//TODO how can we maintain the queryToken for QueryMore to go beyond 100 items in first page?
    	if (this.doWhitelistOperationSupportedCheck && !this.serviceMetadata.supportsQuery(objectDataType.getDeveloperName()))
            throw new RuntimeException("Query not supported for " + objectDataType.getDeveloperName());
		List<MObject> mObjects = Lists.newArrayList();
		
		JSONObject queryBody = queryBody = new JSONObject();
		if (filter.getWhere()!=null && filter.getWhere().size()>0)
		{
			try {
				TypeElement typeElement = this.serviceMetadata.getTypeElement(objectDataType.getDeveloperName());
	
				
				JSONObject queryFilter = new JSONObject();
				queryBody.put("QueryFilter", queryFilter);
				JSONObject expression = new JSONObject();
				queryFilter.put("expression", expression);
				if (filter.getWhere().size()==1)
				{
					ListFilterWhere where = filter.getWhere().get(0);
					String operator = convertCriteriaType(where.getCriteriaType());
					if (operator == null) throw new RuntimeException("Query operator not supported by Atomsphere API - " + where.getCriteriaType().name());
					expression.put("operator", operator);
					expression.put("property", where.getColumnName());
					JSONArray argument = formatFilterArguments(where, typeElement);
					if (argument.length()>0) //is null and singleton operators
						expression.put("argument", argument);
				}
				else 
				{
					expression.put("operator", "and");
					JSONArray nestedExpressions = new JSONArray();
					expression.put("nestedExpression", nestedExpressions);
					for (ListFilterWhere where : filter.getWhere())
					{
						String operator = convertCriteriaType(where.getCriteriaType());
						if (operator == null) throw new RuntimeException("Query operator not supported by Atomsphere API - " + where.getCriteriaType().name());
						JSONObject nestedExpression = new JSONObject();
						nestedExpressions.put(nestedExpression);
						nestedExpression.put("operator", operator);
						nestedExpression.put("property", where.getColumnName());
						JSONArray argument = formatFilterArguments(where, typeElement);
						if (argument.length()>0) //is null and singleton operators
							nestedExpression.put("argument", argument);
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		
//		{
//			  "QueryFilter" : 
//			    {
//			      "expression" :
//			        {
//			          "operator" : "and",
//			          "nestedExpression" : [
//			            {
//			              "argument" : ["deleted"],
//			              "operator" : "NOT_EQUALS",
//			              "property" : "status"
//			            },
//			            {
//			              "argument" : ["2014-01-01T00:00:00Z","2016-01-01T00:00:00Z"],
//			              "operator" : "BETWEEN",
//			              "property":"dateCreated"
//			            }
//			          ]
//			        }
//			    }
//			}
		
		
		JSONObject response = executeAPI(configuration, objectDataType.getDeveloperName(), "POST", "query", queryBody);
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
    	if (this.doWhitelistOperationSupportedCheck && !this.serviceMetadata.supportsUpdate(object.getDeveloperName()))
            throw new RuntimeException("Update not supported for " + object.getDeveloperName());
    	JSONObject body = mObjectToJson(object, object.getDeveloperName());
		JSONObject response = executeAPI(configuration, object.getDeveloperName(), "POST", object.getExternalId()+"/update", body);
		return jsonToMObject(response, object.getDeveloperName());
    }

    @Override
    public List<MObject> update(ServiceConfiguration configuration, List<MObject> objects) {
        throw new RuntimeException("Bulk Update Not Supported");
    }
    
    @Override
    public void delete(ServiceConfiguration configuration, MObject object) {
    	if (this.doWhitelistOperationSupportedCheck && !this.serviceMetadata.supportsDelete(object.getDeveloperName()))
            throw new RuntimeException("Delete not supported for " + object.getDeveloperName());
		executeAPI(configuration, object.getDeveloperName(), "DELETE", object.getExternalId(), null);
    }

    @Override
    public void delete(ServiceConfiguration configuration, List<MObject> objects) {
        throw new RuntimeException("Bulk Delete Not Supported");
    }

    //Build request body
    JSONObject mObjectToJson(MObject mObject, String entityName)
    {
    	JSONObject body = new JSONObject();
//    	body.put("@type", entityName);
    	for (Property property : mObject.getProperties())
    	{
    		//TODO Datatypes getContentValue only returns String so convert
    		if (property.getContentValue()!=null && property.getContentValue().length()>0)
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
           	if (value.startsWith("[\"Long") || value.startsWith("[\"Double"))
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
		else 
			resource="";
        StringBuffer response= new StringBuffer();
        HttpURLConnection conn=null;
        String responseCode = null;
		
        try {
    		URL url = new URL(String.format("https://api.boomi.com/api/rest/v1/%s/%s%s", configuration.getAccount(), entityName, resource));
    		LOGGER.info(method + " " + url.toString());
            conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(method);
	        conn.setDoOutput(true);
	        conn.setRequestProperty("Content-Type", "application/json");
	        conn.setRequestProperty("Accept", "application/json");
	    	String userpass = configuration.getUsername() + ":" + configuration.getPassword();
	    	String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
	    	conn.setRequestProperty ("Authorization", basicAuth);
	    	if (payload!=null)
	    	{
		        conn.setDoInput(true);
	    		String body = payload.toString();
		        LOGGER.info(body);
		        byte[] input = body.getBytes();
		 		conn.setRequestProperty("Content-Length", ""+input.length);
		        OutputStream os = conn.getOutputStream();
		        os.write(input, 0, input.length);  
	    	}  
	    		
	 
			responseCode = conn.getResponseCode() +"";
	        
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
					responseCode = conn.getResponseCode() +"";
					response.append("Error code: " + conn.getResponseCode());
					response.append(" " + conn.getResponseMessage() + " ");
					BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream())) ;
					String responseLine = null;
					while ((responseLine = br.readLine()) != null) {
					    response.append(responseLine.trim());
					}
					LOGGER.error(response.toString());
					throw new RuntimeException(response.toString());
				} else throw new RuntimeException(e);
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
		}
	    LOGGER.info(response.toString());
        String responseString = response.toString();
        JSONObject responseObj = null;
        if (responseString.length()==0)
        {	
        	responseObj = new JSONObject();
        	responseObj.put("statusCode", responseCode);
        } else {
        	responseObj = new JSONObject(response.toString());
        }
        return responseObj;
	}

	//TODO Unsupported API Operators
	//	LIKE	Like	1
	//	IS_NOT_NULL	Is Not Null	0
	//	BETWEEN	Between	2

	String convertCriteriaType(CriteriaType type)
	{
		String operator = null;
		switch (type)
		{
		case Contains:
			operator="LIKE";
			break;
		case EndsWith:
			break;
		case Equal:
			operator="EQUALS";
			break;
		case GreaterThan:
			operator="GREATER_THAN";
			break;
		case GreaterThanOrEqual:
			operator="GREATER_THAN_OR_EQUAL";
			break;
		case IsEmpty:
			operator="IS_NULL";
			break;
		case LessThan:
			operator="LESS_THAN";
			break;
		case LessThanOrEqual:
			operator="LESS_THAN_OR_EQUAL";
			break;
		case NotEqual:
			operator="NOT_EQUALS";
			break;
		case StartsWith:
			operator="STARTS_WITH";
			break;
		default:
			break;
		}
		return operator;
	}
	
	//TODO
	//2019-11-18T00:00:00.0000000+00:00), expected format: yyyy-MM-dd'T'HH:mm:ss'Z'"}
	JSONArray formatFilterArguments(ListFilterWhere where, TypeElement typeElement) throws ParseException
	{
		JSONArray arguments = new JSONArray();
		//TODO Boolean, Date Number
		if (where.hasContentValue())
		{
			ContentType propertyType = ServiceMetadata.getPropertyType(typeElement, where.getColumnName());
			switch (propertyType)
			{
//			case Boolean:
//				break;
//			case Content:
//				break;
			case DateTime:
				//(2019-11-18T00:00:00.0000000+00:00), expected format: yyyy-MM-dd'T'HH:mm:ss'Z'"}
				final String OLD_FORMAT = "yyyy-MM-dd'T'hh:mm:ss.SSSSSSS";
				final String NEW_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

				SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
				Date d = sdf.parse(where.getContentValue());
				sdf.applyPattern(NEW_FORMAT);
				arguments.put(sdf.format(d));
				break;
//			case Encrypted:
//				break;
//			case List:
//				break;
//			case Number:
//				break;
//			case Object:
//				break;
//			case Password:
//				break;
//			case String:
//				break;
			default:
				arguments.put(where.getContentValue());
				break;		
			}			
		}
		return arguments;
	}

	public boolean isDoWhitelistOperationSupportedCheck() {
		return doWhitelistOperationSupportedCheck;
	}

	public void setDoWhitelistOperationSupportedCheck(boolean doWhitelistOperationSupportedCheck) {
		this.doWhitelistOperationSupportedCheck = doWhitelistOperationSupportedCheck;
	}

}