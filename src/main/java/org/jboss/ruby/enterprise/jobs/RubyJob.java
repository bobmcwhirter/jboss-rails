package org.jboss.ruby.enterprise.jobs;

import java.text.ParseException;

import org.jboss.logging.Logger;
import org.jboss.ruby.runtime.RubyRuntimePool;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

public class RubyJob {
	
	private static final Logger log = Logger.getLogger( RubyJob.class );
	
	public static final String RUBY_CLASS_NAME_KEY = "jboss.ruby.class.name"; 
	public static final String RUNTIME_POOL_KEY = "jboss.ruby.pool";
	
	private String group;
	private String name;
	
	private String rubyClassName;
	private String description;
	
	private String cronExpression;
	
	private RubyRuntimePool runtimePool;
	private Scheduler scheduler;
	

	public RubyJob() {

	}
	
	public void setGroup(String group) {
		this.group = group;
	}
	
	public String getGroup() {
		return this.group;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setRubyClassName(String rubyClassName) {
		this.rubyClassName = rubyClassName;
	}
	
	public String getRubyClassName() {
		return this.rubyClassName;
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
	
	public void setRubyRuntimePool(RubyRuntimePool runtimePool) {
		this.runtimePool = runtimePool;
	}
	
	public RubyRuntimePool getRubyRuntimePool() {
		return this.runtimePool;
	}
	
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	public Scheduler getScheduler() {
		return this.scheduler;
	}

	public void start() throws ParseException, SchedulerException {
		log.info( "Starting Ruby job: " + this.group + "." + this.name );
		JobDetail jobDetail = new JobDetail();

		jobDetail.setGroup(this.group);
		jobDetail.setName(this.name);
		jobDetail.setDescription(this.description);
		jobDetail.setJobClass(RubyJobHandler.class);

		JobDataMap jobData = jobDetail.getJobDataMap();

		jobData.put( RUBY_CLASS_NAME_KEY, this.rubyClassName );
		jobData.put( RUNTIME_POOL_KEY, this.runtimePool );

		CronTrigger trigger = new CronTrigger(getTriggerName(), this.group, this.cronExpression );
		
		scheduler.scheduleJob(jobDetail, trigger);
	}
	
	private String getTriggerName() {
		return this.name + ".trigger";
	}

	public void stop() throws SchedulerException {
		log.info( "Stopping Ruby job: " + this.group + "." + this.name );
		scheduler.unscheduleJob( getTriggerName(), this.group );
	}

}
