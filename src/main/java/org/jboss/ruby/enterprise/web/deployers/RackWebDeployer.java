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
package org.jboss.ruby.enterprise.web.deployers;

import javax.management.MBeanServer;

import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.helpers.AbstractSimpleRealDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.ruby.enterprise.web.RackWebDeployment;
import org.jboss.ruby.enterprise.web.metadata.RackWebMetaData;

/**
 * Deployer that consults RailsMetaData to deploy a Rails web application, for
 * real.
 * 
 * @author Bob McWhirter
 */
public class RackWebDeployer extends AbstractSimpleRealDeployer<RackWebMetaData> {

	private MBeanServer mbeanServer;

	/**
	 * Construct.
	 */
	public RackWebDeployer() {
		super(RackWebMetaData.class);
		setTopLevelOnly(true);
		addOutput(BeanMetaData.class);
	}

	public void setMbeanServer(MBeanServer mbeanServer) {
		this.mbeanServer = mbeanServer;
	}

	public MBeanServer getMbeanServer() {
		return this.mbeanServer;
	}

	@Override
	public void deploy(DeploymentUnit unit, RackWebMetaData rackWebMetaData) throws DeploymentException {
		
		BeanMetaDataBuilder builder = BeanMetaDataBuilder.createBuilder("jboss.ruby.web." + unit.getSimpleName(), RackWebDeployment.class.getName());
		
		builder.addPropertyMetaData("rackWebMetaData", rackWebMetaData);
		builder.addPropertyMetaData( "classLoader", unit.getClassLoader() );
		builder.addAnnotation("@org.jboss.aop.microcontainer.aspects.jmx.JMX(registerDirectly=true, exposedInterface=void.class, name=\"jboss.ruby.web:app="
				+ unit.getSimpleName() + "\")");
		BeanMetaData rackWebDeployment = builder.getBeanMetaData();
		unit.addAttachment(BeanMetaData.class.getName() + "$RackWebDeployment", rackWebDeployment, BeanMetaData.class);
	}
}