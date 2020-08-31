package com.manywho.services.atomsphere.actions.utility_atomcompare;

import java.util.Date;
import java.util.List;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;
import com.manywho.services.atomsphere.actions.utility_apimclusterlogs.NodeLog;

@Action.Metadata(name="Compare Atom Propertes", summary = "Get GW/Broker/API logs for an atom by time range", uri="/atomsphere/compareAtom")
public class CompareAtomProperties {
	public static class Inputs{
	    @Action.Input(name = "Atom ID 1", contentType = ContentType.String)
	    private String atomId1;

	    public String getAtomId1() {
	        return atomId1;
	    }

	    @Action.Input(name = "Atom ID 2", contentType = ContentType.String)
	    private String atomId2;

	    public String getAtomId2() {
	        return atomId2;
	    }

		public void setAtomId1(String atomId1) {
			this.atomId1 = atomId1;
		}

		public void setAtomId2(String atomId2) {
			this.atomId2 = atomId2;
		}
	}
	
	public static class Outputs {
		@Action.Output(name="Atom Comparison", contentType=ContentType.List)
		private List<AtomPropertyCompareItem> compareItems;
		public Outputs(List<AtomPropertyCompareItem> compareItems)
		{
			this.compareItems=compareItems;
		}
	}
}
