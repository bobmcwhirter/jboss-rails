package org.jboss.rails.scheduler.deployers;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractSimpleRealDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.rails.core.metadata.RailsApplicationMetaData;
import org.jboss.rails.core.metadata.RailsMetaData;
import org.jboss.rails.core.metadata.RailsVersionMetaData;
import org.jboss.ruby.enterprise.scheduler.metadata.ScheduleMetaData;

public class RailsScheduleDescribeDeployer extends AbstractSimpleRealDeployer<ScheduleMetaData> {
	
	private static Logger log = Logger.getLogger( RailsScheduleDescribeDeployer.class );
	
	public RailsScheduleDescribeDeployer() {
		super( ScheduleMetaData.class );
		addInput( RailsApplicationMetaData.class );
		addInput( RailsVersionMetaData.class );
		addOutput( ScheduleMetaData.class );
		setStage( DeploymentStages.DESCRIBE );
	}

	@Override
	public void deploy(DeploymentUnit unit, ScheduleMetaData scheduleMetaData) throws DeploymentException {
		RailsVersionMetaData railsVersion = unit.getAttachment( RailsVersionMetaData.class );
		scheduleMetaData.setThreadSafe( railsVersion.isThreadSafe() );
		RailsApplicationMetaData railsMetaData = unit.getAttachment( RailsApplicationMetaData.class );
		scheduleMetaData.addLoadPath( railsMetaData.getRailsRoot() + "/app/scheduler" );
		log.info( "fixed up schedule with " + railsMetaData.getRailsRoot() + " and " + railsVersion );
	}


}
