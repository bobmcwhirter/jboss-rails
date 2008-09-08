package org.jboss.rails;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.jboss.virtual.plugins.context.AbstractVFSContext;
import org.jboss.virtual.plugins.context.vfs.Assembled;
import org.jboss.virtual.spi.VirtualFileHandler;

public class RailsAppContext extends AbstractVFSContext {
	
	private RailsAppContextHandler rootHandler;
	
	public RailsAppContext(String simpleName, String railsAppDir) throws URISyntaxException {
		super(new URI( "rails://" + simpleName + "/" ) );
		this.rootHandler = new RailsAppContextHandler( this, simpleName );
	}

	public static final String NAME = "rails";

	public String getName() {
		return NAME;
	}

	public VirtualFileHandler getRoot() throws IOException {
		return rootHandler;
	}

	

}