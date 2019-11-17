package com.manywho.services.atomsphere.actions.cancelexecution;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

@Action.Metadata(name="Cancel Execution", summary = "Cancel execution of a running process", uri="/atomsphere/cancelExecution")
public class CancelExecution {
	public static class Inputs{
	    @Action.Input(name = "Execution ID", contentType = ContentType.String)
	    private String executionId;

	    public String getExecutionId() {
	        return executionId;
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
