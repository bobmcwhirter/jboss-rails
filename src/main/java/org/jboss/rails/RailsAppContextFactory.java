package org.jboss.rails;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.jboss.virtual.VFS;
import org.jboss.virtual.plugins.context.file.FileSystemContextFactory;
import org.jboss.virtual.spi.VFSContext;

public class RailsAppContextFactory {

	private static final RailsAppContextFactory INSTANCE = new RailsAppContextFactory();
	
	private Map<String,RailsAppContext> railsApps = new HashMap<String,RailsAppContext>();

	public static RailsAppContextFactory getInstance() {
		return INSTANCE;
	}
	
	public RailsAppContextFactory() {
		
	}
	
	public RailsAppContext createRoot(String name, String railsAppPath) throws URISyntaxException, MalformedURLException, IOException {
		File railsAppDir = new File( railsAppPath );
		VFSContext railsAppDirContext = new FileSystemContextFactory().getVFS( railsAppDir.toURL() );
		RailsAppContext context = new RailsAppContext( name, railsAppDirContext );
		railsApps.put( name, context );
		return context;
	}
	
	public VFS getVFS(String name) {
		return railsApps.get( name ).getVFS();
	}

	public static VFS find(String name) {
		return getInstance().getVFS( name );
	}
}
