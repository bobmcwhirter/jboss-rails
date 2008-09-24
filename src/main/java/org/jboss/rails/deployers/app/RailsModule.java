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
package org.jboss.rails.deployers.app;

import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.rails.metadata.RailsMetaData;

/** Top-level managed deployment object.
 * 
 * <p>
 * The deployment is auto-start()'d by the manager.
 * </p>
 * 
 * @see RailsDeployment
 * 
 * @author Bob McWhirter
 */
public class RailsModule implements RailsModuleMBean {

	private Logger log = Logger.getLogger(RailsModule.class);
	
	private VFSDeploymentUnit unit;
	private RailsAppDeployer deployer;
	private RailsDeployment deployment;

	public RailsModule(VFSDeploymentUnit unit, RailsAppDeployer deployer, RailsDeployment deployment) {
		this.unit = unit;
		this.deployer = deployer;
		this.deployment = deployment;
	}

	public void create() throws Exception {
		if (log.isTraceEnabled()) {
			log.trace("create()");
		}
	}

	public void destroy() throws Exception {
		if (log.isTraceEnabled()) {
			log.trace("destroy()");
		}
	}

	public void start() throws Exception {
		if (log.isTraceEnabled()) {
			log.trace("start()");
		}
		deployment.start(unit.getAttachment(RailsMetaData.class));
	}

	public void stop() throws Exception {
		if (log.isTraceEnabled()) {
			log.trace("stop()");
		}
		deployment.stop(unit.getAttachment(RailsMetaData.class));
	}

}
