package com.manywho.services.atomsphere.actions.utility_apimclusterlogs;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.apimlog.LogUtil;
import com.manywho.services.atomsphere.ServiceConfiguration;

public class GetClusterLogsCommand implements ActionCommand<ServiceConfiguration, GetClusterLogs, GetClusterLogs.Inputs, GetClusterLogs.Outputs>{

	AuthenticatedWho user;
    @Inject
    public GetClusterLogsCommand(AuthenticatedWho user) 
    {
    	this.user=user;
    }
    
	@Override
	public ActionResponse<GetClusterLogs.Outputs> execute(ServiceConfiguration configuration, ServiceRequest request,
			GetClusterLogs.Inputs input) {
		List<NodeLog> logs;
		try {
			logs = LogUtil.getLogFiles(configuration, user, input);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
        Collections.sort(logs, new NodeLogComparator());

		return new ActionResponse<>(new GetClusterLogs.Outputs(logs));
	}
	
	class NodeLogComparator implements Comparator<NodeLog> {
		@Override
		public int compare(NodeLog n1, NodeLog n2) {
			if (!n1.getLogType().contentEquals(n2.getLogType())) {
				return n1.getLogType().compareTo(n2.getLogType());
			}
			return n1.getNode().compareTo(n2.getNode());
		}
	}}
