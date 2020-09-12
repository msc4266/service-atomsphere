package com.manywho.services.atomsphere.actions.utility_processloganalysis;

import java.util.List;

import javax.inject.Inject;

import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.apimlog.ProcessLogUtil;
import com.manywho.services.atomsphere.ServiceConfiguration;

public class AnalyzeProcessLogCommand implements ActionCommand<ServiceConfiguration, AnalyzeProcessLog, AnalyzeProcessLog.Inputs, AnalyzeProcessLog.Outputs>{

    AuthenticatedWho user;
    @Inject
    public AnalyzeProcessLogCommand(AuthenticatedWho user) 
    {
    	this.user=user;
    }
    
	@Override
	public ActionResponse<AnalyzeProcessLog.Outputs> execute(ServiceConfiguration configuration, ServiceRequest request,
			AnalyzeProcessLog.Inputs input) {
		List<ProcessLogItem> processLogItems=null;
		try {
			ProcessLogUtil util = new ProcessLogUtil();
			processLogItems = util.analyzeLog(configuration, user, input.getExecutionId(), input.getAggregate());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
//        Collections.sort(processLogItems, new AtomPropertyComparator());

		return new ActionResponse<>(new AnalyzeProcessLog.Outputs(processLogItems));
	}	
}
