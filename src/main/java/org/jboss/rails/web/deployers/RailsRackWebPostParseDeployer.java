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
package org.jboss.rails.web.deployers;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractSimpleRealDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.rails.core.metadata.RailsApplicationMetaData;
import org.jboss.ruby.enterprise.web.metadata.RackWebMetaData;

public class RailsRackWebPostParseDeployer extends AbstractSimpleRealDeployer<RackWebMetaData> {

	private static final String CONTEXT_CONFIG_CLASS_NAME = "org.jboss.rails.web.tomcat.RailsContextConfig";

	private static Logger log = Logger.getLogger(RailsRackWebPostParseDeployer.class);

	public RailsRackWebPostParseDeployer() {
		super(RackWebMetaData.class);
		addInput(RailsApplicationMetaData.class);
		addOutput(RackWebMetaData.class);
		setStage(DeploymentStages.POST_PARSE);
	}

	@Override
	public void deploy(DeploymentUnit unit, RackWebMetaData rackMetaData) throws DeploymentException {
		rackMetaData.setContextConfigClassName(CONTEXT_CONFIG_CLASS_NAME);
		RailsApplicationMetaData railsAppMetaData = unit.getAttachment(RailsApplicationMetaData.class);
		String docBase = railsAppMetaData.getRailsRootPath() + "/public";
		log.info( "DOCBASE = [" + docBase + "]" );
		rackMetaData.setDocBase(docBase);

		rackMetaData.setFrameworkMetaData(railsAppMetaData);
	}

}
