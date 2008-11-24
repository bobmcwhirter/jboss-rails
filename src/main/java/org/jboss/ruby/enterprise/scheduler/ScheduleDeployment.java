package org.jboss.ruby.enterprise.scheduler;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Map;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.logging.Logger;
import org.jboss.ruby.enterprise.scheduler.metadata.ScheduleMetaData;
import org.jboss.ruby.enterprise.scheduler.metadata.ScheduleTaskMetaData;
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

	public ScheduleDeployment(ScheduleMetaData metaData) {
		this.metaData = metaData;
	}

	// ----------------------------------------

	public String getStatus() {
		return status;
	}

	// ----------------------------------------

	public void setScheduler(Scheduler scheduler) {
		log.info("ScheduleDeployment setScheduler(" + scheduler + ")");
		this.scheduler = scheduler;
	}

	public Scheduler getScheduler() {
		return this.scheduler;
	}

	public synchronized void start() throws ParseException, SchedulerException {
		log.info("ScheduleDeployment start()");
		for (ScheduleTaskMetaData task : metaData.getScheduledTasks()) {
			startTask(task);
		}
		this.status = "RUNNING";
	}

	protected void startTask(ScheduleTaskMetaData task) throws ParseException, SchedulerException {
		JobDetail jobDetail = new JobDetail();

		jobDetail.setGroup(task.getGroup());
		jobDetail.setName(task.getName());
		jobDetail.setDescription(task.getDescription());
		jobDetail.setJobClass(task.getJobClass());

		Map<String, Object> taskData = task.getTaskData();

		JobDataMap data = jobDetail.getJobDataMap();
		data.putAll(taskData);

		String expr = task.getCronExpression();

		log.info("starting task: " + jobDetail.getName() + " -- " + expr + " taskData: " + taskData);

		CronTrigger trigger = new CronTrigger(getTriggerName(task), task.getGroup(), expr);
		scheduler.scheduleJob(jobDetail, trigger);

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
