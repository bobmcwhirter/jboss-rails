package org.jboss.rails.metadata.schedule;

import java.util.Map;

public class ScheduledTaskMetaData {

	private String group;
	private String name;
	private String description;
	private Map<String, Object> taskData;
	private String cronExpression;

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

	public void setTaskData(Map<String, Object> taskData) {
		this.taskData = taskData;
	}
	
	public Map<String, Object> getTaskData() {
		return this.taskData;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}
	
	public String getCronExpression() {
		return this.cronExpression;
	}
	
	public void setGroup(String group) {
		this.group = group;
	}

	public String getGroup() {
		return this.group;
	}
}
