package com.manywho.services.atomsphere.actions.downloadAtomLog;

import java.text.SimpleDateFormat;

import org.json.JSONObject;

import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.atomsphere.database.Database;
import com.manywho.services.atomsphere.ServiceConfiguration;

public class DownloadAtomLogCommand implements ActionCommand<ServiceConfiguration, DownloadAtomLog, DownloadAtomLog.Inputs, DownloadAtomLog.Outputs>{

	@Override
	public ActionResponse<DownloadAtomLog.Outputs> execute(ServiceConfiguration configuration, ServiceRequest request,
			DownloadAtomLog.Inputs input) {
		JSONObject body = new JSONObject();
		body.put("atomId", input.getAtomId());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		body.put("logDate", simpleDateFormat.format(input.getLogDate()));
		JSONObject response = Database.executeAPI(configuration, "AtomLog", "POST", null, body);
		return new ActionResponse<>(new DownloadAtomLog.Outputs(response));
	}
}
