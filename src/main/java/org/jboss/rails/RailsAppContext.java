package org.jboss.rails;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.jboss.virtual.VirtualFile;
import org.jboss.virtual.plugins.context.AbstractVFSContext;
import org.jboss.virtual.spi.VFSContext;
import org.jboss.virtual.spi.VirtualFileHandler;

public class RailsAppContext extends AbstractVFSContext {
	
	private RailsAppContextHandler rootHandler;
	private VFSContext railsAppDir;
	
	public RailsAppContext(String simpleName, VFSContext railsAppDir) throws URISyntaxException {
		super(new URI( "rails://" + simpleName + "/" ) );
		this.rootHandler = new RailsAppContextHandler( this, simpleName );
		this.railsAppDir = railsAppDir;
	}

	public static final String NAME = "rails";

	public String getName() {
		return NAME;
	}

	public VirtualFileHandler getRoot() throws IOException {
		return rootHandler;
	}
	
	public VFSContext getRailsAppDir() {
		return railsAppDir;
	}
	
	public VirtualFile getRailsRoot() throws IOException {
		//return railsAppDir.getRootPeer().getVirtualFile();
		return railsAppDir.getRoot().getVirtualFile();
	}
	
	public VirtualFile getRailsPublic() throws IOException {
		return getRailsRoot().getChild("public");
	}

}