package com.manywho.services.atomsphere.actions.getAssignableRoles;

import java.io.IOException;
import java.util.List;
import org.json.JSONObject;
import com.google.common.collect.Lists;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

@Action.Metadata(name="Get Assignable Roles", summary = "Get Assignable Roles operation is used to retrieve a list of roles that are assignable under a Dell Boomi account", uri="/atomsphere/getAssignableRoles")
public class GetAssignableRoles {
	public static class Inputs{
	}
	
	public static class Outputs {
		@Action.Output(name="Roles", contentType=ContentType.List)
		private List<Role> roles;
		
		public Outputs(JSONObject response)
		{	
			this.roles=Lists.newArrayList();
			for (Object obj:response.getJSONArray("Role"))
			{
				JSONObject jRole = (JSONObject)obj;
				roles.add(new Role(jRole));
			}
		}

		public List<Role> getRoles() throws IOException {
			return roles;
		}
	}
}
