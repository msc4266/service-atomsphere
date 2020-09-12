package com.manywho.services.atomsphere.actions.cancelexecution;

import javax.inject.Inject;

import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.atomsphere.database.AtomsphereAPI;
import com.manywho.services.atomsphere.ServiceConfiguration;

public class CancelExecutionCommand implements ActionCommand<ServiceConfiguration, CancelExecution, CancelExecution.Inputs, CancelExecution.Outputs>{

	AuthenticatedWho user;
    @Inject
    public CancelExecutionCommand(AuthenticatedWho user) 
    {
    	this.user=user;
    }
    
	@Override
	public ActionResponse<CancelExecution.Outputs> execute(ServiceConfiguration configuration, ServiceRequest request,
			CancelExecution.Inputs input) {
		String status = "";
		AtomsphereAPI.executeAPI(configuration, user.getToken(), "cancelExecution", "POST", input.getExecutionId(), null, false);

		return new ActionResponse<>(new CancelExecution.Outputs(status));
	}
}
