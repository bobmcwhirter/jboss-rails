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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.virtual.plugins.context.AbstractVirtualFileHandler;
import org.jboss.virtual.plugins.context.DelegatingHandler;
import org.jboss.virtual.plugins.context.StructuredVirtualFileHandler;
import org.jboss.virtual.spi.VirtualFileHandler;

/** Handler for RAILS_ROOT/public serving from the root of the .war
 * 
 * <p>
 * This handler takes care of parentage of the public/ directory contents.
 * </p>
 * 
 * @author Bob McWhirter
 */
public class RailsPublicHandler extends AbstractVirtualFileHandler implements StructuredVirtualFileHandler {

	
	/** Construct.
	 * 
	 * @param vfsContext The root Rails context.
	 */
	public RailsPublicHandler(RailsAppContext vfsContext) {
		super( vfsContext, vfsContext.getWarRootHandler(), "/" );
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
		VirtualFileHandler child = getRailsAppContext().getRawRailsPublic().getChild(name);
		
		// Since this handler serves public/ only under the war-root,
		// we need to reparent the children handlers so that the WarRootHandler
		// is indeed their parent.  DelegatingHandler allows us to delegate 
		// everything except parentage, overriding it.
		if ( child != null ) {
			child = new DelegatingHandler( getRailsAppContext(), getRailsAppContext().getWarRootHandler(), name, child );
		}
		return child;
	}
	
	public List<VirtualFileHandler> getChildren(boolean ignoreErrors) throws IOException {
		// Since this handler serves public/ only under the war-root,
		// we need to reparent the children handlers so that the WarRootHandler
		// is indeed their parent.  DelegatingHandler allows us to delegate 
		// everything except parentage, overriding it.
		
		List<VirtualFileHandler> children = getRailsAppContext().getRawRailsPublic().getChildren( ignoreErrors );
		
		List<VirtualFileHandler> wrappedChildren = new ArrayList<VirtualFileHandler>();
		
		RailsAppContext railsAppContext = getRailsAppContext();
		WarRootHandler warRootHandler = railsAppContext.getWarRootHandler();
		
		for ( VirtualFileHandler child : children ) {
			wrappedChildren.add( new DelegatingHandler( railsAppContext, warRootHandler, child.getName(), child ) );
		}
		
		return wrappedChildren;
	}

	public long getLastModified() throws IOException {
		return getRailsAppContext().getRawRailsPublic().getLastModified();
	}

	public long getSize() throws IOException {
		return getRailsAppContext().getRawRailsPublic().getSize();
	}

	public boolean isHidden() throws IOException {
		return getRailsAppContext().getRawRailsPublic().isHidden();
	}

	public boolean isLeaf() throws IOException {
		return getRailsAppContext().getRawRailsPublic().isLeaf();
	}

	public boolean isNested() throws IOException {
		return true;
	}

	public InputStream openStream() throws IOException {
		return getRailsAppContext().getRawRailsPublic().openStream();
	}

	public boolean removeChild(String name) throws IOException {
		return getRailsAppContext().getRawRailsPublic().removeChild(name);
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