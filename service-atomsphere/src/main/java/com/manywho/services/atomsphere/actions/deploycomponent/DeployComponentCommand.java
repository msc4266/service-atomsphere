package com.manywho.services.atomsphere.actions.deploycomponent;

import org.json.JSONObject;

import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.atomsphere.database.Database;
import com.manywho.services.atomsphere.ServiceConfiguration;

public class DeployComponentCommand implements ActionCommand<ServiceConfiguration, DeployComponent, DeployComponent.Inputs, DeployComponent.Outputs>{

	@Override
	public ActionResponse<DeployComponent.Outputs> execute(ServiceConfiguration configuration, ServiceRequest request,
			DeployComponent.Inputs input) {
		String resource = input.getDeploymentId()+"/"+input.getEnvironmentId()+"/"+input.getDigest();
		String listenerStatus = input.getListenerStatus();
		if (listenerStatus!=null && listenerStatus.length()>0)
			resource+="?listenerStatus="+listenerStatus;
		JSONObject status = Database.executeAPI(configuration, "deployComponent", "POST", resource, null);

		return new ActionResponse<>(new DeployComponent.Outputs(status.toString()));
	}
}
