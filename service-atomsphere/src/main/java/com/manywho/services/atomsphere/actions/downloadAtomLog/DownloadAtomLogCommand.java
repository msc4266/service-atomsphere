package com.manywho.services.atomsphere.actions.downloadAtomLog;

import java.text.SimpleDateFormat;

import javax.inject.Inject;

import org.json.JSONObject;

import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.atomsphere.database.AtomsphereAPI;
import com.manywho.services.atomsphere.ServiceConfiguration;

public class DownloadAtomLogCommand implements ActionCommand<ServiceConfiguration, DownloadAtomLog, DownloadAtomLog.Inputs, DownloadAtomLog.Outputs>{

	AuthenticatedWho user;
    @Inject
    public DownloadAtomLogCommand(AuthenticatedWho user) 
    {
    	this.user=user;
    }
    
	@Override
	public ActionResponse<DownloadAtomLog.Outputs> execute(ServiceConfiguration configuration, ServiceRequest request,
			DownloadAtomLog.Inputs input) {
		JSONObject body = new JSONObject();
		body.put("atomId", input.getAtomId());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		body.put("logDate", simpleDateFormat.format(input.getLogDate()));
		JSONObject response = AtomsphereAPI.executeAPI(configuration, user.getToken(), "AtomLog", "POST", null, body.toString(), false);
		return new ActionResponse<>(new DownloadAtomLog.Outputs(response));
	}
}
