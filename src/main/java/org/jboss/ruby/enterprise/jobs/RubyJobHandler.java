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
package org.jboss.ruby.enterprise.jobs;

import org.jboss.logging.Logger;
import org.jboss.ruby.runtime.RubyRuntimePool;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

public class RubyJobHandler implements Job, StatefulJob {
	private static final Logger log = Logger.getLogger( RubyJobHandler.class );
	
	public RubyJobHandler() {
	}

	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		JobDetail jobDetail = context.getJobDetail();
		JobDataMap jobDataMap = jobDetail.getJobDataMap();
		
		String rubyClassName = (String) jobDataMap.get( RubyJob.RUBY_CLASS_NAME_KEY );
		RubyRuntimePool runtimePool = (RubyRuntimePool) jobDataMap.get( RubyJob.RUNTIME_POOL_KEY );
		
		log.info( "execute(" + rubyClassName + ", " + runtimePool + ")" );
	}

}
