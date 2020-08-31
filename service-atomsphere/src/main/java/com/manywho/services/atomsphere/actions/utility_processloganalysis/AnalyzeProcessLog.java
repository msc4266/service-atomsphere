package com.manywho.services.atomsphere.actions.utility_processloganalysis;

import java.util.List;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

@Action.Metadata(name="Analyze Process Log", summary = "Parse log to list component executions", uri="/atomsphere/analyzeProcessLog")
public class AnalyzeProcessLog {
	public static class Inputs{
	    @Action.Input(name = "Execution Id", contentType = ContentType.String)
	    private String executionId;

	    public String getExecutionId() {
	        return executionId;
	    }

	    @Action.Input(name = "Aggregate by Component Name", contentType = ContentType.Boolean)
	    private Boolean aggregate;

	    public Boolean getAggregate() {
	        return aggregate;
	    }
	}
	
	public static class Outputs {
		@Action.Output(name="Log File Analysis", contentType=ContentType.List)
		private List<ProcessLogItem> processLogItems;
		public Outputs(List<ProcessLogItem> processLogItems)
		{
			this.processLogItems=processLogItems;
		}
	}
}
