package org.jboss.rails.deployers.schedule;

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
		runtime.evalScriptlet( "$task.run\n" );
		
	}

}
