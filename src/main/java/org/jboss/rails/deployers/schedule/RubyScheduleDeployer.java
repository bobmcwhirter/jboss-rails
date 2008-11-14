package org.jboss.rails.deployers.schedule;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.ho.yaml.Yaml;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.vfs.spi.deployer.AbstractVFSParsingDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.rails.metadata.RailsMetaData;
import org.jboss.rails.metadata.schedule.ScheduleMetaData;
import org.jboss.rails.metadata.schedule.ScheduledTaskMetaData;
import org.jboss.rails.util.RuntimeUtils;
import org.jboss.virtual.VirtualFile;
import org.jruby.Ruby;
import org.jruby.runtime.builtin.IRubyObject;

public class RubyScheduleDeployer extends AbstractVFSParsingDeployer<ScheduleMetaData> {

	public RubyScheduleDeployer() {
		super(ScheduleMetaData.class);
		setAllInputs(true);
		setName("jboss-scheduler.yml");
	}

	@Override
	protected ScheduleMetaData parse(VFSDeploymentUnit unit, VirtualFile file, ScheduleMetaData root) throws Exception {
		log.debug("##### parsing " + file);

		RailsMetaData railsMetaData = unit.getAttachment(RailsMetaData.class);

		return parseSchedulerYml(railsMetaData.getRailsRoot(), railsMetaData.getEnvironment(), file);
	}

	@SuppressWarnings("unchecked")
	private ScheduleMetaData parseSchedulerYml(String railsRoot, String railsEnv, VirtualFile file) throws DeploymentException {
		try {
			Map<String, Map<String,String>> results = (Map<String, Map<String,String>>) Yaml.load(file.openStream());

			ScheduleMetaData metaData = new ScheduleMetaData();
			
			for ( String jobName : results.keySet() ) {
				Map<String,String> jobSpec = results.get( jobName );
				ScheduledTaskMetaData task = new ScheduledTaskMetaData();
				task.setName( jobName );
				task.setDescription( jobSpec.get( "description" ) );
				
				Map<String,Object> taskData = new HashMap<String,Object>();
				
				Ruby runtime = RuntimeUtils.createRuntime( railsRoot, railsEnv);
				runtime.evalScriptlet( "$: << '" + railsRoot + "/app/scheduler/'\n" );
				
				String taskLoadScript = "require '" +jobSpec.get( "task" ) + "'.underscore\n" +
									    "$task = " + jobSpec.get( "task" ) + ".new\n";
				
				IRubyObject taskObj = runtime.evalScriptlet(  taskLoadScript );
				
				taskData.put( "ruby.runtime", runtime );
				taskData.put( "ruby.task", taskObj );
				
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

	private String buildCronExpression(Map<String, String> jobSpec) {

		String cronExpr = jobSpec.get("cron");

		if (cronExpr != null) {
			return cronExpr.trim();
		}

		String seconds = jobSpec.get("seconds");
		if (seconds == null || seconds.trim().equals("")) {
			seconds = "*";
		} else {
			seconds = seconds.trim();
		}

		String minutes = jobSpec.get("minutes");
		if (minutes == null || minutes.trim().equals("")) {
			minutes = "*";
		} else {
			minutes = minutes.trim();
		}

		String hours = jobSpec.get("hours");
		if (hours == null || hours.trim().equals("")) {
			hours = "*";
		} else {
			hours = hours.trim();
		}

		cronExpr = seconds + " " + minutes + " " + hours + " * * ?";
		return cronExpr;
	}
}