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
