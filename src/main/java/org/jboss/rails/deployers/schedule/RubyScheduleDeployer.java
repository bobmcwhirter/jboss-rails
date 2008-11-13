package org.jboss.rails.deployers.schedule;

import org.jboss.deployers.vfs.spi.deployer.AbstractVFSParsingDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.rails.metadata.schedule.ScheduleMetaData;
import org.jboss.rails.metadata.schedule.ScheduledTaskMetaData;
import org.jboss.virtual.VirtualFile;

public class RubyScheduleDeployer extends AbstractVFSParsingDeployer<ScheduleMetaData> {

	public RubyScheduleDeployer(){
		super(ScheduleMetaData.class);
		setAllInputs( true );
		setName( "jboss-scheduler.yml" );
	}

	@Override
	protected ScheduleMetaData parse(VFSDeploymentUnit unit, VirtualFile file, ScheduleMetaData root) throws Exception {
		log.debug( "##### parsing " + file );
		
		ScheduleMetaData md = new ScheduleMetaData();
		ScheduledTaskMetaData task = new ScheduledTaskMetaData();
		task.setName( "Blog poller" );
		task.setDescription( "Blog poller tasky" );
		md.addScheduledTask( task );
		return md;
	}
}