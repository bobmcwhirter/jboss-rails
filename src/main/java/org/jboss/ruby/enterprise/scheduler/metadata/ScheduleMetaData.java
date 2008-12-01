/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
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

	private boolean threadSafe = false;
	
	private List<String> loadPaths = new ArrayList<String>();
	
	public ScheduleMetaData() {
		this.scheduledTasks = new ArrayList<ScheduleTaskMetaData>();
	}
	
	public void addLoadPath(String loadPath) {
		this.loadPaths.add( loadPath );
	}
	
	public List<String> getLoadPaths() {
		return this.loadPaths;
	}
	
	public void addScheduledTask(ScheduleTaskMetaData scheduledTask) {
		this.scheduledTasks.add( scheduledTask );
	}
	
	public List<ScheduleTaskMetaData> getScheduledTasks() {
		return this.scheduledTasks;
	}
	
	public void setThreadSafe(boolean threadSafe) {
		this.threadSafe = threadSafe;
	}
	
	public boolean isThreadSafe() {
		return this.threadSafe;
	}

}
