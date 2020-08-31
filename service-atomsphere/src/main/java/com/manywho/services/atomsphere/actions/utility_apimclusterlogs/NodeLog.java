package com.manywho.services.atomsphere.actions.utility_apimclusterlogs;

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
	private long entrySize;
	@Type.Property(name = "Segment Size", contentType = ContentType.Number)
	private long segmentSize;

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

	public long getEntrySize() {
		return entrySize;
	}

	public void setEntrySize(long entrySize) {
		this.entrySize = entrySize;
	}
	
	public void setSegmentSize(long segmentSize) {
		this.segmentSize = segmentSize;
	}
	
	public long getSegmentSize() {
		return segmentSize;
	}
}
