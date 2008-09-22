/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.rails.deploy;

import javax.management.ObjectName;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.rails.metadata.RailsMetaData;
import org.jboss.system.metadata.ServiceConstructorMetaData;
import org.jboss.system.metadata.ServiceMetaData;

public class RailsAppDeployer extends AbstractDeployer {

	public RailsAppDeployer() {
		setStage(DeploymentStages.REAL);
		setTopLevelOnly(true);
		setInput(RailsMetaData.class);
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		if (!(unit instanceof VFSDeploymentUnit)) {
			throw new DeploymentException("deployment unit must be a VFSDeploymentUnit");
		}

		VFSDeploymentUnit vfsUnit = (VFSDeploymentUnit) unit;
		RailsMetaData railsMetaData = vfsUnit.getAttachment(RailsMetaData.class);
		if (railsMetaData == null) {
			return;
		}

		doDeploy(vfsUnit, railsMetaData);
	}

	protected void doDeploy(VFSDeploymentUnit unit, RailsMetaData railsMetaData) throws DeploymentException {
		try {
			RailsDeployment deployment = new RailsDeployment();
			ServiceMetaData railsModule = new ServiceMetaData();
			String name = getObjectName(railsMetaData);
			ObjectName objectName = new ObjectName(name);
			railsModule.setObjectName(objectName);
			railsModule.setCode(RailsModule.class.getName());
			ServiceConstructorMetaData constructor = new ServiceConstructorMetaData();
			constructor.setSignature(new String[] { VFSDeploymentUnit.class.getName(), RailsAppDeployer.class.getName(), RailsDeployment.class.getName() });
			constructor.setParameters(new Object[] { unit, this, deployment });
			railsModule.setConstructor(constructor);
			unit.addAttachment("RailsServiceMetaData", railsModule, ServiceMetaData.class);
		} catch (Throwable e) {
			throw new DeploymentException(e);
		}
	}

	private String getObjectName(RailsMetaData railsMetaData) {
		String ctxPath = "/ballast";
		String objectName = "jboss.rails.deployment:root=" + ctxPath;
		return objectName;
	}

}