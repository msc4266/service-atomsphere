package com.manywho.services.atomsphere.actions.apimclusterlogs;

import java.util.Date;
import java.util.List;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;
import com.manywho.services.atomsphere.actions.apimclusterlogs.NodeLog;

@Action.Metadata(name="Get Cluster Logs", summary = "Get GW/Broker/API logs for an atom by time range", uri="/atomsphere/getClusterLogs")
public class GetClusterLogs {
	public static class Inputs{
	    @Action.Input(name = "Atom ID", contentType = ContentType.String)
	    private String atomId;

	    public String getAtomId() {
	        return atomId;
	    }

	    @Action.Input(name = "Errors Only", contentType = ContentType.Boolean)
	    private boolean errorsOnly;

	    public boolean getErrorsOnly() {
	        return errorsOnly;
	    }

	    @Action.Input(name = "Full Stack Traces", contentType = ContentType.Boolean)
	    private boolean fullStackTraces;

	    public boolean getFullStackTraces() {
	        return fullStackTraces;
	    }

	    @Action.Input(name = "Date/Time Format", contentType = ContentType.String)
	    private String datetimeFormat;

	    public String getDateTimeFormat() {
	        return datetimeFormat;
	    }

	    @Action.Input(name = "Timezone", contentType = ContentType.String)
	    private String timezone;

	    public String getTimeZone() {
	        return timezone;
	    }

	    @Action.Input(name = "Maximum File Size", contentType = ContentType.Number)
	    private long maxFileSize;

	    public long getMaximumFileSize() {
	        return maxFileSize;
	    }

	    @Action.Input(name = "Start Time", contentType = ContentType.DateTime)
	    private Date startTime;

	    public Date getStartTime() {
	        return startTime;
	    }

	    @Action.Input(name = "Seconds Before", contentType = ContentType.Number)
	    private int secondsBefore;

	    public int getSecondsBefore() {
	        return secondsBefore;
	    }

	    @Action.Input(name = "Seconds After", contentType = ContentType.Number)
	    private int secondsAfter;

	    public int getSecondsAfter() {
	        return secondsAfter;
	    }

		public String getDatetimeFormat() {
			return datetimeFormat;
		}

		public void setDatetimeFormat(String datetimeFormat) {
			this.datetimeFormat = datetimeFormat;
		}

		public String getTimezone() {
			return timezone;
		}

		public void setTimezone(String timezone) {
			this.timezone = timezone;
		}

		public long getMaxFileSize() {
			return maxFileSize;
		}

		public void setMaxFileSize(long maxFileSize) {
			this.maxFileSize = maxFileSize;
		}

		public void setAtomId(String atomId) {
			this.atomId = atomId;
		}

		public void setErrorsOnly(boolean errorsOnly) {
			this.errorsOnly = errorsOnly;
		}

		public void setFullStackTraces(boolean fullStackTraces) {
			this.fullStackTraces = fullStackTraces;
		}

		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}

		public void setSecondsBefore(int secondsBefore) {
			this.secondsBefore = secondsBefore;
		}

		public void setSecondsAfter(int secondsAfter) {
			this.secondsAfter = secondsAfter;
		}
	}
	
	public static class Outputs {
		@Action.Output(name="Cluster Logs", contentType=ContentType.List)
		private List<NodeLog> clusterLogs;
		public Outputs(List<NodeLog> clusterLogs)
		{
			this.clusterLogs=clusterLogs;
		}
	}
}
