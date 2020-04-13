package com.manywho.services.atomsphere.actions.apimclusterlogs;

import java.util.List;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.apimlog.LogUtil;
import com.manywho.services.atomsphere.ServiceConfiguration;

public class GetClusterLogsCommand implements ActionCommand<ServiceConfiguration, GetClusterLogs, GetClusterLogs.Inputs, GetClusterLogs.Outputs>{

	@Override
	public ActionResponse<GetClusterLogs.Outputs> execute(ServiceConfiguration configuration, ServiceRequest request,
			GetClusterLogs.Inputs input) {
		List<NodeLog> logs;
		try {
			logs = LogUtil.getLogFiles(configuration, input.getAtomId(), input.getErrorsOnly(), input.getStartTime(), input.getSecondsBefore(), input.getSecondsAfter());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return new ActionResponse<>(new GetClusterLogs.Outputs(logs));
	}
}
