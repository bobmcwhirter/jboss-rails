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
package org.jboss.rails.runtime.deployers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.rails.core.metadata.RailsApplicationMetaData;
import org.jboss.ruby.core.runtime.metadata.RubyLoadPathMetaData;
import org.jboss.ruby.core.runtime.metadata.RubyRuntimeMetaData;
import org.jboss.virtual.VirtualFile;

public class RailsRubyRuntimeFactoryDescriber extends AbstractDeployer {

	public RailsRubyRuntimeFactoryDescriber() {
		setStage(DeploymentStages.PRE_DESCRIBE);
		setInput(RailsApplicationMetaData.class);
		addOutput(RubyRuntimeMetaData.class);
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		if (unit instanceof VFSDeploymentUnit) {
			deploy((VFSDeploymentUnit) unit);
		}
	}

	public void deploy(VFSDeploymentUnit unit) throws DeploymentException {
		RailsApplicationMetaData railsMetaData = unit.getAttachment(RailsApplicationMetaData.class);
		
		RubyRuntimeMetaData runtimeMetaData = unit.getAttachment(RubyRuntimeMetaData.class);
		if (runtimeMetaData == null) {
			runtimeMetaData = new RubyRuntimeMetaData();
			runtimeMetaData.setBaseDir( railsMetaData.getRailsRoot() );
			unit.addAttachment(RubyRuntimeMetaData.class, runtimeMetaData);
		}

		addRuntimeInitializer(runtimeMetaData, railsMetaData );
		
		try {
			addRailsRootLoadPath(runtimeMetaData, railsMetaData);
			addRailtiesLibLoadPath(runtimeMetaData, railsMetaData);
			VirtualFile baseDir = unit.getRoot();
			unit.addAttachment(VirtualFile.class.getName() + "$ruby.baseDir", baseDir);
		} catch (MalformedURLException e) {
			throw new DeploymentException(e);
		} catch (URISyntaxException e) {
			throw new DeploymentException(e);
		} catch (IOException e) {
			throw new DeploymentException(e);
		}

	}

	protected void addRuntimeInitializer(RubyRuntimeMetaData runtimeMetaData, RailsApplicationMetaData railsMetaData) {
		RailsRuntimeInitializer initializer = createRuntimeInitializer(railsMetaData.getRailsRoot(), railsMetaData
				.getRailsEnv());

		runtimeMetaData.setRuntimeInitializer(initializer);
	}

	protected void addRailsRootLoadPath(RubyRuntimeMetaData runtimeMetaData, RailsApplicationMetaData railsMetaData) throws MalformedURLException, URISyntaxException {
		RubyLoadPathMetaData railsRootPath = new RubyLoadPathMetaData();
		railsRootPath.setURL( railsMetaData.getRailsRoot().toURL() );
		runtimeMetaData.appendLoadPath( railsRootPath );
	}

	protected void addRailtiesLibLoadPath(RubyRuntimeMetaData runtimeMetaData, RailsApplicationMetaData railsMetaData) throws IOException, URISyntaxException {
		VirtualFile railtiesLib = railsMetaData.getRailsRoot().getChild("vendor/rails/railties/lib");
		if (railtiesLib != null) {
			RubyLoadPathMetaData railtiesPath = new RubyLoadPathMetaData();
			railtiesPath.setURL(railtiesLib.toURL());
			runtimeMetaData.appendLoadPath( railtiesPath );
		}
	}

	public RailsRuntimeInitializer createRuntimeInitializer(VirtualFile railsRoot, String railsEnv) {
		return new RailsRuntimeInitializer(railsRoot, railsEnv);
	}

}
