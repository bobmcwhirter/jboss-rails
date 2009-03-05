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
package org.jboss.rails.endpoints.deployers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.helpers.AbstractParsingDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.rails.core.metadata.RailsApplicationMetaData;
import org.jboss.ruby.enterprise.endpoints.metadata.RubyEndpointMetaData;
import org.jboss.ruby.runtime.metadata.RubyLoadPathMetaData;
import org.jboss.ruby.util.StringUtils;
import org.jboss.virtual.VirtualFile;
import org.jboss.virtual.plugins.vfs.helpers.SuffixMatchFilter;

public class RailsEndpointsParsingDeployer extends AbstractParsingDeployer {

	private static final String ENDPOINTS_DIR = "app/endpoints/";
	private static final String SUFFIX = ".wsdl";
	private static final SuffixMatchFilter SUFFIX_FILTER = new SuffixMatchFilter(SUFFIX);
	
	private static final String DEFAULT_TRUST_STORE = "auth/truststore.jks";

	public RailsEndpointsParsingDeployer() {
		setInput(RailsApplicationMetaData.class);
		addOutput(RubyEndpointMetaData.class);
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		if (unit instanceof VFSDeploymentUnit) {
			deploy((VFSDeploymentUnit) unit);
		}
	}

	public void deploy(VFSDeploymentUnit unit) throws DeploymentException {

		try {
			VirtualFile endpointsDir = unit.getRoot().getChild(ENDPOINTS_DIR);

			if (endpointsDir != null) {
				addLoadPath(unit, endpointsDir);
				scanEndpointsDir(unit, endpointsDir);
			}
		} catch (IOException e) {
			throw new DeploymentException(e);
		} catch (URISyntaxException e) {
			throw new DeploymentException(e);
		}

	}

	protected void addLoadPath(DeploymentUnit unit, VirtualFile endpointsDir) throws MalformedURLException, URISyntaxException {
		RubyLoadPathMetaData loadPathMetaData = new RubyLoadPathMetaData();

		loadPathMetaData.setURL(endpointsDir.toURL());

		unit.addAttachment(RubyLoadPathMetaData.class.getName() + "$" + unit.getName() + "$endpoints", loadPathMetaData, RubyLoadPathMetaData.class);
	}

	protected void scanEndpointsDir(DeploymentUnit unit, VirtualFile endpointsDir) throws IOException, URISyntaxException {
		List<VirtualFile> wsdls = endpointsDir.getChildren(new SuffixMatchFilter(".wsdl"));

		for (VirtualFile wsdl : wsdls) {
			deploy(unit, wsdl);
		}

	}

	protected void deploy(DeploymentUnit unit, VirtualFile wsdl) throws IOException, URISyntaxException {
		String wsdlFileName = wsdl.getName();
		String name = wsdlFileName.substring(0, wsdlFileName.length() - SUFFIX.length());
		String classLocation = name + "_endpoint";
		String rubyFileName = classLocation + ".rb";
		VirtualFile rubyFile = wsdl.getParent().getChild(rubyFileName);
		if (rubyFile == null) {
			log.warn("No Ruby endpoint handler definition '" + name + SUFFIX + "' found for WSDL: " + wsdl);
			return;
		}
		RubyEndpointMetaData metaData = new RubyEndpointMetaData();

		metaData.setName(name);
		metaData.setEndpointClassName(getEndpointClassName(name));
		metaData.setWsdlLocation(wsdl.toURL());
		metaData.setClassLocation(classLocation);
		unit.addAttachment(RubyEndpointMetaData.class.getName() + "$" + name, metaData, RubyEndpointMetaData.class);
	}

	private String getEndpointClassName(String name) {
		return StringUtils.camelize(name + "Endpoint", false);
	}

}
