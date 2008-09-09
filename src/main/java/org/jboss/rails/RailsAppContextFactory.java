package org.jboss.rails;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.jboss.virtual.plugins.context.file.FileSystemContext;
import org.jboss.virtual.plugins.context.file.FileSystemContextFactory;
import org.jboss.virtual.spi.VFSContext;

public class RailsAppContextFactory {

	private static final RailsAppContextFactory INSTANCE = new RailsAppContextFactory();

	public static RailsAppContextFactory getInstance() {
		return INSTANCE;
	}
	
	public RailsAppContextFactory() {
		
	}
	
	public RailsAppContext createRoot(String simpleName, String railsAppPath) throws URISyntaxException, MalformedURLException, IOException {
		File railsAppDir = new File( railsAppPath );
		VFSContext railsAppDirContext = new FileSystemContextFactory().getVFS( railsAppDir.toURL() );
		return new RailsAppContext( simpleName, railsAppDirContext );
	}
}
