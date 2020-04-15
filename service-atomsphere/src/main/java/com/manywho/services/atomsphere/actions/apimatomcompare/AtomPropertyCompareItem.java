package com.manywho.services.atomsphere.actions.apimatomcompare;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;

@Type.Element(name = "AtomPropertyCompareItem")
public class AtomPropertyCompareItem implements Type{
	@Type.Identifier
	private String guid;
	@Type.Property(name = "Property Name", contentType = ContentType.String)
	private String propertyName;
	@Type.Property(name = "Atom 1 Value", contentType = ContentType.String)
	private String value1;
	@Type.Property(name = "Atom 2 Value", contentType = ContentType.String)
	private String value2;
	@Type.Property(name = "Message", contentType = ContentType.String)
	private String message;

	public AtomPropertyCompareItem()
	{
		
	}

	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getValue1() {
		return value1;
	}

	public void setValue1(String value1) {
		this.value1 = value1;
	}

	public String getValue2() {
		return value2;
	}

	public void setValue2(String value2) {
		this.value2 = value2;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
