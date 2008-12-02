/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.ruby.enterprise.scheduler.deployers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.ho.yaml.Yaml;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.vfs.spi.deployer.AbstractVFSParsingDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.rails.core.metadata.RailsApplicationMetaData;
import org.jboss.ruby.enterprise.scheduler.metadata.ScheduleMetaData;
import org.jboss.ruby.enterprise.scheduler.metadata.ScheduleTaskMetaData;
import org.jboss.virtual.VirtualFile;

public class YamlScheduleParsingDeployer extends AbstractVFSParsingDeployer<ScheduleMetaData> {

	public YamlScheduleParsingDeployer() {
		super(ScheduleMetaData.class);
		addInput(RailsApplicationMetaData.class);
		setName("jboss-scheduler.yml");
		setBuildManagedObject(true);
		setJarExtension( ".rails" );
	}

	@Override
	protected ScheduleMetaData parse(VFSDeploymentUnit unit, VirtualFile file, ScheduleMetaData root) throws Exception {
		RailsApplicationMetaData railsMetaData = unit.getAttachment(RailsApplicationMetaData.class);
		return parseSchedulerYaml(railsMetaData.getRailsRootPath(), railsMetaData.getRailsEnv(), file);
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
			throw new DeploymentException(e);
		} finally {
			file.closeStreams();
		}
	}
}