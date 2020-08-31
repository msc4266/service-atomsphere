package com.manywho.services.atomsphere.actions.utility_getProcessDeployments;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

@Action.Metadata(name="GetProcessDeployments", summary = "Get Environment and Atom IDs for Process", uri="/atomsphere/getProcessDeployments")
public class GetProcessDeployments {
	private static final Logger LOGGER = LoggerFactory.getLogger(GetProcessDeployments.class);
	public static class Inputs{
	    @Action.Input(name = "Process ID", contentType = ContentType.String)
	    private String processId;

	    public String getProcessId() {
			return processId;
		}

	}
	
	public static class Outputs {
	    @Action.Output(name = "Process Deployments", contentType = ContentType.List)
	    private List<ProcessDeployment> processDeployments;

	    public Outputs(List<ProcessDeployment> procDeployments)
	    {
	    	this.processDeployments = procDeployments;
			LOGGER.info("processDeployments: " + processDeployments.size());
	    }
		public List<ProcessDeployment> getProcesssDeployments() {
			LOGGER.info("processDeployments 2: " + processDeployments.size());
			return processDeployments;
		}
	}
}
