package org.jboss.ruby.enterprise.scheduler.metadata;

import java.util.Map;

import org.quartz.Job;

public class ScheduleTaskMetaData {

	private String group;
	private String name;
	private String description;
	private Map<String, Object> taskData;
	private String cronExpression;
	private Class<? extends Job> jobClass;

	public ScheduleTaskMetaData() {
		
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
	
	public Class<? extends Job> getJobClass() {
		return jobClass;
	}
	
	public void setJobClass(Class<? extends Job> jobClass) {
		this.jobClass = jobClass;
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
