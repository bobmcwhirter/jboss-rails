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

import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.mx.util.MBeanServerLocator;
import org.jboss.rails.core.metadata.RailsMetaData;

/**
 * Deployer that consules RailsMetaData to deploy a rails application, for real.
 * 
 * @author Bob McWhirter
 */
public class RailsAppDeployer extends AbstractDeployer {

	/**
	 * Construct.
	 */
	public RailsAppDeployer() {
		setStage(DeploymentStages.REAL);
		setTopLevelOnly(true);
		setInput(RailsMetaData.class);
		addOutput(BeanMetaData.class);
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		if (!(unit instanceof VFSDeploymentUnit)) {
			throw new DeploymentException("deployment unit must be a VFSDeploymentUnit");
		}

		log.info("Deploy: " + unit);
		VFSDeploymentUnit vfsUnit = (VFSDeploymentUnit) unit;
		RailsMetaData railsMetaData = vfsUnit.getAttachment(RailsMetaData.class);
		if (railsMetaData == null) {
			return;
		}

		doDeploy(vfsUnit, railsMetaData);
	}

	@Override
	public void undeploy(DeploymentUnit unit) {
		log.info("Undeploy: " + unit);
		super.undeploy(unit);
	}

	protected void doDeploy(VFSDeploymentUnit unit, RailsMetaData railsMetaData) throws DeploymentException {
		if (log.isTraceEnabled()) {
			log.trace("doDeploy(" + unit.getRoot() + ", ...)");
		}
		try {
			//RailsDeployment deployment = new RailsDeployment();
			//deployment.setMBeanServer(MBeanServerLocator.locateJBoss());
			//BeanMetaDataBuilder builder = BeanMetaDataBuilder.createBuilder("jboss.rails." + unit.getSimpleName(), RailsModule.class.getName());
			BeanMetaDataBuilder builder = BeanMetaDataBuilder.createBuilder("jboss.rails." + unit.getSimpleName(), RailsDeployment.class.getName());
			builder.addPropertyMetaData( "MBeanServer", MBeanServerLocator.locateJBoss() );
			builder.addPropertyMetaData( "railsMetaData", railsMetaData );
			BeanMetaData railsDeployment = builder.getBeanMetaData();
			unit.addAttachment(BeanMetaData.class.getName() + "$RailsDeployment", railsDeployment, BeanMetaData.class);
		} catch (Throwable e) {
			throw new DeploymentException(e);
		}
	}
}