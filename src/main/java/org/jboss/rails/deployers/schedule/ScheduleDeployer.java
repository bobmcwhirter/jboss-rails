package org.jboss.rails.deployers.schedule;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.rails.metadata.schedule.ScheduleMetaData;
import org.jboss.rails.metadata.schedule.ScheduledTaskMetaData;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class ScheduleDeployer extends AbstractDeployer {
	
	public ScheduleDeployer() {
		setInput( ScheduleMetaData.class );
		setStage( DeploymentStages.REAL );
		setParentFirst( true );
	}
	
	public void deploy(DeploymentUnit unit) throws DeploymentException {
		ScheduleMetaData md = unit.getAttachment( ScheduleMetaData.class );
		if ( md == null ) {
			return;
		}
		log.info( "deploying " + md );
		
		StdSchedulerFactory factory = new StdSchedulerFactory();
		try {
			Scheduler scheduler = factory.getScheduler();
			
			Random random = new Random();
			
			for ( ScheduledTaskMetaData task : md.getScheduledTasks() ) {
				JobDetail jobDetail = new JobDetail();
				jobDetail.setName( task.getName() );
				jobDetail.setDescription( task.getDescription() );
				jobDetail.setJobClass( RubyJob.class );
				
				Map<String,Object> taskData = task.getTaskData();
				JobDataMap data =  jobDetail.getJobDataMap();
				data.put( "ruby.class", taskData.get( "ruby.class" ) );
				
				String expr = task.getCronExpression();
				
				log.info( "job: " + jobDetail.getName() + " -- " + expr + " taskData: " + taskData );
				
				CronTrigger trigger = new CronTrigger( jobDetail.getName() + ".trigger", jobDetail.getGroup(), expr );
				//int randomOffset = random.nextInt( 1000 );
				int randomOffset = 0;
				Date startTime = new Date( System.currentTimeMillis() + randomOffset );
				trigger.setStartTime( startTime );
				scheduler.scheduleJob( jobDetail, trigger );
			}
		} catch (SchedulerException e) {
			log.error( e );
		} catch (ParseException e) {
			log.error( e );
		}
		
	}

}
