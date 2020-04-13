package com.manywho.services.atomsphere.actions.downloadAtomLog;

import java.util.Date;

import org.json.JSONObject;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

@Action.Metadata(name="Download Atom Log", summary = "Initiate download of the Atom log and return a URL for the download", uri="/atomsphere/downloadAtomLog")
public class DownloadAtomLog {
	public static class Inputs{
	    @Action.Input(name = "Atom ID", contentType = ContentType.String)
	    private String atomId;

	    @Action.Input(name = "Log Date", contentType = ContentType.DateTime)
	    private Date logDate;

	    @Action.Input(name = "Include Binary Files", contentType = ContentType.Boolean)
	    private Boolean includeBin;

		public String getAtomId() {
			return atomId;
		}

		public Date getLogDate() {
			return logDate;
		}

		public Boolean getIncludeBin() {
			return includeBin;
		}
	}
	
	public static class Outputs {
		@Action.Output(name="Status Code", contentType=ContentType.Number)
		private int statusCode;

		@Action.Output(name="Status Message", contentType=ContentType.String)
		private String message;

		@Action.Output(name="URL for download (ready when status code = 202)", contentType=ContentType.String)
		private String url;

		public Outputs(JSONObject response)
		{
			this.statusCode=response.getInt("statusCode");
			this.message=response.getString("message");
			this.url=response.getString("url");
		}
		public int getStatusCode() {
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
