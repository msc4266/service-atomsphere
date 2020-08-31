package com.manywho.services.atomsphere.actions.utility_atomcompare;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.apimlog.LogUtil;
import com.manywho.services.atomsphere.ServiceConfiguration;

public class CompareAtomPropertiesCommand implements ActionCommand<ServiceConfiguration, CompareAtomProperties, CompareAtomProperties.Inputs, CompareAtomProperties.Outputs>{

	@Override
	public ActionResponse<CompareAtomProperties.Outputs> execute(ServiceConfiguration configuration, ServiceRequest request,
			CompareAtomProperties.Inputs input) {
		List<AtomPropertyCompareItem> compareItems=null;
		try {
			compareItems = LogUtil.compareAtomProperties(configuration, input);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
        Collections.sort(compareItems, new AtomPropertyComparator());

		return new ActionResponse<>(new CompareAtomProperties.Outputs(compareItems));
	}
	
	class AtomPropertyComparator implements Comparator<AtomPropertyCompareItem> {
		@Override
		public int compare(AtomPropertyCompareItem n1, AtomPropertyCompareItem n2) {
//			if (!n1.getPropertyName()().contentEquals(n2.getPropertyName())) {
//				return n1.getLogType().compareTo(n2.getLogType());
//			}
			return n1.getPropertyName().compareTo(n2.getPropertyName());
		}
	}}
