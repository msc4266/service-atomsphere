package com.manywho.services.atomsphere.actions.cancelexecution;

import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.atomsphere.database.AtomsphereAPI;
import com.manywho.services.atomsphere.ServiceConfiguration;

public class CancelExecutionCommand implements ActionCommand<ServiceConfiguration, CancelExecution, CancelExecution.Inputs, CancelExecution.Outputs>{

	@Override
	public ActionResponse<CancelExecution.Outputs> execute(ServiceConfiguration configuration, ServiceRequest request,
			CancelExecution.Inputs input) {
		String status = "";
		AtomsphereAPI.executeAPI(configuration, "cancelExecution", "POST", input.getExecutionId(), null);

		return new ActionResponse<>(new CancelExecution.Outputs(status));
	}
}
