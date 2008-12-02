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
import java.util.List;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.attachments.MutableAttachments;
import org.jboss.deployers.spi.deployer.matchers.JarExtensionProvider;
import org.jboss.deployers.spi.structure.ClassPathEntry;
import org.jboss.deployers.spi.structure.ContextInfo;
import org.jboss.deployers.spi.structure.StructureMetaDataFactory;
import org.jboss.deployers.vfs.plugins.structure.AbstractVFSStructureDeployer;
import org.jboss.deployers.vfs.spi.structure.StructureContext;
import org.jboss.rails.core.metadata.RailsApplicationMetaData;
import org.jboss.virtual.VirtualFile;
import org.jboss.virtual.VirtualFileFilter;
import org.jboss.virtual.VisitorAttributes;
import org.jboss.virtual.plugins.context.jar.JarUtils;
import org.jboss.virtual.plugins.vfs.helpers.SuffixMatchFilter;

public class RailsStructure extends AbstractVFSStructureDeployer implements JarExtensionProvider {

	private static final VirtualFileFilter JAR_FILTER = new SuffixMatchFilter(".jar", VisitorAttributes.DEFAULT);

	public RailsStructure() {
		setRelativeOrder(-10000);
	}

	public boolean determineStructure(StructureContext structureContext) throws DeploymentException {
		boolean recognized = false;
		VirtualFile root = structureContext.getRoot();

		log.info("attempt deploy against root: " + root);

		ContextInfo context = null;
		try {
			if (JarUtils.isArchive(root.getName())) {
				if (!root.isLeaf()) {
					VirtualFile config = root.getChild("config");
					if (config != null) {
						VirtualFile environment = config.getChild("environment.rb");
						if (environment != null) {
							context = createContext(structureContext, "config");
							addLibJava(structureContext, context);
							MutableAttachments attachments = (MutableAttachments) context.getPredeterminedManagedObjects();
							RailsApplicationMetaData railsAppMetaData = new RailsApplicationMetaData(root);
							attachments.addAttachment(RailsApplicationMetaData.class, railsAppMetaData);
							recognized = true;
						}
					}
				}
			}
		} catch (IOException e) {
			recognized = false;
		}

		return recognized;
	}

	protected void addLibJava(StructureContext structureContext, ContextInfo context) throws IOException {
		VirtualFile root = structureContext.getRoot();
		VirtualFile libJava = root.getChild( "lib/java" );
		
		if ( libJava != null ) {
			if ( libJava.getChild( "classes" ) != null ) { 
				ClassPathEntry classpath = StructureMetaDataFactory.createClassPathEntry("lib/java/classes");
				context.addClassPathEntry(classpath);
			}
			List<VirtualFile> jars = libJava.getChildrenRecursively( JAR_FILTER );
			
			for ( VirtualFile jar : jars ) {
				log.info( "Adding to classpath: " + jar );
		        addClassPath(structureContext, jar, true, true, context);
			}
		}
	}

	public String getJarExtension() {
		log.info("getJarExtension()");
		return ".rails";
	}
}
