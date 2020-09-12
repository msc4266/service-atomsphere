package com.manywho.services.atomsphere.actions.changelistenerstatus;

import javax.inject.Inject;

import org.json.JSONObject;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.atomsphere.database.AtomsphereAPI;
import com.manywho.services.atomsphere.ServiceConfiguration;

public class ChangeListenerStatusCommand implements ActionCommand<ServiceConfiguration, ChangeListenerStatus, ChangeListenerStatus.Inputs, ChangeListenerStatus.Outputs>{

	AuthenticatedWho user;
    @Inject
    public ChangeListenerStatusCommand(AuthenticatedWho user) 
    {
    	this.user=user;
    }
    
	@Override
	public ActionResponse<ChangeListenerStatus.Outputs> execute(ServiceConfiguration configuration, ServiceRequest request,
			ChangeListenerStatus.Inputs input) {

		JSONObject body = new JSONObject();
		body.put("listenerId", input.getListenerId());
		body.put("containerId", input.getContainerId());
		body.put("action", input.getAction());
		JSONObject status = AtomsphereAPI.executeAPI(configuration, user.getToken(), "changeListenerStatus", "POST", null, body.toString(), false);

		return new ActionResponse<>(new ChangeListenerStatus.Outputs(status.toString()));
	}
}
