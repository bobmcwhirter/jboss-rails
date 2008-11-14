package org.jboss.rails.deployers.schedule;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.ho.yaml.Yaml;
import org.jboss.deployers.spi.DeploymentException;
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
		
		return parseSchedulerYml( file );
	}
	
	@SuppressWarnings("unchecked")
	private ScheduleMetaData parseSchedulerYml(VirtualFile file) throws DeploymentException {
		try {
			Map<String, Map<String,String>> results = (Map<String, Map<String,String>>) Yaml.load(file.openStream());

			ScheduleMetaData metaData = new ScheduleMetaData();
			
			for ( String jobName : results.keySet() ) {
				Map<String,String> jobSpec = results.get( jobName );
				ScheduledTaskMetaData task = new ScheduledTaskMetaData();
				task.setName( jobName );
				task.setDescription( jobSpec.get( "description" ) );
				Map<String,Object> taskData = new HashMap<String,Object>();
				taskData.put( "ruby.class", jobSpec.get( "task" ) );
				task.setTaskData( taskData );
				task.setCronExpression( buildCronExpression( jobSpec ) );
				metaData.addScheduledTask( task );
			}
			
			return metaData;
		} catch (IOException e) {
			file.closeStreams();
			throw new DeploymentException(e);
		}
	}
	
	private String buildCronExpression(Map<String,String> jobSpec) {
		
		String cronExpr = jobSpec.get( "cron" );
		
		if ( cronExpr != null ) {
			return cronExpr.trim();
		}
		
		String seconds = jobSpec.get( "seconds" );
		if ( seconds == null || seconds.trim().equals( "" ) ) {
			seconds = "*";
		} else {
			seconds = seconds.trim();
		}
		
		String minutes = jobSpec.get( "minutes" );
		if ( minutes == null || minutes.trim().equals( "" ) ) {
			minutes = "*";
		} else {
			minutes = minutes.trim();
		}
		
		String hours = jobSpec.get( "hours" );
		if ( hours == null || hours.trim().equals( "" ) ) {
			hours = "*";
		} else {
			hours = hours.trim();
		}
		
		cronExpr = seconds + " " + minutes + " " + hours + " * * ?";
		return cronExpr;
	}
}