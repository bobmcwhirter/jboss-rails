package org.jboss.rails.enterprise.scheduler.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ScheduleMetaData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5243123742456609507L;
	
	private List<ScheduledTaskMetaData> scheduledTasks;
	
	public ScheduleMetaData() {
		this.scheduledTasks = new ArrayList<ScheduledTaskMetaData>();
	}
	
	public void addScheduledTask(ScheduledTaskMetaData scheduledTask) {
		this.scheduledTasks.add( scheduledTask );
	}
	
	public List<ScheduledTaskMetaData> getScheduledTasks() {
		return this.scheduledTasks;
	}

}
