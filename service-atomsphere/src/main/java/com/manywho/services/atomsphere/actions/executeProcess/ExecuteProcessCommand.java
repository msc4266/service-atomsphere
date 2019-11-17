package com.manywho.services.atomsphere.actions.executeProcess;

import org.json.JSONObject;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.atomsphere.database.Database;
import com.manywho.services.atomsphere.ServiceConfiguration;

public class ExecuteProcessCommand implements ActionCommand<ServiceConfiguration, ExecuteProcess, ExecuteProcess.Inputs, ExecuteProcess.Outputs>{

	@Override
	public ActionResponse<ExecuteProcess.Outputs> execute(ServiceConfiguration configuration, ServiceRequest request,
			ExecuteProcess.Inputs input) {
		JSONObject body = new JSONObject();
		body.put("processId", input.getProcessId());
		if (input.getProcessName()!=null && input.getProcessName().length()>0)
			body.put("processName", input.getProcessName());
		else
			body.put("processId", input.getProcessId());
		body.put("atomId", input.getAtomId());
		JSONObject response = Database.executeAPI(configuration, "executeProcess", "POST", null, null);
		return new ActionResponse<>(new ExecuteProcess.Outputs(response));
	}
}
