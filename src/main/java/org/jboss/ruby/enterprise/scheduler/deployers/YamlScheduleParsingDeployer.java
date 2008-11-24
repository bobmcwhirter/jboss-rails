package org.jboss.ruby.enterprise.scheduler.deployers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.ho.yaml.Yaml;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.vfs.spi.deployer.AbstractVFSParsingDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.rails.core.metadata.RailsMetaData;
import org.jboss.ruby.enterprise.scheduler.RubyJob;
import org.jboss.ruby.enterprise.scheduler.metadata.ScheduleMetaData;
import org.jboss.ruby.enterprise.scheduler.metadata.ScheduleTaskMetaData;
import org.jboss.virtual.VirtualFile;

public class YamlScheduleParsingDeployer extends AbstractVFSParsingDeployer<ScheduleMetaData> {

	public YamlScheduleParsingDeployer() {
		super(ScheduleMetaData.class);
		addInput(RailsMetaData.class);
		setName("jboss-scheduler.yml");
		setBuildManagedObject(true);
	}

	@Override
	protected ScheduleMetaData parse(VFSDeploymentUnit unit, VirtualFile file, ScheduleMetaData root) throws Exception {
		RailsMetaData railsMetaData = unit.getAttachment(RailsMetaData.class);
		return parseSchedulerYaml(railsMetaData.getRailsRoot(), railsMetaData.getEnvironment(), file);
	}

	@SuppressWarnings("unchecked")
	private ScheduleMetaData parseSchedulerYaml(String railsRoot, String railsEnv, VirtualFile file) throws DeploymentException {
		try {
			Map<String, Map<String, String>> results = (Map<String, Map<String, String>>) Yaml.load(file.openStream());

			ScheduleMetaData scheduleMetaData = new ScheduleMetaData();

			for (String jobName : results.keySet()) {
				Map<String, String> jobSpec = results.get(jobName);
				String description = jobSpec.get( "description" );
				String task        = jobSpec.get( "task" );
				String cron        = jobSpec.get( "cron" );
				
				ScheduleTaskMetaData taskMetaData = new ScheduleTaskMetaData();
				
				taskMetaData.setName(jobName);
				taskMetaData.setGroup(railsRoot);
				taskMetaData.setDescription(description);
				taskMetaData.setRubyClass( task );
				taskMetaData.setCronExpression( cron.trim() );
				scheduleMetaData.addScheduledTask(taskMetaData);
			}

			return scheduleMetaData;
		} catch (IOException e) {
			file.closeStreams();
			throw new DeploymentException(e);
		}
	}
}