package com.manywho.services.atomsphere.actions.executeProcess;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;

@Type.Element(name = "ProcessProperty")
public class ProcessProperty implements Type{
	@Type.Identifier
	private String guid;
	@Type.Property(name = "Value", contentType = ContentType.String)
	private String value;
	@Type.Property(name = "Name", contentType = ContentType.String)
	private String name;
	public ProcessProperty()
	{
		
	}
	public ProcessProperty(String value, String name, String guid)
	{
		this.name=name;
		this.value=value;
		this.guid=guid;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}	
}
