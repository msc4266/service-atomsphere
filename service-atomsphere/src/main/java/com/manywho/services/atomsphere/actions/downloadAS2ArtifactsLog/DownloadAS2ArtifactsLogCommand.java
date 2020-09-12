package com.manywho.services.atomsphere.actions.downloadAS2ArtifactsLog;

import java.text.SimpleDateFormat;

import javax.inject.Inject;

import org.json.JSONObject;

import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.atomsphere.database.AtomsphereAPI;
import com.manywho.services.atomsphere.ServiceConfiguration;

public class DownloadAS2ArtifactsLogCommand implements ActionCommand<ServiceConfiguration, DownloadAS2ArtifactsLog, DownloadAS2ArtifactsLog.Inputs, DownloadAS2ArtifactsLog.Outputs>{

	AuthenticatedWho user;
    @Inject
    public DownloadAS2ArtifactsLogCommand(AuthenticatedWho user) 
    {
    	this.user=user;
    }
    
	@Override
	public ActionResponse<DownloadAS2ArtifactsLog.Outputs> execute(ServiceConfiguration configuration, ServiceRequest request,
			DownloadAS2ArtifactsLog.Inputs input) {
		JSONObject body = new JSONObject();
		body.put("atomId", input.getAtomId());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		body.put("logDate", simpleDateFormat.format(input.getLogDate()));
		JSONObject response = AtomsphereAPI.executeAPI(configuration, user.getToken(), "AtomAS2Artifacts", "POST", null, body.toString(), false);
		return new ActionResponse<>(new DownloadAS2ArtifactsLog.Outputs(response));
	}
}
