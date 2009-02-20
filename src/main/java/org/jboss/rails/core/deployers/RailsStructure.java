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
import java.net.URISyntaxException;
import java.util.List;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.attachments.MutableAttachments;
import org.jboss.deployers.spi.deployer.matchers.JarExtensionProvider;
import org.jboss.deployers.spi.structure.ClassPathEntry;
import org.jboss.deployers.spi.structure.ContextInfo;
import org.jboss.deployers.spi.structure.ModificationType;
import org.jboss.deployers.spi.structure.StructureMetaDataFactory;
import org.jboss.deployers.vfs.plugins.structure.AbstractVFSStructureDeployer;
import org.jboss.deployers.vfs.spi.structure.StructureContext;
import org.jboss.rails.core.metadata.RailsApplicationMetaData;
import org.jboss.virtual.VirtualFile;
import org.jboss.virtual.VirtualFileFilter;
import org.jboss.virtual.VisitorAttributes;
import org.jboss.virtual.plugins.context.jar.JarUtils;
import org.jboss.virtual.plugins.vfs.helpers.SuffixMatchFilter;

/**
 * StructureDeployer to identify Ruby-on-Rails applications.
 * 
 * @author Bob McWhirter
 */
public class RailsStructure extends AbstractVFSStructureDeployer implements JarExtensionProvider {

	/** Filter for finding *.jar files. */
	private static final VirtualFileFilter JAR_FILTER = new SuffixMatchFilter(".jar", VisitorAttributes.DEFAULT);
	

	/**
	 * Construct.
	 */
	public RailsStructure() {
		// We want this to fire before anything else, because we're selfish.
		setRelativeOrder(-10000);
	}

	public boolean determineStructure(StructureContext structureContext) throws DeploymentException {
		boolean recognized = false;
		VirtualFile file = structureContext.getFile();

		log.debug("attempt deploy against root: " + file);

		if (file.getName().endsWith(".rails")) {
			try {
				log.info("PROTOCOL " + file.toURL().getProtocol());
			} catch (Exception e) {
				throw new DeploymentException(e);
			}
		}

		ContextInfo context = null;

		try {
			if (JarUtils.isArchive(file.getName()) || !file.isLeaf()) {

				// Look for config/environment.rb to identify this tree
				// as a Ruby-on-Rails application.

				VirtualFile config = file.getChild("config");
				if (config != null) {
					VirtualFile environment = config.getChild("environment.rb");
					if (environment != null) {
						log.debug("Recognized: " + file);

						// Add the config/ directory to the metadata paths
						context = createContext(structureContext, new String[] { "config" } );

						// Flag this archive for unpacking
						//context.setModificationType( ModificationType.EXPLODE );
						context.setModificationType(ModificationType.UNPACK);
						
						/*
						for ( VirtualFile child : file.getChildren() ) {
							log.info( "CREATE CONTEXTINFO FOR CHILD: " + child.getName() );
							ContextInfo childContextInfo = StructureMetaDataFactory.createContextInfo( child.getName() );
							childContextInfo.setModificationType( ModificationType.TEMP );
							log.info( "contextinfo for child " + child.getName() + " == " + childContextInfo );
							structureContext.addChild( childContextInfo );
						}
						*/

						// Do the classpath loading for lib/java/
						addLibJavaClasspath(structureContext, context);
						//1addPluginJars(structureContext, context);

						MutableAttachments attachments = (MutableAttachments) context.getPredeterminedManagedObjects();
						RailsApplicationMetaData railsAppMetaData = new RailsApplicationMetaData(file);
						attachments.addAttachment(RailsApplicationMetaData.class, railsAppMetaData);
						recognized = true;
					}
				}
			}
		} catch (IOException e) {
			recognized = false;
			throw new DeploymentException(e);
		} catch (URISyntaxException e) {
			recognized = false;
			throw new DeploymentException(e);
		} finally {
			if (recognized == false && context != null) {
				structureContext.removeChild(context);
			}
		}

		return recognized;
	}

	protected void addPluginJars(StructureContext structureContext, ContextInfo context) throws IOException {
		VirtualFile root = structureContext.getRoot();
		VirtualFile vendorPlugins = root.getChild("vendor/plugins");
		if (vendorPlugins != null) {
			List<VirtualFile> plugins = vendorPlugins.getChildren();

			for (VirtualFile plugin : plugins) {
				VirtualFile pluginLib = plugin.getChild("lib");
				if (pluginLib != null) {
					List<VirtualFile> jars = vendorPlugins.getChildrenRecursively(JAR_FILTER);
					for (VirtualFile jar : jars) {
						addClassPath(structureContext, jar, true, true, context);
					}
				}
			}
		}
	}

	protected void addLibJavaClasspath(StructureContext structureContext, ContextInfo context) throws IOException {
		VirtualFile root = structureContext.getRoot();
		VirtualFile libJava = root.getChild("lib/java");

		if (libJava != null) {
			if (libJava.getChild("classes") != null) {
				log.debug("Adding lib/java/classes/ to classpath");
				ClassPathEntry classpath = StructureMetaDataFactory.createClassPathEntry("lib/java/classes");
				context.addClassPathEntry(classpath);
			}
			List<VirtualFile> jars = libJava.getChildrenRecursively(JAR_FILTER);

			for (VirtualFile jar : jars) {
				log.debug("Adding jar to classpath: " + jar);
				addClassPath(structureContext, jar, true, true, context);
			}
		}
	}

	public String getJarExtension() {
		log.info("getJarExtension()");
		return ".rails";
	}
}
