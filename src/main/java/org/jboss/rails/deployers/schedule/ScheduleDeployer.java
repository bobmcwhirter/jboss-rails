package org.jboss.rails.deployers.schedule;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractSimpleRealDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.rails.metadata.schedule.ScheduleMetaData;
import org.jboss.rails.metadata.schedule.ScheduledTaskMetaData;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class ScheduleDeployer extends AbstractSimpleRealDeployer<ScheduleMetaData> {

	public ScheduleDeployer() {
		super(ScheduleMetaData.class);
		// setInput( ScheduleMetaData.class );
		// setStage( DeploymentStages.REAL );
		setParentFirst(true);
	}

	@Override
	public void deploy(DeploymentUnit unit, ScheduleMetaData deployment) throws DeploymentException {
		if (deployment == null) {
			return;
		}
		log.info("deploying " + deployment);

		StdSchedulerFactory factory = new StdSchedulerFactory();
		try {
			Scheduler scheduler = factory.getScheduler();

			Random random = new Random();

			for (ScheduledTaskMetaData task : deployment.getScheduledTasks()) {
				JobDetail jobDetail = new JobDetail();
				jobDetail.setGroup(task.getGroup());
				jobDetail.setName(task.getName());
				jobDetail.setDescription(task.getDescription());
				jobDetail.setJobClass(RubyJob.class);

				Map<String, Object> taskData = task.getTaskData();
				JobDataMap data = jobDetail.getJobDataMap();
				data.putAll(taskData);

				String expr = task.getCronExpression();

				log.info("job: " + jobDetail.getName() + " -- " + expr + " taskData: " + taskData);

				CronTrigger trigger = new CronTrigger(jobDetail.getName() + ".trigger", jobDetail.getGroup(), expr);
				// int randomOffset = random.nextInt( 1000 );
				int randomOffset = 0;
				Date startTime = new Date(System.currentTimeMillis() + randomOffset);
				trigger.setStartTime(startTime);
				scheduler.scheduleJob(jobDetail, trigger);
			}
		} catch (SchedulerException e) {
			log.error(e);
		} catch (ParseException e) {
			log.error(e);
		}

	}

	@Override
	public void undeploy(DeploymentUnit unit, ScheduleMetaData deployment) {
		StdSchedulerFactory factory = new StdSchedulerFactory();
		try {
			Scheduler scheduler = factory.getScheduler();
			for (ScheduledTaskMetaData task : deployment.getScheduledTasks()) {
				scheduler.unscheduleJob( task.getName() + ".trigger", task.getGroup() );
			}
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
