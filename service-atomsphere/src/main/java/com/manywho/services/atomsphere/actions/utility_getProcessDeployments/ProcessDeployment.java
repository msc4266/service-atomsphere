package com.manywho.services.atomsphere.actions.utility_getProcessDeployments;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;

@Type.Element(name = "ProcessDeployment")
public class ProcessDeployment implements Type{
	@Type.Identifier
	private String guid;
	@Type.Property(name = "Environment Name", contentType = ContentType.String)
	private String environmentName;
	@Type.Property(name = "Environment ID", contentType = ContentType.String)
	private String environmentId;
	@Type.Property(name = "Atom Name", contentType = ContentType.String)
	private String atomName;
	@Type.Property(name = "Atom ID", contentType = ContentType.String)
	private String atomId;
	public ProcessDeployment()
	{
		
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getEnvironmentName() {
		return environmentName;
	}
	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}
	public String getEnvironmentId() {
		return environmentId;
	}
	public void setEnvironmentId(String environmentId) {
		this.environmentId = environmentId;
	}
	public String getAtomName() {
		return atomName;
	}
	public void setAtomName(String atomName) {
		this.atomName = atomName;
	}
	public String getAtomId() {
		return atomId;
	}
	public void setAtomId(String atomId) {
		this.atomId = atomId;
	}
}
