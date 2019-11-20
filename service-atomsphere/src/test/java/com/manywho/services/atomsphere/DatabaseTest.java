package com.manywho.services.atomsphere;

import static org.junit.Assert.*;

import java.util.List;
import org.junit.Test;
import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.services.atomsphere.database.Database;
import com.manywho.services.atomsphere.database.ServiceMetadata;

public class DatabaseTest {

	ServiceConfiguration configuration;
	private void init()
	{
		configuration = new ServiceConfiguration();
		configuration.setUsername("dave.hock@dell.com");
		configuration.setPassword("Gopack!24");
		configuration.setAccount("boomi_davehock-T9DOG4");
	}
	//TODO Generate edit mobject request payloads from metadata
	@Test
	public void testAllCRUD() throws Exception {
		init();
		ServiceMetadata serviceMetadata = new ServiceMetadata();
		Database database = new Database();
		database.setDoWhitelistOperationSupportedCheck(true); //if false we will let all operations be attempted irregardless of the whitelist supportsXXX entries in order to test whitelists
		List<TypeElement> typeElements=serviceMetadata.getAllTypesMetadata();
		for (TypeElement typeElement:typeElements)
		{
			Thread.sleep(500);
			ObjectDataType objectDataType = new ObjectDataType();
			objectDataType.setDeveloperName(typeElement.getDeveloperName());
			
			//CREATE
			if (!database.isDoWhitelistOperationSupportedCheck() || serviceMetadata.supportsCreate(typeElement.getDeveloperName()))
			{
				try {
					MObject createObject = new MObject();
					createObject.setDeveloperName(typeElement.getDeveloperName());
					createObject.setExternalId("FAKEID");
					MObject object = database.create(configuration, createObject);
				} catch (Exception e) {
					//Ignore unsupported operation errors but report documentation errors 
					if (e.toString().contains("Unknown objectType for create"))
					{
						if(serviceMetadata.supportsCreate(typeElement.getDeveloperName()))
							System.out.println("********"+"Documentation incorrect - " + typeElement.getDeveloperName() + " does not support CREATE");
					}
					else System.out.println("****CREATE - "+typeElement.getDeveloperName() + " - " + e.getMessage());
				}
			}
			
			//QUERY
			if (!database.isDoWhitelistOperationSupportedCheck() || serviceMetadata.supportsQuery(typeElement.getDeveloperName()))
			{
				ListFilter filter = new ListFilter();
				filter.setOffset(0);
				filter.setLimit(20);
				try {
					List<MObject> objects = database.findAll(configuration, objectDataType, filter);
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
