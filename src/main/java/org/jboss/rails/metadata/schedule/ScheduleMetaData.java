package org.jboss.rails.metadata.schedule;

import java.util.ArrayList;
import java.util.List;

public class ScheduleMetaData {
	
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
