package com.manywho.services.atomsphere.actions.getAssignableRoles;

import org.json.JSONObject;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.atomsphere.database.AtomsphereAPI;
import com.manywho.services.atomsphere.ServiceConfiguration;

public class GetAssignableRolesCommand implements ActionCommand<ServiceConfiguration, GetAssignableRoles, GetAssignableRoles.Inputs, GetAssignableRoles.Outputs>{

	@Override
	public ActionResponse<GetAssignableRoles.Outputs> execute(ServiceConfiguration configuration, ServiceRequest request,
			GetAssignableRoles.Inputs input) {
		JSONObject roles=AtomsphereAPI.executeAPI(configuration, "getAssignableRoles", "POST", null, null);

		return new ActionResponse<>(new GetAssignableRoles.Outputs(roles));
	}
}
