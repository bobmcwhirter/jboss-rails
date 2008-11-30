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

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.matchers.JarExtensionProvider;
import org.jboss.deployers.spi.structure.ContextInfo;
import org.jboss.deployers.vfs.plugins.structure.AbstractVFSStructureDeployer;
import org.jboss.deployers.vfs.spi.structure.StructureContext;
import org.jboss.virtual.VirtualFile;

public class RailsStructure extends AbstractVFSStructureDeployer implements JarExtensionProvider {

	public RailsStructure() {
		setRelativeOrder(-10000);
	}

	public boolean determineStructure(StructureContext deploymentContext) throws DeploymentException {
		boolean recognized = false;
		VirtualFile root = deploymentContext.getRoot();

		try {
			if ( ! root.isLeaf() ) {
				VirtualFile config = root.getChild( "config" );
				if ( config != null ) {
					VirtualFile environment = config.getChild( "environment.rb" );
					if ( environment != null ) {
						ContextInfo context = createContext(deploymentContext, "config" );
						recognized = true;
					}
				}
			}
		} catch (IOException e) {
			throw new DeploymentException( e );
		} 
		
		return recognized;
	}

	public String getJarExtension() {
		log.info( "getJarExtension()..." );
		return ".rails";
	}
}
