package org.jboss.ruby.enterprise.scheduler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;
import org.jboss.ruby.enterprise.scheduler.metadata.ScheduleMetaData;
import org.jboss.ruby.enterprise.scheduler.metadata.ScheduleTaskMetaData;
import org.jboss.ruby.runtime.RubyRuntimeFactory;
import org.jruby.Ruby;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

public class ScheduleDeployment implements ScheduleDeploymentMBean, Serializable {

	private static final long serialVersionUID = -2068933525731765570L;

	protected static Logger log = Logger.getLogger(ScheduleDeployment.class);

	private Scheduler scheduler;

	private ScheduleMetaData metaData;

	private String status = "STOPPED";

	private RubyRuntimeFactory runtimeFactory;

	private Ruby sharedInstance;

	public ScheduleDeployment(ScheduleMetaData metaData, RubyRuntimeFactory runtimeFactory) {
		this.metaData = metaData;
		this.runtimeFactory = runtimeFactory;
	}

	// ----------------------------------------

	public String getStatus() {
		return status;
	}

	public int getNumberOfTasks() {
		return metaData.getScheduledTasks().size();
	}

	// ----------------------------------------

	public void setScheduler(Scheduler scheduler) {
		log.info("ScheduleDeployment setScheduler(" + scheduler + ")");
		this.scheduler = scheduler;
	}

	public Scheduler getScheduler() {
		return this.scheduler;
	}

	public synchronized void start() throws Exception {
		log.info("ScheduleDeployment start()");
		List<ScheduleTaskMetaData> startedTasks = new ArrayList<ScheduleTaskMetaData>();
		try {
			for (ScheduleTaskMetaData task : metaData.getScheduledTasks()) {
				startTask(task);
				startedTasks.add(task);
			}
		} catch (Exception e) {
			for (ScheduleTaskMetaData task : startedTasks) {
				stopTask(task);
			}
			throw e;
		}
		this.status = "RUNNING";
	}

	protected void startTask(ScheduleTaskMetaData task) throws Exception {
		JobDetail jobDetail = new JobDetail();

		jobDetail.setGroup(task.getGroup());
		jobDetail.setName(task.getName());
		jobDetail.setDescription(task.getDescription());
		jobDetail.setJobClass(RubyJob.class);

		JobDataMap jobData = jobDetail.getJobDataMap();

		jobData.put("task.class.name", task.getRubyClass());
		jobData.put("ruby.runtime", createRubyTaskObject( task ) );

		String expr = task.getCronExpression();

		log.info("scheduling task: " + jobDetail.getName() + " -- " + expr );

		CronTrigger trigger = new CronTrigger(getTriggerName(task), task.getGroup(), expr);
		scheduler.scheduleJob(jobDetail, trigger);

	}

	protected Ruby createRubyTaskObject(ScheduleTaskMetaData taskMetaData) throws Exception {
		Ruby ruby = getRuntime();

		StringBuilder script = new StringBuilder();
		script.append("s = '" + taskMetaData.getRubyClass() + "'\n");
		script.append("s = s.gsub( /::/, '/' )\n");
		script.append("s = s.gsub( /([^\\/][A-Z])/ ) { |m| \"#{m[0,1]}_#{m[1,1].downcase}\" }\n");
		script.append("s = s.gsub( /([\\/][A-Z])/ ) { |m| \"/#{m[1,1].downcase}\" }\n");
		script.append("s = s.downcase\n");
		script.append("require s\n");
		script.append("$TASKS['" + taskMetaData.getName() + "'] = " + taskMetaData.getRubyClass() + ".new\n");

		ruby.evalScriptlet(script.toString());
		
		return ruby;
	}

	protected Ruby getRuntime() throws Exception {
		if (this.metaData.isThreadSafe()) {
			synchronized (this) {
				log.info("using shared runtime");
				if (this.sharedInstance == null) {
					this.sharedInstance = initializeRuntime(this.runtimeFactory.createRubyRuntime());
				}
				return this.sharedInstance;
			}
		}

		log.info("using unique runtime");
		return initializeRuntime(this.runtimeFactory.createRubyRuntime());
	}

	protected Ruby initializeRuntime(Ruby ruby) {
		StringBuilder script = new StringBuilder();
		for (String path : this.metaData.getLoadPaths()) {
			script.append("$: << '" + path + "'\n");
			script.append( "$TASKS = {}\n" );
		}
		log.info("initialize with: " + script);
		ruby.evalScriptlet(script.toString());
		return ruby;
	}

	private String getTriggerName(ScheduleTaskMetaData task) {
		return task.getName() + ".trigger";
	}

	public synchronized void stop() {
		log.info("ScheduleDeployment stop()");

		for (ScheduleTaskMetaData task : metaData.getScheduledTasks()) {
			try {
				stopTask(task);
			} catch (SchedulerException e) {
				log.error(e);
			}
		}

		this.status = "STOPPED";
	}

	protected void stopTask(ScheduleTaskMetaData task) throws SchedulerException {
		scheduler.unscheduleJob(getTriggerName(task), task.getGroup());
	}

}
