package com.manywho.services.atomsphere.actions.executeProcess;

import javax.inject.Inject;

import org.json.JSONArray;
import org.json.JSONObject;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.atomsphere.database.AtomsphereAPI;
import com.manywho.services.atomsphere.ServiceConfiguration;

public class ExecuteProcessCommand implements ActionCommand<ServiceConfiguration, ExecuteProcess, ExecuteProcess.Inputs, ExecuteProcess.Outputs>{

	AuthenticatedWho user;
    @Inject
    public ExecuteProcessCommand(AuthenticatedWho user) 
    {
    	this.user=user;
    }
    
	@Override
	public ActionResponse<ExecuteProcess.Outputs> execute(ServiceConfiguration configuration, ServiceRequest request,
			ExecuteProcess.Inputs input) {
		JSONObject body = new JSONObject();
		if (input.getProcessName()!=null && input.getProcessName().length()>0)
			body.put("processName", input.getProcessName());
		else
			body.put("processId", input.getProcessId());
		body.put("atomId", input.getAtomId());

		JSONObject processProperties = new JSONObject();
		body.put("ProcessProperties", processProperties);
//		processProperties.put("@type", "ProcessProperties");
		JSONArray processPropertyArray = new JSONArray();
		processProperties.put("ProcessProperty", processPropertyArray);
		for (ProcessProperty processProperty: input.getProcessProperties())
		{
			JSONObject prop = new JSONObject();
			prop.put("Name", processProperty.getName());
			prop.put("Value", processProperty.getValue());
//			prop.put("@type", "ProcessProperty");
			processPropertyArray.put(prop);
		}
		
		JSONObject response = AtomsphereAPI.executeAPI(configuration, user.getToken(), "executeProcess", "POST", null, body.toString(), false);
		return new ActionResponse<>(new ExecuteProcess.Outputs(response));
	}
}
