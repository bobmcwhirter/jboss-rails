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

import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.ValueMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.helpers.AbstractSimpleRealDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.ruby.enterprise.scheduler.ScheduleDeployment;
import org.jboss.ruby.enterprise.scheduler.metadata.ScheduleMetaData;
import org.jboss.ruby.runtime.RubyRuntimeFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class ScheduleDeployer extends AbstractSimpleRealDeployer<ScheduleMetaData> {

	public ScheduleDeployer() {
		super(ScheduleMetaData.class);
		addOutput( BeanMetaData.class );
		setParentFirst(true);
	}

	@Override
	public void deploy(DeploymentUnit unit, ScheduleMetaData deployment) throws DeploymentException {
		
		StdSchedulerFactory factory = new StdSchedulerFactory();
		
		try {
			Scheduler scheduler = factory.getScheduler();
			BeanMetaDataBuilder builder = BeanMetaDataBuilder.createBuilder("jboss.ruby.scheduler:unit="+unit.getSimpleName() , ScheduleDeployment.class.getName() );
			
			ValueMetaData factoryValue = builder.createInject( "jboss.ruby.runtime.factory." + unit.getSimpleName() );
			builder.addConstructorParameter( ScheduleMetaData.class.getName(), deployment);
			builder.addConstructorParameter( RubyRuntimeFactory.class.getName(), factoryValue );
			builder.addPropertyMetaData( "scheduler", scheduler );
			builder.addAnnotation("@org.jboss.aop.microcontainer.aspects.jmx.JMX(registerDirectly=true, exposedInterface=void.class, name=\"jboss.ruby.scheduler:app=" + unit.getSimpleName() + "\")");
			BeanMetaData schedulerBean = builder.getBeanMetaData();
			unit.addAttachment( BeanMetaData.class, schedulerBean );
		} catch (SchedulerException e) {
			log.error(e);
		}
		
	}

}
