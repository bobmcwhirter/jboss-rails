package org.jboss.ruby.enterprise.scheduler.metadata;

import java.util.Map;

import org.quartz.Job;

public class ScheduleTaskMetaData {

	private String group;
	private String name;
	private String description;
	private String cronExpression;
	private String rubyClass;

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

	public void setRubyClass(String rubyClass) {
		this.rubyClass = rubyClass;
	}
	
	public String getRubyClass() {
		return this.rubyClass;
	}
}
