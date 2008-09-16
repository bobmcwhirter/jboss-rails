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
import java.util.Arrays;
import java.util.List;

import org.jboss.virtual.plugins.context.AbstractVirtualFileHandler;
import org.jboss.virtual.plugins.context.StructuredVirtualFileHandler;
import org.jboss.virtual.spi.VirtualFileHandler;

/** Handler for the WEB-INF/ directory of a synthetic Rails WAR VFS.
 * 
 * <p>
 * Primarily this handler proxies for the RAILS_ROOT handler filesystem
 * handler, it overlays {@code web.xml} handling and {@code lib/} handling
 * to other handlers.
 * </p>
 * 
 * @author Bob McWhirter
 */
public class WebInfHandler extends AbstractVirtualFileHandler implements StructuredVirtualFileHandler {
	
	/** Files to not serve directly form the RAILS_ROOT. */
	private static final String[] RESERVED_NAMES = {  "jboss-rails.yml",  "lib"  };
	
	
	/** Construct.
	 * 
	 * @param vfsContext The Rails application context.
	 */
	public WebInfHandler(RailsAppContext vfsContext) {
		super( vfsContext, vfsContext.getWarRootHandler(), "WEB-INF" );
	}

	public VirtualFileHandler createChildHandler(String name) throws IOException {
		
		VirtualFileHandler child = null;
		
		// First do any special-case dispatching, followed by
		// simple proxying to the RAILS_ROOT handler
		if ( "jboss-rails.yml".equals( name ) ) {
			child = getRailsAppContext().getJBossRailsYml();
		} else if ( "lib".equals( name ) ) {
			child = getRailsAppContext().getWebInfLib();
		} else {
			child = getRailsAppContext().getRailsRoot().getChild( name );
		}
		
		return child;
	}

	public boolean exists() throws IOException {
		return true;
	}

	public VirtualFileHandler getChild(String path) throws IOException {
		return structuredFindChild( path );
	}

	public List<VirtualFileHandler> getChildren(boolean ignoreErrors) throws IOException {
		RailsAppContext railsAppContext = getRailsAppContext();
		
		List<VirtualFileHandler> children = railsAppContext.getRailsRoot().getChildren( ignoreErrors );
		
		List<VirtualFileHandler> totalChildren = new ArrayList<VirtualFileHandler>();
		
		for ( VirtualFileHandler child : children ) {
			if ( Arrays.binarySearch( RESERVED_NAMES, child.getName() ) < 0 ) {
				RedelegatingHandler childDelegate = new RedelegatingHandler( railsAppContext, this, child.getName(), child );
				childDelegate.setPathName( "WEB-INF/" + child.getPathName() );
				totalChildren.add( childDelegate );
			}
		}
		
		// Add the overlaid synthetic children handlers.
		totalChildren.add( getRailsAppContext().getJBossRailsYml() );
		totalChildren.add( getRailsAppContext().getWebInfLib() );
		
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
		return getRailsAppContext().getWarRootHandler().toURI().resolve( "WEB-INF" );
	}
	
	/** Retrieve the VFSContext cast to a RailsAppContext.
	 * 
	 * @return The VFSContext recast to the actual RailsAppContext.
	 */
	private RailsAppContext getRailsAppContext() {
		return (RailsAppContext) getVFSContext();
	}


}
