/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.ruby.enterprise.web.rack.deployers;

import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.ValueMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.vfs.spi.deployer.AbstractSimpleVFSRealDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.ruby.enterprise.web.rack.RackApplicationFactory;
import org.jboss.ruby.enterprise.web.rack.RackApplicationPool;
import org.jboss.ruby.enterprise.web.rack.RubyRackApplicationFactory;
import org.jboss.ruby.enterprise.web.rack.SharedRackApplicationPool;
import org.jboss.ruby.enterprise.web.rack.metadata.RubyRackApplicationMetaData;
import org.jboss.ruby.runtime.RubyRuntimeFactory;

public class RubyRackApplicationFactoryDeployer extends AbstractSimpleVFSRealDeployer<RubyRackApplicationMetaData> {

	private static final Logger log = Logger.getLogger(RubyRackApplicationFactoryDeployer.class);

	public RubyRackApplicationFactoryDeployer() {
		super(RubyRackApplicationMetaData.class);
		addOutput(BeanMetaData.class);
	}

	@Override
	public void deploy(VFSDeploymentUnit unit, RubyRackApplicationMetaData metaData) throws DeploymentException {
		String beanName = "jboss.rack.app." + unit.getSimpleName();

		log.debug("deploying rack app factory: " + beanName);

		RubyRuntimeFactory factory = unit.getAttachment(RubyRuntimeFactory.class);

		BeanMetaDataBuilder builder = BeanMetaDataBuilder.createBuilder(beanName, RubyRackApplicationFactory.class.getName());
		builder.addPropertyMetaData("rubyRuntimeFactory", factory);
		builder.addPropertyMetaData("rackUpScript", metaData.getRackUpScript());

		BeanMetaData beanMetaData = builder.getBeanMetaData();

		unit
				.addAttachment(BeanMetaData.class.getName() + "$" + RackApplicationFactory.class.getName() + "$" + unit.getName(), beanMetaData, BeanMetaData.class);

		builder = BeanMetaDataBuilder.createBuilder(beanName + ".pool", SharedRackApplicationPool.class.getName());
		ValueMetaData appFactoryInjection = builder.createInject(beanName);
		builder.addConstructorParameter(RackApplicationFactory.class.getName(), appFactoryInjection);

		beanMetaData = builder.getBeanMetaData();

		unit.addAttachment(BeanMetaData.class.getName() + "$" + RackApplicationPool.class.getName() + "$" + unit.getName(), beanMetaData);
	}

}
