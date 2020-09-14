package com.manywho.services.atomsphere;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.List;
import com.google.common.collect.Lists;
import com.manywho.sdk.api.CriteriaType;
import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.ListFilterWhere;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.services.TestUtil;
import com.manywho.services.atomsphere.database.Database;
import com.manywho.services.atomsphere.database.ServiceMetadata;

public class DatabaseTest {

	ServiceConfiguration configuration;
	AuthenticatedWho user;

	private void init() throws JSONException, Exception
	{
		JSONObject testCredentials=new JSONObject(TestUtil.readResource("testCredentials.json", this.getClass()));
		configuration = new ServiceConfiguration();
		configuration.setAccount(testCredentials.getString("accountId"));
		configuration.setUsername(testCredentials.getString("username"));
		configuration.setPassword(testCredentials.getString("password"));
		user = new AuthenticatedWho();
	}
	//TODO Generate edit mobject request payloads from metadata
	@Test
	public void testAllCRUD() throws Exception {
		init();
		ServiceMetadata serviceMetadata = new ServiceMetadata();
		Database database = new Database(user);
		database.setDoWhitelistOperationSupportedCheck(true); //if false we will let all operations be attempted irregardless of the whitelist supportsXXX entries in order to test whitelists
		List<TypeElement> typeElements=serviceMetadata.getAllTypeElements();
		for (TypeElement typeElement:typeElements)
		{
			ObjectDataType objectDataType = new ObjectDataType();
			objectDataType.setDeveloperName(typeElement.getDeveloperName());
			
			//CREATE
			if (!database.isDoWhitelistOperationSupportedCheck() || serviceMetadata.supportsCreate(typeElement.getDeveloperName()))
			{
				boolean success=true;
				try {
					MObject createObject = new MObject();
					createObject.setDeveloperName(typeElement.getDeveloperName());
					createObject.setExternalId("FAKEID");
					MObject object = database.create(configuration, createObject);
				} catch (Exception e) {
					success=false;
					//Ignore unsupported operation errors but report documentation errors 
					if (e.toString().contains("Unknown objectType for create"))
					{
						if(serviceMetadata.supportsCreate(typeElement.getDeveloperName()))
							System.out.println("********"+"Documentation incorrect - " + typeElement.getDeveloperName() + " does not support CREATE");
					}
					else System.out.println("****CREATE - "+typeElement.getDeveloperName() + " - " + e.getMessage());
				}
				if (success)
					System.out.println("CREATE SUCCESS");
			}
			int xxxx=0;
			if (typeElement.getDeveloperName().contentEquals("Process"))
				xxxx=1;

			//QUERY
			if (!database.isDoWhitelistOperationSupportedCheck() || serviceMetadata.supportsQuery(typeElement.getDeveloperName()))
			{
				ListFilter filter = new ListFilter();
				filter.setOffset(0);
				filter.setLimit(20);
				ListFilterWhere listFilterWhere = new ListFilterWhere();
				listFilterWhere.setColumnName("name");
				listFilterWhere.setContentValue("(sub)");
				listFilterWhere.setCriteriaType(CriteriaType.Contains);
				List<ListFilterWhere> whereList = Lists.newArrayList();
				whereList.add(listFilterWhere);
				if (typeElement.getDeveloperName().contentEquals("Process"))
					filter.setWhere(whereList);
				try {
					List<MObject> objects = database.findAll(configuration, objectDataType, filter);
					System.out.println("****QUERY - "+typeElement.getDeveloperName() + " - " + objects.size());
				} catch (Exception e) {
					//Ignore unsupported operation errors but report documentation errors 
					if (e.toString().contains("Unknown objectType for query"))
					{
						if(serviceMetadata.supportsQuery(typeElement.getDeveloperName()))
							System.out.println("********"+"Documentation incorrect - " + typeElement.getDeveloperName() + " does not support QUERY");
					}
					else System.out.println("****QUERY - "+typeElement.getDeveloperName() + " - " + e.getMessage());
				}
			}
			
			//GET
			if (!database.isDoWhitelistOperationSupportedCheck() || serviceMetadata.supportsGet(typeElement.getDeveloperName()))
			{
				try {
					MObject object = database.find(configuration, objectDataType, "FAKEID");
				} catch (Exception e) {
					//Ignore unsupported operation errors but report documentation errors 
					if (e.toString().contains("Unknown objectType for query"))
					{
						if(serviceMetadata.supportsGet(typeElement.getDeveloperName()))
							System.out.println("********"+"Documentation incorrect - " + typeElement.getDeveloperName() + " does not support GET");
					}
					else System.out.println("****GET - "+typeElement.getDeveloperName() + " - " + e.getMessage());
				}
			}

			//UPDATE
			if (!database.isDoWhitelistOperationSupportedCheck() || serviceMetadata.supportsUpdate(typeElement.getDeveloperName()))
			{
				try {
					MObject updateObject = new MObject();
					updateObject.setDeveloperName(typeElement.getDeveloperName());
					updateObject.setExternalId("FAKEID");
					MObject object = database.update(configuration, updateObject);
				} catch (Exception e) {
					//Ignore unsupported operation errors but report documentation errors 
					if (e.toString().contains("Unknown objectType for update"))
					{
						if(serviceMetadata.supportsUpdate(typeElement.getDeveloperName()))
							System.out.println("********"+"Documentation incorrect - " + typeElement.getDeveloperName() + " does not support UPDATE");
					}
					else 
						System.out.println("****UPDATE - "+typeElement.getDeveloperName() + " - " + e.getMessage());
				}
			}

			//DELETE
			if (!database.isDoWhitelistOperationSupportedCheck() || serviceMetadata.supportsDelete(typeElement.getDeveloperName()))
			{
				try {
					MObject deleteObject = new MObject();
					deleteObject.setDeveloperName(typeElement.getDeveloperName());
					deleteObject.setExternalId("FAKEID");
					database.delete(configuration, deleteObject);
				} catch (Exception e) {
					//Ignore unsupported operation errors but report documentation errors 
					if (e.toString().contains("Unknown objectType for delete"))
					{
						if(serviceMetadata.supportsDelete(typeElement.getDeveloperName()))
							System.out.println("********"+"Documentation incorrect - " + typeElement.getDeveloperName() + " does not support DELETE");
					}
					else System.out.println("****DELETE - "+typeElement.getDeveloperName() + " - " + e.getMessage());
//						assertEquals(e.getMessage(),"Error code: 400 Bad Request {\"@type\":\"Error\",\"message\":\"The component (FAKEID) is not found.\"}");
					
				}
			}
		}
		assertTrue(typeElements.size()>0);
	}	
}
