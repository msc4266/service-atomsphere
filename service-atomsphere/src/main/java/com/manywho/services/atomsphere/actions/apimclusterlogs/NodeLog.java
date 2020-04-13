package com.manywho.services.atomsphere.actions.apimclusterlogs;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;

@Type.Element(name = "NodeLog")
public class NodeLog implements Type{
	@Type.Identifier
	private String guid;
	@Type.Property(name = "Node", contentType = ContentType.String)
	private String node;
	@Type.Property(name = "Log Type", contentType = ContentType.String)
	private String logType;
	@Type.Property(name = "Log Entries", contentType = ContentType.String)
	private String entries;
	@Type.Property(name = "Log File Name", contentType = ContentType.String)
	private String fileName;
	@Type.Property(name = "Entry Size", contentType = ContentType.Number)
	private int entrySize;

	public NodeLog()
	{
		
	}

	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public String getLogType() {
		return logType;
	}
	public void setLogType(String logType) {
		this.logType = logType;
	}
	public String getEntries() {
		return entries;
	}
	public void setEntries(String entries) {
		this.entries = entries;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getEntrySize() {
		return entrySize;
	}

	public void setEntrySize(int entrySize) {
		this.entrySize = entrySize;
	}
}
