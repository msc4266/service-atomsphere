package com.manywho.services.atomsphere.actions.downloadProcessLog;

import org.json.JSONObject;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.atomsphere.database.Database;
import com.manywho.services.atomsphere.ServiceConfiguration;

public class DownloadProcessLogCommand implements ActionCommand<ServiceConfiguration, DownloadProcessLog, DownloadProcessLog.Inputs, DownloadProcessLog.Outputs>{

	@Override
	public ActionResponse<DownloadProcessLog.Outputs> execute(ServiceConfiguration configuration, ServiceRequest request,
			DownloadProcessLog.Inputs input) {
		JSONObject body = new JSONObject();
		body.put("executionId", input.getExecutionId());
		body.put("logLevel", input.getLogLevel());
		JSONObject response = Database.executeAPI(configuration, "ProcessLog", "POST", null, body);
		return new ActionResponse<>(new DownloadProcessLog.Outputs(response));
	}
}
