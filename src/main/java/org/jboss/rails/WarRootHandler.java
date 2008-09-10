package org.jboss.rails;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.virtual.plugins.context.AbstractVirtualFileHandler;
import org.jboss.virtual.plugins.context.StructuredVirtualFileHandler;
import org.jboss.virtual.spi.VirtualFileHandler;

public class WarRootHandler extends AbstractVirtualFileHandler implements StructuredVirtualFileHandler {

	
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
	
	protected RailsAppContext getRailsAppContext() {
		return (RailsAppContext) getVFSContext();
	}

}