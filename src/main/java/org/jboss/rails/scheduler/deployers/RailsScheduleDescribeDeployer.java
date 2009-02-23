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
package org.jboss.rails.scheduler.deployers;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractSimpleRealDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.rails.core.metadata.RailsApplicationMetaData;
import org.jboss.rails.core.metadata.RailsVersionMetaData;
import org.jboss.ruby.enterprise.scheduler.metadata.ScheduleMetaData;

public class RailsScheduleDescribeDeployer extends AbstractSimpleRealDeployer<ScheduleMetaData> {

	private static Logger log = Logger.getLogger(RailsScheduleDescribeDeployer.class);

	public RailsScheduleDescribeDeployer() {
		super(ScheduleMetaData.class);
		addInput(RailsApplicationMetaData.class);
		addInput(RailsVersionMetaData.class);
		addOutput(ScheduleMetaData.class);
		setStage(DeploymentStages.DESCRIBE);
	}

	@Override
	public void deploy(DeploymentUnit unit, ScheduleMetaData scheduleMetaData) throws DeploymentException {
		RailsVersionMetaData railsVersion = unit.getAttachment(RailsVersionMetaData.class);
		scheduleMetaData.setThreadSafe(railsVersion.isThreadSafe());
		RailsApplicationMetaData railsMetaData = unit.getAttachment(RailsApplicationMetaData.class);
		scheduleMetaData.addLoadPath(railsMetaData.getRailsRootPath() + "/app/scheduler");
		log.info("fixed up schedule with " + railsMetaData.getRailsRootPath() + " and " + railsVersion);
	}

}
