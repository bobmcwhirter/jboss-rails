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

package org.jboss.rails.vfs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.virtual.plugins.context.AbstractVirtualFileHandler;
import org.jboss.virtual.plugins.context.StructuredVirtualFileHandler;
import org.jboss.virtual.spi.VirtualFileHandler;


/** Root-level handler for the synthetic Rails-app .war file.
 * 
 * <p>
 * It delegates to {@code RailsPublicHandler} and {@code WebInfHandler} in
 * an overlay fashion to handle each request.
 * </p>
 * 
 * @see WebInfHandler
 * @see RailsPublicHandler
 * 
 * @author Bob McWhirter
 */
public class WarRootHandler extends AbstractVirtualFileHandler implements StructuredVirtualFileHandler {
	
	/** Construct.
	 * 
	 * @param vfsContext The Rails VFS context.
	 */
	public WarRootHandler(RailsAppContext vfsContext) {
		super( vfsContext, null, "/" );
	}
	
	public boolean exists() throws IOException {
		return true;
	}

	public VirtualFileHandler getChild(String path) throws IOException {
		return structuredFindChild( path );
	}
	
	@Override
	public VirtualFileHandler structuredFindChild(String path) throws IOException {
		return super.structuredFindChild(path);
	}

	public VirtualFileHandler createChildHandler(String name) throws IOException {
		VirtualFileHandler child = null;
		
		if ( "WEB-INF".equals( name ) ) {
			child = getRailsAppContext().getWebInf();
		} else {
			child = getRailsAppContext().getRailsPublic().getChild(name);
		}
		return child;
	}
	
	public List<VirtualFileHandler> getChildren(boolean ignoreErrors) throws IOException {
		List<VirtualFileHandler> children = getRailsAppContext().getRailsPublic().getChildren(ignoreErrors);
		
		List<VirtualFileHandler> totalChildren = new ArrayList<VirtualFileHandler>();
		for ( VirtualFileHandler child : children )  {
			if ( ! "WEB-INF".equals( child.getName() ) ) {
				totalChildren.add( child );
			}
		}
		totalChildren.add( getRailsAppContext().getWebInf() );
		return totalChildren;	
	}

	public long getLastModified() throws IOException {
		return 0;
	}

	public long getSize() throws IOException {
		return 0;
	}

	public boolean isHidden() throws IOException {
		return false;
	}

	public boolean isLeaf() throws IOException {
		return false;
	}

	public boolean isNested() throws IOException {
		return false;
	}

	public InputStream openStream() throws IOException {
		throw new IOException( "Cannot open stream" );
	}

	public boolean removeChild(String name) throws IOException {
		return false;
	}

	public URI toURI() throws URISyntaxException {
		return getRailsAppContext().getRootURI();
	}
	
	/** Retrieve the VFSContext cast to a RailsAppContext.
	 * 
	 * @return The VFSContext recast to the actual RailsAppContext.
	 */
	protected RailsAppContext getRailsAppContext() {
		return (RailsAppContext) getVFSContext();
	}

}