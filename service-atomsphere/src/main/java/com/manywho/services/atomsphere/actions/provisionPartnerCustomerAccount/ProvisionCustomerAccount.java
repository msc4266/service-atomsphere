package com.manywho.services.atomsphere.actions.provisionPartnerCustomerAccount;

import org.json.JSONObject;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

@Action.Metadata(name="Provision Customer Account", summary = "The Provision Partner Customer Account operation is used by Dell Boomi partners to programmatically provision accounts for their customers as sub-accounts of the partner account", uri="/atomsphere/provisionCustomerAccount")
public class ProvisionCustomerAccount {
	public static class Inputs{
		
		//TODO Product List
//		"product": [  
//		            {  
//		                "productCode": "E_Ent1Yr",
//		                "quantity": 1
//		            },
		
	    @Action.Input(name = "Name", contentType = ContentType.String)
	    private String name;

	    @Action.Input(name = "Street", contentType = ContentType.String)
	    private String street;

	    @Action.Input(name = "City", contentType = ContentType.String)
	    private String city;

	    @Action.Input(name = "State Code", contentType = ContentType.String)
	    private String stateCode;

	    @Action.Input(name = "Country Code", contentType = ContentType.String)
	    private String countryCode;

	    @Action.Input(name = "Status", contentType = ContentType.String)
	    private String status;

		public String getName() {
			return name;
		}

		public String getStreet() {
			return street;
		}

		public String getCity() {
			return city;
		}

		public String getStateCode() {
			return stateCode;
		}

		public String getCountryCode() {
			return countryCode;
		}

		public String getStatus() {
			return status;
		}

	}
	
	public static class Outputs {
		@Action.Output(name="Status", contentType=ContentType.String)
		private String status;
		@Action.Output(name="ID", contentType=ContentType.String)
		private String id;
		
		public Outputs(JSONObject response)
		{
			this.status=response.getString("status");
			this.id=response.getString("id");
		}

		public String getStatus() {
			return status;
		}

		public String getId() {
			return id;
		}

	}
}
