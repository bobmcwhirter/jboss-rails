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
package org.jboss.rails.core.deployers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.ho.yaml.Yaml;
import org.jboss.deployers.client.spi.Deployment;
import org.jboss.deployers.client.spi.main.MainDeployer;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.attachments.MutableAttachments;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.structure.ContextInfo;
import org.jboss.deployers.spi.structure.StructureMetaData;
import org.jboss.deployers.spi.structure.StructureMetaDataFactory;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.deployers.vfs.plugins.client.AbstractVFSDeployment;
import org.jboss.deployers.vfs.spi.deployer.AbstractVFSParsingDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.rails.core.metadata.RailsApplicationMetaData;
import org.jboss.ruby.enterprise.web.metadata.RackWebMetaData;
import org.jboss.virtual.VFS;
import org.jboss.virtual.VirtualFile;

//public class RailsParsingDeployer extends AbstractVFSParsingDeployer<RailsMetaData> {
public class RailsRootReferenceParsingDeployer extends AbstractVFSParsingDeployer<RailsApplicationMetaData> {

	private Logger log = Logger.getLogger(RailsRootReferenceParsingDeployer.class);

	public RailsRootReferenceParsingDeployer() {
		super(RailsApplicationMetaData.class);
		addOutput(RackWebMetaData.class);
		setSuffix("-rails.yml");
		setStage(DeploymentStages.REAL);
		setTopLevelOnly(true);
	}

	@Override
	protected RailsApplicationMetaData parse(VFSDeploymentUnit vfsUnit, VirtualFile file, RailsApplicationMetaData root) throws Exception {

		if (!file.equals(vfsUnit.getRoot())) {
			log.debug("not deploying non-root: " + file);
			return null;
		}

		log.debug("deploying: " + file);

		Deployment deployment = parseAndSetUp(file);

		performDeploy(vfsUnit, deployment);

		// Returning null since the RailsMetaData is actually
		// attached as a predetermined managed object on the
		// sub-deployment, and not directly applicable
		// to *this* deployment unit.
		return null;

	}

	@Override
	public void undeploy(DeploymentUnit unit) {
		log.trace("attempting undeploy from: " + unit.getName());
		Deployment deployment = unit.getAttachment("jboss.rails.root.deployment", Deployment.class);
		if (deployment != null) {
			log.debug("Undeploying: " + deployment.getName());
			MainDeployer deployer = unit.getAttachment("jboss.rails.root.deployer", MainDeployer.class);
			try {
				deployer.removeDeployment(deployment);
				deployer.process();
			} catch (DeploymentException e) {
				log.error(e);
			}
		}
	}

	private void performDeploy(DeploymentUnit unit, Deployment deployment) throws DeploymentException {
		MainDeployer deployer = unit.getMainDeployer();
		deployer.addDeployment(deployment);
		deployer.process();
		deployer.checkComplete(deployment);
		unit.addAttachment("jboss.rails.root.deployment", deployment);
		unit.addAttachment("jboss.rails.root.deployer", deployer);
	}

	private Deployment createDeployment(RailsApplicationMetaData railsMetaData, RackWebMetaData webMetaData) throws MalformedURLException, IOException {
		Deployment deployment = new AbstractVFSDeployment(railsMetaData.getRailsRoot());

		MutableAttachments attachments = ((MutableAttachments) deployment.getPredeterminedManagedObjects());

		attachments.addAttachment(RailsApplicationMetaData.class, railsMetaData);

		if (webMetaData != null) {
			attachments.addAttachment(RackWebMetaData.class, webMetaData);
		}

		return deployment;
	}

	@SuppressWarnings("unchecked")
	private Deployment parseAndSetUp(VirtualFile file) throws DeploymentException {
		try {
			Map<String, Object> results = (Map<String, Object>) Yaml.load(file.openStream());

			Map<String, Object> application = (Map<String, Object>) results.get("application");
			Map<String, Object> web = (Map<String, Object>) results.get("web");

			RailsApplicationMetaData railsMetaData = new RailsApplicationMetaData();

			if (application != null) {
				String railsRoot = (String) application.get("RAILS_ROOT");
				String railsEnv = (String) application.get("RAILS_ENV");
				URL railsRootUrl = new URL("file://" + railsRoot);
				VirtualFile railsRootFile = VFS.getRoot(railsRootUrl);
				railsMetaData.setRailsRoot(railsRootFile);
				railsMetaData.setRailsEnv(railsEnv);
			}

			RackWebMetaData webMetaData = null;
			
			if (web != null) {
				String context = (String) web.get("context");
				String host = (String) web.get("host");
				webMetaData = new RackWebMetaData(context, host);
			}

			return createDeployment(railsMetaData, webMetaData);

		} catch (IOException e) {
			file.closeStreams();
			throw new DeploymentException(e);
		}
	}

}
