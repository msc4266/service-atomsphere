package com.manywho.services.atomsphere.actions.getAssignableRoles;

import java.util.UUID;

import org.json.JSONObject;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;

@Type.Element(name = "Property")
public class Role implements Type{
	@Type.Identifier
	private String guid;
	@Type.Property(name = "ID", contentType = ContentType.String)
	private String id;
	@Type.Property(name = "Name", contentType = ContentType.String)
	private String name;	
	@Type.Property(name = "Account ID", contentType = ContentType.String)
	private String accountId;	
	@Type.Property(name = "Description", contentType = ContentType.String)
	private String description;

	public Role()
	{
	}

	public Role(JSONObject role)
	{
		this.id=role.getString("id");
		this.accountId=role.getString("accountId");
		this.name=role.getString("name");
		this.description=role.getString("description");
		this.guid=UUID.randomUUID().toString();
	}

	public String getGuid() {
		return guid;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAccountId() {
		return accountId;
	}

	public String getDescription() {
		return description;
	}
}
