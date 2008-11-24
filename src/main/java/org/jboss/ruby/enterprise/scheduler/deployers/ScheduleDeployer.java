package org.jboss.ruby.enterprise.scheduler.deployers;

import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.helpers.AbstractSimpleRealDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.ruby.enterprise.scheduler.ScheduleDeployment;
import org.jboss.ruby.enterprise.scheduler.metadata.ScheduleMetaData;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class ScheduleDeployer extends AbstractSimpleRealDeployer<ScheduleMetaData> {

	public ScheduleDeployer() {
		super(ScheduleMetaData.class);
		addOutput( BeanMetaData.class );
		setParentFirst(true);
	}

	@Override
	public void deploy(DeploymentUnit unit, ScheduleMetaData deployment) throws DeploymentException {
		
		StdSchedulerFactory factory = new StdSchedulerFactory();
		
		try {
			Scheduler scheduler = factory.getScheduler();
			BeanMetaDataBuilder builder = BeanMetaDataBuilder.createBuilder("jboss.ruby.scheduler:unit="+unit.getSimpleName() , ScheduleDeployment.class.getName() );
			builder.addConstructorParameter( ScheduleMetaData.class.getName(), deployment);
			builder.addPropertyMetaData( "scheduler", scheduler );
			BeanMetaData schedulerBean = builder.getBeanMetaData();
			unit.addAttachment( BeanMetaData.class, schedulerBean );
		} catch (SchedulerException e) {
			log.error(e);
		}
		
	}

}
