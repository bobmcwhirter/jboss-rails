package org.jboss.rails.enterprise.scheduler;

import org.jboss.logging.Logger;
import org.jruby.Ruby;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

public class RubyJob implements Job, StatefulJob {
	Logger log = Logger.getLogger( RubyJob.class );
	
	public RubyJob() {
	}

	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap data = context.getMergedJobDataMap();
		Ruby runtime = (Ruby) data.get( "ruby.runtime" );
		log.info( "Starting job" );
		runtime.evalScriptlet( "$task.run\n" );
		log.info( "Completed job" );
		
	}

}
