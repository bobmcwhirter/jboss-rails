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
package org.jboss.ruby.enterprise.jobs.deployers;

import java.util.Set;

import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.ValueMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.ruby.enterprise.jobs.RubyJob;
import org.jboss.ruby.enterprise.jobs.metadata.RubyJobMetaData;
import org.jboss.ruby.runtime.deployers.RubyRuntimePoolDeployer;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class RubyJobDeployer extends AbstractDeployer {

	public RubyJobDeployer() {
		setAllInputs(true);
		addOutput(BeanMetaData.class);
		setStage(DeploymentStages.REAL);
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		Set<? extends RubyJobMetaData> allMetaData = unit.getAllMetaData(RubyJobMetaData.class);

		if (allMetaData.size() == 0) {
			return;
		}

		StdSchedulerFactory factory = new StdSchedulerFactory();
		try {
			Scheduler scheduler = factory.getScheduler();

			for (RubyJobMetaData metaData : allMetaData) {
				deploy(unit, scheduler, metaData);
			}
		} catch (SchedulerException e) {
			throw new DeploymentException( e );
		}

	}

	protected void deploy(DeploymentUnit unit, Scheduler scheduler, RubyJobMetaData metaData) throws DeploymentException {
		String beanName = "jboss.ruby.jobs." + unit.getName() + "." + metaData.getName();
		
		log.info( "deploying job: " + beanName );

		BeanMetaDataBuilder builder = BeanMetaDataBuilder.createBuilder(beanName, RubyJob.class.getName());
		
		
		builder.addPropertyMetaData( "group", metaData.getGroup());
		builder.addPropertyMetaData( "name", metaData.getName() );
		builder.addPropertyMetaData( "rubyClassName", metaData.getRubyClassName());
		builder.addPropertyMetaData( "description", metaData.getDescription() );
		builder.addPropertyMetaData( "cronExpression", metaData.getCronExpression() );
		builder.addPropertyMetaData( "scheduler", scheduler);
		
		ValueMetaData poolInjection = builder.createInject( RubyRuntimePoolDeployer.getBeanName( unit ) );
		builder.addPropertyMetaData( "rubyRuntimePool", poolInjection);
		
		BeanMetaData beanMetaData = builder.getBeanMetaData();
		
		unit.addAttachment( BeanMetaData.class.getName() + "$" + RubyJob.class.getName() + "$" + beanName, beanMetaData, BeanMetaData.class );

	}


}
