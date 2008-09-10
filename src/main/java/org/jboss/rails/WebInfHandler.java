package org.jboss.rails;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.virtual.plugins.context.AbstractVirtualFileHandler;
import org.jboss.virtual.plugins.context.DelegatingHandler;
import org.jboss.virtual.plugins.context.StructuredVirtualFileHandler;
import org.jboss.virtual.spi.VirtualFileHandler;

public class WebInfHandler extends AbstractVirtualFileHandler implements StructuredVirtualFileHandler {
	
	private static final String[] RESERVED_NAMES = {  "web.xml",  "lib"  };
	
	
	public WebInfHandler(RailsAppContext vfsContext) {
		super( vfsContext, vfsContext.getWarRootHandler(), "WEB-INF" );
	}

	public VirtualFileHandler createChildHandler(String name) throws IOException {
		
		VirtualFileHandler child = null;
		
		if ( "web.xml".equals( name ) ) {
			child = getRailsAppContext().getWebXml();
		} else if ( "lib".equals( name ) ) {
			child = getRailsAppContext().getWebInfLib();
		} else {
			child = getRailsAppContext().getRailsRoot().getChild( name );
		}
		
		return child;
	}

	public boolean exists() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	public VirtualFileHandler getChild(String path) throws IOException {
		return structuredFindChild( path );
	}

	public List<VirtualFileHandler> getChildren(boolean ignoreErrors) throws IOException {
		RailsAppContext railsAppContext = getRailsAppContext();
		VirtualFileHandler webInf = railsAppContext.getWebInf();
		
		List<VirtualFileHandler> children = railsAppContext.getRailsRoot().getChildren( ignoreErrors );
		
		List<VirtualFileHandler> totalChildren = new ArrayList<VirtualFileHandler>();
		
		for ( VirtualFileHandler child : children ) {
			if ( Arrays.binarySearch( RESERVED_NAMES, child.getName() ) < 0 ) {
				child = new DelegatingHandler( railsAppContext, webInf, child.getName(), child );
				totalChildren.add( child );
			}
		}
		
		totalChildren.add( getRailsAppContext().getWebXml() );
		totalChildren.add( getRailsAppContext().getWebInfLib() );
		
		return totalChildren;
	}

	public long getLastModified() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getSize() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isHidden() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isLeaf() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isNested() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	public InputStream openStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean removeChild(String name) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	public URI toURI() throws URISyntaxException {
		return getRailsAppContext().getWarRootHandler().toURI().resolve( "WEB-INF" );
	}
	
	private RailsAppContext getRailsAppContext() {
		return (RailsAppContext) getVFSContext();
	}


}
