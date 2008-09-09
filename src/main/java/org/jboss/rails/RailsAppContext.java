package org.jboss.rails;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.jboss.virtual.VirtualFile;
import org.jboss.virtual.plugins.context.AbstractVFSContext;
import org.jboss.virtual.spi.VFSContext;
import org.jboss.virtual.spi.VirtualFileHandler;

public class RailsAppContext extends AbstractVFSContext {
	
	private WarRootHandler warRootHandler;
	private WebInfHandler webInfHandler;
	private VFSContext railsAppDir;
	
	public RailsAppContext(String simpleName, VFSContext railsAppDir) throws URISyntaxException {
		super(new URI( "rails://" + simpleName + "/" ) );
		this.warRootHandler = new WarRootHandler( this );
		this.webInfHandler  = new WebInfHandler( this );
		this.railsAppDir    = railsAppDir;
	}

	public static final String NAME = "rails";

	public String getName() {
		return NAME;
	}

	public VirtualFileHandler getRoot() throws IOException {
		return getWarRootHandler();
	}
	
	public WarRootHandler getWarRootHandler() {
		return warRootHandler;
	}
	
	public VFSContext getRailsAppDir() {
		return railsAppDir;
	}
	
	public VirtualFileHandler getRailsRoot() throws IOException {
		return railsAppDir.getRoot();
	}
	
	public VirtualFileHandler getRailsPublic() throws IOException {
		return getRailsRoot().getChild("public");
	}

	public VirtualFileHandler getWebInf() {
		return webInfHandler;
	}

}