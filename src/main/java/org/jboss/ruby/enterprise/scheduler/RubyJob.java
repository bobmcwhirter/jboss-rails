package org.jboss.ruby.enterprise.scheduler;

import org.jboss.logging.Logger;
import org.jruby.Ruby;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

public class RubyJob implements Job, StatefulJob {
	private Logger log;
	
	public RubyJob() {
	}

	public void execute(JobExecutionContext context) throws JobExecutionException {
		String taskClass = (String) context.getJobDetail().getJobDataMap().get( "task.class.name");
		log = Logger.getLogger( taskClass );
		
		Ruby runtime = (Ruby) context.getJobDetail().getJobDataMap().get( "ruby.runtime" );
		String name= context.getJobDetail().getName();
		
		log.info( "Starting job: " + name );
		String script = "$TASKS['" + name + "'].run\n";
		runtime.evalScriptlet( script );
		log.info( "Completed job: " + name );
	}

}
