package com.manywho.services.atomsphere.actions.changelistenerstatus;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

@Action.Metadata(name="Change Listener Status", summary = "Change listener status to pause, resume, restart, pause_all, resume_all, or restart_all", uri="/atomsphere/changeListenerStatus")
public class ChangeListenerStatus {
	public static class Inputs{
	    @Action.Input(name = "Listener ID", contentType = ContentType.String)
	    private String listenerId;

	    @Action.Input(name = "Container ID", contentType = ContentType.String)
	    private String containerId;

	    @Action.Input(name = "Action (pause|resume|restart|pause_all|resume_all|restart_all)", contentType = ContentType.String)
	    private String action;

		public String getListenerId() {
			return listenerId;
		}

		public String getContainerId() {
			return containerId;
		}

		public String getAction() {
			return action;
		}

	}
	
	public static class Outputs {
		@Action.Output(name="Status Code", contentType=ContentType.String)
		private String status;
		public Outputs(String status)
		{
			this.status=status;
		}

		public String status() {
			return status;
		}
	}
}
