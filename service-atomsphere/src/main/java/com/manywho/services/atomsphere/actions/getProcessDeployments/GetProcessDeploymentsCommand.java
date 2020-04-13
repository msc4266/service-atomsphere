package com.manywho.services.atomsphere.actions.getProcessDeployments;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.google.common.collect.Lists;
import com.manywho.sdk.api.CriteriaType;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.ListFilterWhere;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.sdk.api.run.elements.type.Property;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.atomsphere.database.Database;
import com.manywho.services.atomsphere.ServiceConfiguration;

public class GetProcessDeploymentsCommand implements ActionCommand<ServiceConfiguration, GetProcessDeployments, GetProcessDeployments.Inputs, GetProcessDeployments.Outputs>{
    private static final Logger LOGGER = LoggerFactory.getLogger(GetProcessDeploymentsCommand.class);
	@Override
	public ActionResponse<GetProcessDeployments.Outputs> execute(ServiceConfiguration configuration, ServiceRequest request,
			GetProcessDeployments.Inputs input) {
		JSONObject body = new JSONObject();
		List<ProcessDeployment> processDeployments = Lists.newArrayList();
		try {

			Database database = new Database();
	
			ListFilter filter = new ListFilter();
			filter.setOffset(0);
			filter.setLimit(20);
			ObjectDataType objectDataType = new ObjectDataType();
	
			objectDataType.setDeveloperName("Environment");
			List<MObject> environments = database.findAll(configuration, objectDataType, filter);
	
			objectDataType.setDeveloperName("EnvironmentAtomAttachment");
			List<MObject> environmentAtomAttachments = database.findAll(configuration, objectDataType, filter);
	
			objectDataType.setDeveloperName("Atom");
			List<MObject> atoms = database.findAll(configuration, objectDataType, filter);
	
			filter.setOffset(0);
			filter.setLimit(20);
			ListFilterWhere listFilterWhere = new ListFilterWhere();
			listFilterWhere.setColumnName("processId");
			listFilterWhere.setContentValue(input.getProcessId());
			listFilterWhere.setCriteriaType(CriteriaType.Equal);
			List<ListFilterWhere> whereList = Lists.newArrayList();
			whereList.add(listFilterWhere);
			filter.setWhere(whereList);
	
			objectDataType.setDeveloperName("Deployment");
			List<MObject> deployments = database.findAll(configuration, objectDataType, filter);
	
			for (MObject deployment : deployments)
			{
				String isCurrent = getPropertyStringValue("current", deployment.getProperties());	
				LOGGER.info("isCurrent: "+isCurrent);
				if (isCurrent.contentEquals("true"))
				{
					String environmentId = getPropertyStringValue("environmentId", deployment.getProperties());	
					LOGGER.info("environmentId: " + environmentId);
					String atomId=null;
					for (MObject environmentAtomAttachment : environmentAtomAttachments)
					{			
						if (environmentId.contentEquals(getPropertyStringValue("environmentId",environmentAtomAttachment.getProperties())))
						{
							atomId = getPropertyStringValue("atomId",environmentAtomAttachment.getProperties());
							LOGGER.info("atomId: " + atomId);
							break;
						}
					}
					if (atomId!=null)
					{
						String atomName="";
						for (MObject atom : atoms)
						{			
							if (atomId.contentEquals(getPropertyStringValue("id",atom.getProperties())))
							{
								atomName = getPropertyStringValue("name",atom.getProperties());
								LOGGER.info("atomName: " + atomName);
								break;
							}
						}
						
						String environmentName="";
						for (MObject environment : environments)
						{			
							if (environmentId.contentEquals(getPropertyStringValue("id",environment.getProperties())))
							{
								environmentName = getPropertyStringValue("name",environment.getProperties());
								LOGGER.info("environmentName: " + environmentName);
								break;
							}
						}
		
						ProcessDeployment processDeployment = new ProcessDeployment();
						processDeployment.setGuid(UUID.randomUUID().toString());
						processDeployment.setEnvironmentId(environmentId);
						processDeployment.setAtomId(atomId);
						processDeployment.setAtomName(atomName);
						processDeployment.setEnvironmentName(environmentName);
						processDeployments.add(processDeployment);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return new ActionResponse<>(new GetProcessDeployments.Outputs(processDeployments));
	}
	
	String getPropertyStringValue(String propertyName, List<Property> properties)
	{
		for (Property prop:properties)	
		{
			if (prop.getDeveloperName().contentEquals(propertyName))
				return prop.getContentValue();
		}
		return "";
	}
	
	void findAtomName()
	{
		
	}
}
