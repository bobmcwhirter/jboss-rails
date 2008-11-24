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

import org.jboss.deployers.vfs.spi.deployer.AbstractVFSParsingDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.rails.core.metadata.RailsMetaData;
import org.jboss.virtual.VirtualFile;

public class RailsEnvironmentParsingDeployer extends AbstractVFSParsingDeployer<RailsMetaData> {
	public RailsEnvironmentParsingDeployer() {
		super(RailsMetaData.class);
		setName("environment.rb");
		setTopLevelOnly(false);
	}

	@Override
	protected RailsMetaData parse(VFSDeploymentUnit unit, VirtualFile file, RailsMetaData root) throws Exception {
		log.info("Parsing " + file + " for " + unit.getRoot());
		String railsRoot = unit.getRoot().toURL().getFile();
		if ( unit.isAttachmentPresent( RailsMetaData.class ) ) {
			log.info( "RailsMetaData present, doing nothing." );
			return root;
		}
		RailsMetaData railsMetaData = new RailsMetaData();
		railsMetaData.setRailsRoot( railsRoot );
		unit.addAttachment( RailsMetaData.class, railsMetaData );
		return railsMetaData;
	}
}