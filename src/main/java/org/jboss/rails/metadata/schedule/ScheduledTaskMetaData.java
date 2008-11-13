package org.jboss.rails.metadata.schedule;

public class ScheduledTaskMetaData {

	private String name;
	private String description;

	public ScheduledTaskMetaData() {
		
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return this.description;
	}
}
