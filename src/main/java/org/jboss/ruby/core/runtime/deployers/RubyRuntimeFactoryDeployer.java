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
package org.jboss.ruby.core.runtime.deployers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.vfs.spi.deployer.AbstractSimpleVFSRealDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.kernel.Kernel;
import org.jboss.ruby.core.DefaultRubyRuntimeFactory;
import org.jboss.ruby.core.RubyDynamicClassLoader;
import org.jboss.ruby.core.runtime.metadata.RubyLoadPathMetaData;
import org.jboss.ruby.core.runtime.metadata.RubyRuntimeMetaData;
import org.jboss.ruby.core.runtime.spi.RubyRuntimeFactory;
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
		DefaultRubyRuntimeFactory factory = new DefaultRubyRuntimeFactory(metaData.getRuntimeInitializer());
		factory.setKernel(this.kernel);
		factory.setApplicationName(unit.getSimpleName());

		try {
			RubyDynamicClassLoader classLoader = createClassLoader(unit, metaData);
			factory.setClassLoader(classLoader);
			unit.addAttachment(RubyDynamicClassLoader.class, classLoader);
		} catch (MalformedURLException e) {
			throw new DeploymentException(e);
		}

		unit.addAttachment(RubyRuntimeFactory.class, factory);

	}

	private RubyDynamicClassLoader createClassLoader(VFSDeploymentUnit unit, RubyRuntimeMetaData metaData)
			throws MalformedURLException {

		List<URL> urls = new ArrayList<URL>();

		for (RubyLoadPathMetaData each : metaData.getLoadPaths()) {
			urls.add(each.getURL());
		}

		VirtualFile baseDir = metaData.getBaseDir();

		if (baseDir == null) {
			baseDir = unit.getRoot();
		}

		ClassLoader parentClassLoader = null;
		try {
			parentClassLoader = unit.getClassLoader();
		} catch (IllegalStateException e) {
			parentClassLoader = getClass().getClassLoader();
		}

		RubyDynamicClassLoader classLoader = RubyDynamicClassLoader.create(unit.getSimpleName(), urls, parentClassLoader, baseDir);
		return classLoader;
	}

	@Override
	public void undeploy(VFSDeploymentUnit unit, RubyRuntimeMetaData deployment) {
		RubyDynamicClassLoader cl = unit.getAttachment(RubyDynamicClassLoader.class);

		if (cl != null) {
			cl.destroy();
		}
	}

}
