package com.manywho.services.atomsphere.actions.executeProcess;

import org.json.JSONObject;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

@Action.Metadata(name="ExecuteProcess", summary = "Start the specified process", uri="/atomsphere/executeProcess")
public class ExecuteProcess {
	public static class Inputs{
	    @Action.Input(name = "Process Name", contentType = ContentType.String)
	    private String processName;

	    @Action.Input(name = "Process ID", contentType = ContentType.String)
	    private String processId;

	    @Action.Input(name = "Atom Id", contentType = ContentType.String)
	    private String atomId;

		public String getProcessId() {
			return processId;
		}

		public String getProcessName() {
			return processName;
		}

		public String getAtomId() {
			return atomId;
		}
	}
	
	public static class Outputs {
		@Action.Output(name="Status Code", contentType=ContentType.String)
		private String statusCode;

		public Outputs(JSONObject response)
		{
			this.statusCode=response.getString("statusCode");
		}
		public String getStatusCode() {
			return statusCode;
		}
	}
}
