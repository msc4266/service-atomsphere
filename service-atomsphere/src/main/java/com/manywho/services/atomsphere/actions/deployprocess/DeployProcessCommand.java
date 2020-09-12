package com.manywho.services.atomsphere.actions.deployprocess;

import javax.inject.Inject;

import org.json.JSONObject;

import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.atomsphere.database.AtomsphereAPI;
import com.manywho.services.atomsphere.ServiceConfiguration;

public class DeployProcessCommand implements ActionCommand<ServiceConfiguration, DeployProcess, DeployProcess.Inputs, DeployProcess.Outputs>{


	AuthenticatedWho user;
    @Inject
    public DeployProcessCommand(AuthenticatedWho user) 
    {
    	this.user=user;
    }
    
	@Override
	public ActionResponse<DeployProcess.Outputs> execute(ServiceConfiguration configuration, ServiceRequest request,
			DeployProcess.Inputs input) {
		String resource = input.getDeploymentId()+"/"+input.getEnvironmentId()+"/"+input.getDigest();
		String listenerStatus = input.getListenerStatus();
		if (listenerStatus!=null && listenerStatus.length()>0)
			resource+="?listenerStatus="+listenerStatus;
		JSONObject status = AtomsphereAPI.executeAPI(configuration, user.getToken(), "deployProcess", "POST", resource, null, false);

		return new ActionResponse<>(new DeployProcess.Outputs(status.toString()));
	}
}
