package com.manywho.services.atomsphere.actions.downloadProcessLog;

import org.json.JSONObject;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

@Action.Metadata(name="Download Process Log", summary = "Initiate download of the Process log and return a URL for the download", uri="/atomsphere/downloadProcessLog")
public class DownloadProcessLog {
	public static class Inputs{
	    @Action.Input(name = "Execution ID", contentType = ContentType.String)
	    private String executionId;

	    @Action.Input(name = "Log Level - SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST|ALL", contentType = ContentType.String)
	    private String logLevel;

		public String getExecutionId() {
			return executionId;
		}

		public String getLogLevel() {
			return logLevel;
		}
	}
	
	public static class Outputs {
		@Action.Output(name="Status Code", contentType=ContentType.String)
		private String statusCode;

		@Action.Output(name="Status Message", contentType=ContentType.String)
		private String message;

		@Action.Output(name="URL for download (ready when status code = 202)", contentType=ContentType.String)
		private String url;

		public Outputs(JSONObject response)
		{
			this.statusCode=response.getInt("statusCode")+"";
			this.message=response.getString("message");
			this.url=response.getString("url");
		}
		public String getStatusCode() {
			return statusCode;
		}

		public String getMesssage() {
			return message;
		}

		public String getUrl() {
			return url;
		}
	}
}
