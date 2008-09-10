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

public class RailsPublicHandler extends AbstractVirtualFileHandler implements StructuredVirtualFileHandler {

	
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
		if ( child != null ) {
			child = new DelegatingHandler( getRailsAppContext(), getRailsAppContext().getWarRootHandler(), name, child );
		}
		return child;
	}
	
	public List<VirtualFileHandler> getChildren(boolean ignoreErrors) throws IOException {
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
	
	protected RailsAppContext getRailsAppContext() {
		return (RailsAppContext) getVFSContext();
	}

}