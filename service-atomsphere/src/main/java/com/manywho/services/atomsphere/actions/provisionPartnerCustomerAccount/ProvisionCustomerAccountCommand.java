package com.manywho.services.atomsphere.actions.provisionPartnerCustomerAccount;

import java.io.IOException;

import org.json.JSONObject;

import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.atomsphere.database.Database;
import com.manywho.services.atomsphere.ServiceConfiguration;

public class ProvisionCustomerAccountCommand implements ActionCommand<ServiceConfiguration, ProvisionCustomerAccount, ProvisionCustomerAccount.Inputs, ProvisionCustomerAccount.Outputs>{

	@Override
	public ActionResponse<ProvisionCustomerAccount.Outputs> execute(ServiceConfiguration configuration, ServiceRequest request,
			ProvisionCustomerAccount.Inputs input) {
		JSONObject body = new JSONObject();
		body.put("atomId", input);

		JSONObject response = Database.executeAPI(configuration, "AccountProvision", "POST", "execute", body);

		return new ActionResponse<>(new ProvisionCustomerAccount.Outputs(response));
	}
}
