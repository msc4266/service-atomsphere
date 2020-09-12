package com.manywho.services.atomsphere.actions.downloadProcessLog;

import javax.inject.Inject;

import org.json.JSONObject;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.atomsphere.database.AtomsphereAPI;
import com.manywho.services.atomsphere.ServiceConfiguration;

public class DownloadProcessLogCommand implements ActionCommand<ServiceConfiguration, DownloadProcessLog, DownloadProcessLog.Inputs, DownloadProcessLog.Outputs>{

	AuthenticatedWho user;
    @Inject
    public DownloadProcessLogCommand(AuthenticatedWho user) 
    {
    	this.user=user;
    }
    
	@Override
	public ActionResponse<DownloadProcessLog.Outputs> execute(ServiceConfiguration configuration, ServiceRequest request,
			DownloadProcessLog.Inputs input) {
		JSONObject body = new JSONObject();
		body.put("executionId", input.getExecutionId());
		body.put("logLevel", input.getLogLevel());
		JSONObject response = AtomsphereAPI.executeAPI(configuration, user.getToken(), "ProcessLog", "POST", null, body.toString(), false);
		return new ActionResponse<>(new DownloadProcessLog.Outputs(response));
	}
}
