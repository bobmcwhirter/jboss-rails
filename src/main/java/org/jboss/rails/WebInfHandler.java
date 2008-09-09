package org.jboss.rails;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.jboss.virtual.plugins.context.AbstractVirtualFileHandler;
import org.jboss.virtual.plugins.context.StructuredVirtualFileHandler;
import org.jboss.virtual.plugins.context.vfs.ByteArrayHandler;
import org.jboss.virtual.spi.VirtualFileHandler;

public class WebInfHandler extends AbstractVirtualFileHandler implements StructuredVirtualFileHandler {
	
	public WebInfHandler(RailsAppContext vfsContext) {
		super( vfsContext, null, "WEB-INF" );
	}

	public VirtualFileHandler createChildHandler(String name) throws IOException {
		System.err.println( "WebInfHandler.createChildHandler(" + name + ")" );
		
		if ( "web.xml".equals( name ) ) {
			return new ByteArrayHandler( getVFSContext(), this, name, "foo".getBytes() );
		} else if ( "lib".equals( name ) ) {
			return getRailsAppContext().getWebInfLib();
		}
		
		return null;
	}

	public boolean exists() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	public VirtualFileHandler getChild(String path) throws IOException {
		return structuredFindChild( path );
	}

	public List<VirtualFileHandler> getChildren(boolean ignoreErrors) throws IOException {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}
	private RailsAppContext getRailsAppContext() {
		return (RailsAppContext) getVFSContext();
	}


}
