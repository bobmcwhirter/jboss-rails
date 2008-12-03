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
package org.jboss.rails.core.deployers;

import java.util.Map;

import org.ho.yaml.Yaml;
import org.jboss.deployers.vfs.spi.deployer.AbstractVFSParsingDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.rails.core.metadata.RailsApplicationMetaData;
import org.jboss.virtual.VirtualFile;

public class RailsEnvironmentParsingDeployer extends AbstractVFSParsingDeployer<RailsApplicationMetaData> {
	public RailsEnvironmentParsingDeployer() {
		super(RailsApplicationMetaData.class);
		// setName("environment.rb");
		setName("jboss-rails-env.yml");
		setTopLevelOnly(false);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected RailsApplicationMetaData parse(VFSDeploymentUnit unit, VirtualFile file, RailsApplicationMetaData root) throws Exception {
		try {
			Map<String, String> parsed = (Map<String, String>) Yaml.load(file.openStream());

			String railsEnv = parsed.get("RAILS_ENV");

			if (railsEnv == null || railsEnv.trim().equals("")) {
				railsEnv = "development";
			}

			RailsApplicationMetaData railsMetaData = new RailsApplicationMetaData(unit.getRoot(), railsEnv);
			unit.addAttachment(RailsApplicationMetaData.class, railsMetaData);
			return railsMetaData;
		} finally {
			file.closeStreams();
		}
	}
}