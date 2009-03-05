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
package org.jboss.ruby.runtime.deployers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.vfs.spi.deployer.AbstractSimpleVFSRealDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.kernel.Kernel;
import org.jboss.ruby.runtime.DefaultRubyRuntimeFactory;
import org.jboss.ruby.runtime.RubyDynamicClassLoader;
import org.jboss.ruby.runtime.RubyRuntimeFactory;
import org.jboss.ruby.runtime.metadata.RubyLoadPathMetaData;
import org.jboss.ruby.runtime.metadata.RubyRuntimeMetaData;
import org.jboss.virtual.VirtualFile;

public class RubyRuntimeFactoryDeployer extends AbstractSimpleVFSRealDeployer<RubyRuntimeMetaData> {

	private Kernel kernel;

	public RubyRuntimeFactoryDeployer() {
		super(RubyRuntimeMetaData.class);
		setStage(DeploymentStages.CLASSLOADER);
	}

	public void setKernel(Kernel kernel) {
		this.kernel = kernel;
	}

	public Kernel getKernel() {
		return this.kernel;
	}

	@Override
	public void deploy(VFSDeploymentUnit unit, RubyRuntimeMetaData metaData) throws DeploymentException {
		String factoryName = "jboss.ruby.runtime.factory." + unit.getSimpleName();
		log.trace("creating RubyRuntimeFactory: " + factoryName);

		DefaultRubyRuntimeFactory factory = new DefaultRubyRuntimeFactory(metaData.getRuntimeInitializer() );
		factory.setKernel(this.kernel);

		try {
			RubyDynamicClassLoader classLoader = createClassLoader(unit);
			factory.setClassLoader(classLoader);
			unit.addAttachment(RubyDynamicClassLoader.class, classLoader);
		} catch (MalformedURLException e) {
			throw new DeploymentException(e);
		}

		unit.addAttachment(RubyRuntimeFactory.class, factory);

	}

	private RubyDynamicClassLoader createClassLoader(VFSDeploymentUnit unit) throws MalformedURLException {

		Set<? extends RubyLoadPathMetaData> allMetaData = unit.getAllMetaData(RubyLoadPathMetaData.class);

		Set<URL> urls = new HashSet<URL>();

		for (RubyLoadPathMetaData each : allMetaData) {
			urls.add(each.getURL());
		}
		
		VirtualFile baseDir = unit.getAttachment( VirtualFile.class.getName() + "$ruby.baseDir", VirtualFile.class );

		RubyDynamicClassLoader classLoader = RubyDynamicClassLoader.create(unit.getSimpleName(), urls, unit.getClassLoader(), baseDir );
		return classLoader;
	}

	@Override
	public void undeploy(VFSDeploymentUnit unit, RubyRuntimeMetaData deployment) {
		RubyDynamicClassLoader cl = unit.getAttachment( RubyDynamicClassLoader.class );
		
		if ( cl != null ) {
			cl.destroy();
		}
	}
	
	

}
