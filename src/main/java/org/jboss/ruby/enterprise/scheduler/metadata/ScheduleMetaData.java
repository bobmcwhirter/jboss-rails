package org.jboss.ruby.enterprise.scheduler.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;

public class ScheduleMetaData implements Serializable {
	
	protected static Logger log = Logger.getLogger( ScheduleMetaData.class );
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5243123742456609507L;
	
	private List<ScheduleTaskMetaData> scheduledTasks;
	
	public ScheduleMetaData() {
		this.scheduledTasks = new ArrayList<ScheduleTaskMetaData>();
	}
	
	public void addScheduledTask(ScheduleTaskMetaData scheduledTask) {
		this.scheduledTasks.add( scheduledTask );
	}
	
	public List<ScheduleTaskMetaData> getScheduledTasks() {
		return this.scheduledTasks;
	}

}
