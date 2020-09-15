package com.manywho.services.atomsphere.database;

import com.google.common.collect.Lists;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.api.CriteriaType;
import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.ListFilter.OrderBy;
import com.manywho.sdk.api.run.elements.type.ListFilter.OrderByDirectionType;
import com.manywho.sdk.api.run.elements.type.ListFilterWhere;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.sdk.api.run.elements.type.Property;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.sdk.services.database.RawDatabase;
import com.manywho.services.atomsphere.ServiceConfiguration;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.SAXException;
import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class Database implements RawDatabase<ServiceConfiguration> {
    private ServiceMetadata serviceMetadata;
    private boolean doWhitelistOperationSupportedCheck=true;
    private Logger logger;
    private AuthenticatedWho user;

    @Inject
    public Database(AuthenticatedWho user) throws SAXException, IOException, ParserConfigurationException {
        serviceMetadata = new ServiceMetadata();
        logger = Logger.getLogger(this.getClass().getName());
        this.user=user;
    }

    @Override
    public MObject create(ServiceConfiguration configuration, MObject object) {
    	if (this.doWhitelistOperationSupportedCheck && !this.serviceMetadata.supportsCreate(object.getDeveloperName()))
            throw new RuntimeException("Create not supported for " + object.getDeveloperName());
    	JSONObject body = mObjectToJson(object, object.getDeveloperName());
 		JSONObject response = AtomsphereAPI.executeAPI(configuration, user.getToken(), object.getDeveloperName(), "POST", "", body.toString(), serviceMetadata.isAPIManagerEntity(object.getDeveloperName()));
		return jsonToMObject(response);
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
		JSONObject response = AtomsphereAPI.executeAPI(configuration, user.getToken(), objectDataType.getDeveloperName(), "GET", id, null, serviceMetadata.isAPIManagerEntity(objectDataType.getDeveloperName()));
		return jsonToMObject(response);
    }

    @Override
    public List<MObject> findAll(ServiceConfiguration configuration, ObjectDataType objectDataType, ListFilter filter) {
    	//https://api.boomi.com/api/rest/v1/boomi_davehock-T9DOG4/Process/query
    	//TODO how can we maintain the queryToken for QueryMore to go beyond 100 items in first page?
    	if (this.doWhitelistOperationSupportedCheck && !this.serviceMetadata.supportsQuery(objectDataType.getDeveloperName()))
            throw new RuntimeException("Query not supported for " + objectDataType.getDeveloperName());
		List<MObject> mObjects = Lists.newArrayList();
		
		JSONObject queryBody = new JSONObject();
		
//		if (this.hasSort(filter) && filter.getOffset()>0)
//			throw new RuntimeException("Offset pagination not supported when sorting is set. Please set your filter 'Number of records to return' to the maximum records required.");
		
		addFilterToBody(filter, objectDataType, queryBody);
		logger.fine("WHERE params: " + queryBody.toString());
		JSONObject response = AtomsphereAPI.executeAPI(configuration, user.getToken(), objectDataType.getDeveloperName(), "POST", "query", queryBody.toString(), serviceMetadata.isAPIManagerEntity(objectDataType.getDeveloperName()));
		if (response.has("result"))
		{
			JSONArray results=response.getJSONArray("result"); 
			//TODO, API does not support offset pagination so flow table attribute pagination=true has no value since all records queried and most are thrown away
//			for (int index=filter.getOffset(); index<filter.getOffset()+filter.getLimit() && index<results.length(); index++)
			
			//If no sort, we iterate with no offset?
			logger.fine("findAll Entity: " + objectDataType.getDeveloperName() + " Limit: " + filter.getLimit() + " Offset: "+ filter.getOffset() + " numberOfResults: "+response.getInt("numberOfResults") + " size:" + results.length());
			int totalRecords=0;
			int maxRecords=filter.getOffset()+filter.getLimit();
			if (this.hasSort(filter))
				maxRecords=5000;//we allow up to 5000 records to allow for sorting
			for (int index=0; index<results.length() && totalRecords<maxRecords; index++)
			{				
				JSONObject jObj = (JSONObject) results.get(index);
				mObjects.add(jsonToMObject(jObj));
				totalRecords++;
			}
			while (response.has("queryToken") && totalRecords<maxRecords)
			{
				response = AtomsphereAPI.executeAPIQueryMore(configuration, user.getToken(), objectDataType.getDeveloperName(), response.getString("queryToken"), this.serviceMetadata.isAPIManagerEntity(objectDataType.getDeveloperName()));
				results=response.getJSONArray("result");
				logger.fine("findAll queryToken Entity: " + objectDataType.getDeveloperName() + " Limit: " + filter.getLimit() + " Offset: "+ filter.getOffset() + " numberOfResults: "+response.getInt("numberOfResults") + " size:" + results.length());
				for (int index=0; index<results.length() && totalRecords<maxRecords; index++)
				{				
					JSONObject jObj = (JSONObject) results.get(index);
					mObjects.add(jsonToMObject(jObj));
					totalRecords++;
				}
			}
		}
		
		this.sortMObjects(mObjects, filter);
		//Truncate 
		List<MObject> returnMObjects = Lists.newArrayList();
		if (filter.getOffset()>0)
		{
			for (int i=filter.getOffset(); i<mObjects.size() && i < (filter.getOffset() + filter.getLimit());i++)
			{
				returnMObjects.add(mObjects.get(i));
			}
		} else {
			returnMObjects = mObjects;
		}
		return returnMObjects;
    }
    
    private void addFilterToBody(ListFilter filter, ObjectDataType objectDataType, JSONObject queryBody)
    {
		if (filter.getWhere()!=null && filter.getWhere().size()>0)
		{
			JSONObject queryFilter = new JSONObject();
			JSONObject expression = new JSONObject();
			queryFilter.put("expression", expression);
			try {
				TypeElement typeElement = this.serviceMetadata.findTypeElement(objectDataType.getDeveloperName());
					
				if (filter.getWhere().size()==1)
				{
					ListFilterWhere where = filter.getWhere().get(0);
					String operator = convertCriteriaType(where.getCriteriaType());
					if (operator == null) throw new RuntimeException("Query operator not supported by Atomsphere API - " + where.getCriteriaType().name());
					expression.put("operator", operator);
					expression.put("property", where.getColumnName());
					JSONArray argument = formatFilterArguments(where, typeElement);
					if (argument.length()>0 && !"%".contentEquals((String)argument.get(0))) //is null and singleton operators
						expression.put("argument", argument);
				}
				else 
				{
					expression.put("operator", filter.getComparisonType().toString().toLowerCase());
					JSONArray nestedExpressions = new JSONArray();
					for (ListFilterWhere where : filter.getWhere())
					{
						String operator = convertCriteriaType(where.getCriteriaType());
						if (operator == null) throw new RuntimeException("Query operator not supported by Atomsphere API - " + where.getCriteriaType().name());
						JSONObject nestedExpression = new JSONObject();
						nestedExpression.put("operator", operator);
						nestedExpression.put("property", where.getColumnName());
						JSONArray argument = formatFilterArguments(where, typeElement);
						if (argument.length()>0) //is null and singleton operators
							nestedExpression.put("argument", argument);
						logger.fine(argument.toString());
						//If it is % we will not add the expression. This trick deals with the fact the flow boolean expressions are static in the UI
						if (argument.length()!=1 || !(argument.get(0) instanceof String) || !"%".contentEquals((String)argument.get(0)))
							nestedExpressions.put(nestedExpression);
					}
					if (nestedExpressions.length()>0)
						expression.put("nestedExpression", nestedExpressions);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			if (expression.has("nestedExpression") || expression.has("argument"))
				queryBody.put("QueryFilter", queryFilter);
		}
    }
    
    private boolean hasSort(ListFilter filter)
    {
    	if (filter.getOrderBy()!=null && filter.getOrderBy().size()>0)
    		return true;
    	if (filter.hasOrderByPropertyDeveloperName())
    		return true;
    	return false;
    }
    
    private void sortMObjects(List<MObject> mObjects, ListFilter filter)
    {
		//Atomsphere doesn't support sorting so we will do a poor person's in memory sort of a single page of data. Only good for a single page/request of a data set
    	if (!this.hasSort(filter))
    		return;
		if (filter.getOrderBy()!=null && filter.getOrderBy().size()>0)
		{
			logger.fine("Order By:" + filter.getOrderBy().size());
			mObjects.sort(new Comparator<MObject>() {
			    @Override
			    public int compare(MObject m1, MObject m2) {
			    	for (OrderBy orderBy:filter.getOrderBy())
			    	{
			    		int result = CompareObjects(m1, m2, orderBy.getColumnName(), orderBy.getDirection());
			    		if (result!=0)
			    			return result;
			    	}
			    	return 0;
			    }
			});
		} else if (filter.hasOrderByPropertyDeveloperName()) {
			logger.fine("Order By:" + filter.getOrderByPropertyDeveloperName());
			mObjects.sort(new Comparator<MObject>() {
			    @Override
			    public int compare(MObject m1, MObject m2) {
			    	String direction = "";
			    	if (filter.getOrderByDirectionType()!=null)
			    		direction=filter.getOrderByDirectionType().toString();
		    		return CompareObjects(m1, m2, filter.getOrderByPropertyDeveloperName(), direction);
			    }
			});
		}
    }
    
    int CompareObjects(MObject m1, MObject m2, String developerName, String direction)
    {
		Property p1 = findPropertyByColumnName(m1, developerName);
		Property p2 = findPropertyByColumnName(m2, developerName);
		if (p1!=null && p2!=null && p1.getContentValue()!=null && !p1.getContentValue().contentEquals(p2.getContentValue()))
		{
	    	int result=0;
			switch (p1.getContentType()) 
			{
			case Number:
				Double f1 = Double.parseDouble(p1.getContentValue()); 
				Double f2 = Double.parseDouble(p2.getContentValue());
//				logger.warning("f1 f2:" + f1 + " " + f2);
				if (f1>f2) 
					result = 1;
				else 
					result = -1;
				break;
			default:
				result = p1.getContentValue().compareToIgnoreCase(p2.getContentValue());
			}
			if (direction==OrderByDirectionType.Descending.toString())
				result*=-1;
//			logger.warning("Direction:" + direction + " " + result);
			return result;
		}
    	return 0;    	
    }
    
    Property findPropertyByColumnName(MObject mObject, String propName)
    {
    	if (mObject!=null && mObject.getProperties()!=null)
    	{
    		for (Property aProp:mObject.getProperties())
    		{
    			if (aProp.getDeveloperName().contentEquals(propName))
    				return aProp;
    		}
    	}
    	return null;
    }

    @Override
    public MObject update(ServiceConfiguration configuration, MObject object) {
    	if (this.doWhitelistOperationSupportedCheck && !this.serviceMetadata.supportsUpdate(object.getDeveloperName()))
            throw new RuntimeException("Update not supported for " + object.getDeveloperName());
    	JSONObject body = mObjectToJson(object, object.getDeveloperName());
		JSONObject response = AtomsphereAPI.executeAPI(configuration, user.getToken(), object.getDeveloperName(), "POST", object.getExternalId()+"/update", body.toString(), serviceMetadata.isAPIManagerEntity(object.getDeveloperName()));
		return jsonToMObject(response);
    }

    @Override
    public List<MObject> update(ServiceConfiguration configuration, List<MObject> objects) {
        throw new RuntimeException("Bulk Update Not Supported");
    }
    
    @Override
    public void delete(ServiceConfiguration configuration, MObject object) {
    	if (this.doWhitelistOperationSupportedCheck && !this.serviceMetadata.supportsDelete(object.getDeveloperName()))
            throw new RuntimeException("Delete not supported for " + object.getDeveloperName());
    	AtomsphereAPI.executeAPI(configuration, user.getToken(), object.getDeveloperName(), "DELETE", object.getExternalId(), null, serviceMetadata.isAPIManagerEntity(object.getDeveloperName()));
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
    
    void addMObjectProperties(MObject mObject, JSONObject body)
    {
    	List<Property> properties = mObject.getProperties();
       	for (String key:body.keySet())
       	{
       		if (!"@type".contentEquals(key))
       		{
               	Property property=null;
               	property = new Property();
               	property.setDeveloperName(key);
               	Object propObject = body.get(key);
               	String value = propObject.toString();
           		if (propObject instanceof JSONObject)
           		{
           			JSONObject propertyObject = (JSONObject) propObject;
           			String type = propertyObject.getString("@type");
           			TypeElement typeElement = serviceMetadata.findTypeElement(type);
           			if (typeElement!=null) //TODO Account.licensing has @type "" but has for objects inside that must be iterated
           			{
               			property.setContentType(ContentType.Object);
                       	property.setTypeElementPropertyId(typeElement.getId());
               			MObject childMObject = jsonToMObject((JSONObject)propObject);
               			List<MObject> list = new ArrayList<MObject>();
               			list.add(childMObject);
               			property.setObjectData(list);          			
                       	properties.add(property);           			
           			} else {
           				//iterate objects and add each as a property
           				this.addMObjectProperties(mObject, propertyObject);
           			}
           		}         	
           		else if (propObject instanceof JSONArray && !isNumber(value))
           		{
           			JSONArray array = (JSONArray) propObject;
           			List<MObject> list = new ArrayList<MObject>();
           			property.setContentType(ContentType.List);
           			
           			for (int i=0; i<array.length(); i++)
           			{
           				MObject item = jsonToMObject(array.getJSONObject(i));
           				list.add(item);
           			}
           			property.setObjectData(list);
                   	properties.add(property);
           		}
           		else
           		{
               		ContentType contentType = ContentType.String;
               		//TODO ContentType.DateTime
                   	//TODO we can do a TypeElement typeElement = getTypeElement(entityName) but that requires a hit on xsd 
                   	
                   	//TODO Hack to fix long types returned as ["long",####]
                   	if (isNumber(value))
                   	{
                   		contentType = ContentType.Number;
                   		String split[] = value.split(",");
                   		if (split.length==2)
                   			value = split[1].replace("]", "");
                   		else
                   			value="0";
                   	} else if (propObject instanceof Boolean){
                   		contentType = ContentType.Boolean;
                   	}
                   	
                  	property.setContentValue(value);
                  	property.setContentType(contentType);
//                   	logger.fine(String.format("Property key: %s value %s type: %s", key, value, contentType));
                   	properties.add(property);
           		}
       		}
       	}
    }
    
    boolean isNumber(String value)
    {
    	return value.startsWith("[\"Long") || value.startsWith("[\"Double"); 
    }
    
    //Process Response
    MObject jsonToMObject(JSONObject body)
    {
    	MObject mObject = new MObject();
//    	logger.fine(body.toString());
    	
    	List<Property> properties = Lists.newArrayList();
       	mObject.setProperties(properties);
       	
       	String typeName = body.getString("@type");
       	mObject.setDeveloperName(typeName);

       	mObject.setExternalId(UUID.randomUUID().toString());
       	String primaryKey = serviceMetadata.getPrimaryKey(typeName);
       	if (primaryKey!=null && primaryKey.length()>0)
       		mObject.setExternalId(body.getString(primaryKey));       		

       	addMObjectProperties(mObject, body);

    	return mObject;
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
			case Number:
//				arguments.put("Long");
				arguments.put(Long.parseLong(where.getContentValue()));
				break;
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