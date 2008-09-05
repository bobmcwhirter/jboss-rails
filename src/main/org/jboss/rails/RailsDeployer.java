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
package org.jboss.rails;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.management.ObjectName;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.ClassLoaderFactory;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.metadata.web.jboss.JBoss50WebMetaData;
import org.jboss.metadata.web.jboss.JBossWebMetaData;
import org.jboss.metadata.web.spec.Web25MetaData;
import org.jboss.metadata.web.spec.WebMetaData;
import org.jboss.mx.util.ObjectNameFactory;
import org.jboss.security.plugins.JaasSecurityManagerServiceMBean;
import org.jboss.virtual.VFS;
import org.jboss.virtual.VirtualFile;
import org.jboss.virtual.plugins.context.file.FileHandler;
import org.jboss.virtual.spi.VirtualFileHandler;
import org.jboss.web.deployers.AbstractWarDeployer;
import org.jboss.web.deployers.AbstractWarDeployment;
import org.jboss.web.tomcat.service.deployers.TomcatDeployer;
import org.jboss.web.tomcat.service.session.SessionIDGenerator;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.builder.JBossXBBuilder;

public class RailsDeployer extends AbstractDeployer
// implements RailsDeployerMBean
{
	private static String NAME = "RailsDeployer";

	public RailsDeployer() {
		setStage(DeploymentStages.PRE_REAL);
		setInput(WebMetaData.class);
		setAllInputs(true);
	}

	public String getName() {
		return NAME;
	}

	public void start() throws Exception {
		log.info("Rails Deployer starting");
	}

	public void stop() throws Exception {
		log.info("Rails Deployer stopping");
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		// Ignore non-vfs deployments
		if (unit instanceof VFSDeploymentUnit == false) {
			return;
		}

		VFSDeploymentUnit vfsUnit = (VFSDeploymentUnit) unit;

		if (vfsUnit.getRoot().getName().endsWith(".rails")) {
			deployRailsApplication(vfsUnit);
		}
	}

	private void deployRailsApplication(VFSDeploymentUnit unit)
			throws DeploymentException {
		log.info("deployRailsApplication(" + unit.getSimpleName() + ")");

		VirtualFile file = unit.getRoot();

		try {
			if (file.isLeaf()) {
				deployRailsReference(unit);
			} else {
				deployRailsDirectory(unit, unit.getRoot());
			}
		} catch (IOException e) {
			throw new DeploymentException(e);
		}
	}

	private void deployRailsReference(VFSDeploymentUnit unit)
			throws DeploymentException {
		log.info("deploy from a reference");
		VirtualFile ref = unit.getRoot();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(ref
					.openStream()));
			String location = in.readLine();
			log.info("deploy from referenced directory: " + location);
			if (location != null) {
				URI dirUri = new URI(location);
				log.info("deploy from referenced directory URI: " + dirUri);
				VirtualFile dir = VFS.getRoot( dirUri );
				deployRailsDirectory(unit, dir);
			}
		} catch (IOException e) {
			throw new DeploymentException(e);
		} catch (URISyntaxException e) {
			throw new DeploymentException(e);
		} finally {
			ref.closeStreams();
		}
	}

	private void deployRailsDirectory(VFSDeploymentUnit unit, VirtualFile dir) {
		log.info("deploy from a directory");
	}

	/*
	 * public void deploy(VFSDeploymentUnit unit, JBossWebMetaData metaData)
	 * throws DeploymentException { log.info("Rails Deployer deploy(" +
	 * unit.getSimpleName() + ", " + metaData + ")"); }
	 */

	public void create() throws Exception {
		log.info("Rails Deployer create()");
	}

	public void destroy() throws Exception {
		log.info("Rails Deployer destroy()");
	}

}
