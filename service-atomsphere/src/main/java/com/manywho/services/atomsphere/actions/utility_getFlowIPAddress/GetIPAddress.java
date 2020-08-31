package com.manywho.services.atomsphere.actions.utility_getFlowIPAddress;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

@Action.Metadata(name="Get IP Address", summary = "Get Cloud IP Address of Flow Service", uri="/atomsphere/getIPAddress")
public class GetIPAddress {
	public static class Inputs{
	}
	
	public static class Outputs {
		@Action.Output(name="IP Address", contentType=ContentType.String)
		private String ipAddress;
		public Outputs(String ipAddress)
		{
			this.ipAddress=ipAddress;
		}

		public String status() {
			return ipAddress;
		}
	}
}
