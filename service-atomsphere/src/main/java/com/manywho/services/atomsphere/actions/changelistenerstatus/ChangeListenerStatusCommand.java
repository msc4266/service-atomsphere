package com.manywho.services.atomsphere.actions.changelistenerstatus;

import org.json.JSONObject;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.atomsphere.database.AtomsphereAPI;
import com.manywho.services.atomsphere.ServiceConfiguration;

public class ChangeListenerStatusCommand implements ActionCommand<ServiceConfiguration, ChangeListenerStatus, ChangeListenerStatus.Inputs, ChangeListenerStatus.Outputs>{

	@Override
	public ActionResponse<ChangeListenerStatus.Outputs> execute(ServiceConfiguration configuration, ServiceRequest request,
			ChangeListenerStatus.Inputs input) {

		JSONObject body = new JSONObject();
		body.put("listenerId", input.getListenerId());
		body.put("containerId", input.getContainerId());
		body.put("action", input.getAction());
		JSONObject status = AtomsphereAPI.executeAPI(configuration, "changeListenerStatus", "POST", null, body);

		return new ActionResponse<>(new ChangeListenerStatus.Outputs(status.toString()));
	}
}
