package com.manywho.services.atomsphere.actions.utility_processloganalysis;

import java.util.UUID;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;

@Type.Element(name = "ProcessLogItem")
public class ProcessLogItem implements Type{
	@Type.Identifier
	private String guid;
	@Type.Property(name = "Component Name", contentType = ContentType.String)
	private String componentName;
	@Type.Property(name = "Execution Duration", contentType = ContentType.Number)
	private Long executionDuration;
	@Type.Property(name = "Document Count", contentType = ContentType.Number)
	private Long documentCount;
	@Type.Property(name = "Execution Count", contentType = ContentType.Number)
	private Long executionCount;

	public ProcessLogItem()
	{
		
	}

	public ProcessLogItem(String componentName, Long duration, Long documentCount, Long executionCount) {
		this.setComponentName(componentName);
		this.setExecutionDuration(duration);
		this.setDocumentCount(documentCount);
		this.setExecutionCount(executionCount);
		this.guid = UUID.randomUUID().toString();
	}

	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public Long getExecutionDuration() {
		return executionDuration;
	}

	public void setExecutionDuration(Long executionDuration) {
		this.executionDuration = executionDuration;
	}

	public Long getDocumentCount() {
		return documentCount;
	}

	public void setDocumentCount(Long documentCount) {
		this.documentCount = documentCount;
	}

	public Long getExecutionCount() {
		return executionCount;
	}

	public void setExecutionCount(Long executionCount) {
		this.executionCount = executionCount;
	}
}
