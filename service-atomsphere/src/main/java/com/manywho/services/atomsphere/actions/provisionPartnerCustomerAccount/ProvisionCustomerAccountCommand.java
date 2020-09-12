package com.manywho.services.atomsphere.actions.provisionPartnerCustomerAccount;

import javax.inject.Inject;

import org.json.JSONObject;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.atomsphere.database.AtomsphereAPI;
import com.manywho.services.atomsphere.ServiceConfiguration;

public class ProvisionCustomerAccountCommand implements ActionCommand<ServiceConfiguration, ProvisionCustomerAccount, ProvisionCustomerAccount.Inputs, ProvisionCustomerAccount.Outputs>{

	AuthenticatedWho user;
    @Inject
    public ProvisionCustomerAccountCommand(AuthenticatedWho user) 
    {
    	this.user=user;
    }
    
	@Override
	public ActionResponse<ProvisionCustomerAccount.Outputs> execute(ServiceConfiguration configuration, ServiceRequest request,
			ProvisionCustomerAccount.Inputs input) {
		JSONObject body = new JSONObject();
		body.put("atomId", input);

		JSONObject response = AtomsphereAPI.executeAPI(configuration, user.getToken(), "AccountProvision", "POST", "execute", body.toString(), false);

		return new ActionResponse<>(new ProvisionCustomerAccount.Outputs(response));
	}
}
