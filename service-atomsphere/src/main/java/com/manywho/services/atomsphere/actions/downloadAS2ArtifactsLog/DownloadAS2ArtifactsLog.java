package com.manywho.services.atomsphere.actions.downloadAS2ArtifactsLog;

import java.util.Date;

import org.json.JSONObject;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

@Action.Metadata(name="Download AS2 Artifacts Log", summary = "Initiate download of the AS2 Artifacts log and return a URL for the download", uri="/atomsphere/downloadAS2ArtifactsLog")
public class DownloadAS2ArtifactsLog {
	public static class Inputs{
	    @Action.Input(name = "Atom ID", contentType = ContentType.String)
	    private String atomId;

	    @Action.Input(name = "Log Date", contentType = ContentType.DateTime)
	    private Date logDate;

		public String getAtomId() {
			return atomId;
		}

		public Date getLogDate() {
			return logDate;
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
			this.statusCode=response.getString("statusCode");
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
