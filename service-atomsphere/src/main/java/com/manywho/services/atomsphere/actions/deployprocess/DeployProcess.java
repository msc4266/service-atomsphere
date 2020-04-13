package com.manywho.services.atomsphere.actions.deployprocess;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

@Action.Metadata(name="Deploy Process", summary = "Deploy the specified process to an environment", uri="/atomsphere/deployProcess")
public class DeployProcess {
	public static class Inputs{
	    @Action.Input(name = "Deployment ID", contentType = ContentType.String)
	    private String deploymentId;

	    @Action.Input(name = "Environment ID", contentType = ContentType.String)
	    private String environmentId ;

	    @Action.Input(name = "Digest", contentType = ContentType.String)
	    private String digest ;

	    @Action.Input(name = "Listener Status", contentType = ContentType.String)
	    private String listenerStatus ;

		public String getDeploymentId() {
			return deploymentId;
		}

		public String getEnvironmentId() {
			return environmentId;
		}

		public String getDigest() {
			return digest;
		}

		public String getListenerStatus() {
			return listenerStatus;
		}
	}
	
	public static class Outputs {
		@Action.Output(name="Status Code", contentType=ContentType.String)
		private String status;
		public Outputs(String status)
		{
			this.status=status;
		}

		public String status() {
			return status;
		}
	}
}
