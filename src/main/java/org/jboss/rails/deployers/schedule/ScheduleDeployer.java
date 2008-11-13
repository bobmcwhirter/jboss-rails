package org.jboss.rails.deployers.schedule;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.rails.metadata.schedule.ScheduleMetaData;
import org.jboss.rails.metadata.schedule.ScheduledTaskMetaData;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class ScheduleDeployer extends AbstractDeployer {
	
	public ScheduleDeployer() {
		setInput( ScheduleMetaData.class );
		setStage( DeploymentStages.REAL );
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
			
			for ( ScheduledTaskMetaData task : md.getScheduledTasks() ) {
				JobDetail jobDetail = new JobDetail();
				jobDetail.setName( task.getName() );
				jobDetail.setDescription( task.getDescription() );
				log.info( "deploying task " + jobDetail );
				scheduler.addJob( jobDetail, false );
			}
		} catch (SchedulerException e) {
			log.error( e );
		}
		
	}

}
