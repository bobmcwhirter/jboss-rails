package org.jboss.rails;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.jboss.virtual.VirtualFile;
import org.jboss.virtual.plugins.context.AbstractVFSContext;
import org.jboss.virtual.plugins.context.DelegatingHandler;
import org.jboss.virtual.plugins.context.vfs.AssembledDirectoryHandler;
import org.jboss.virtual.plugins.context.vfs.ByteArrayHandler;
import org.jboss.virtual.spi.VFSContext;
import org.jboss.virtual.spi.VirtualFileHandler;

public class RailsAppContext extends AbstractVFSContext {
	
	private String name;
	private WarRootHandler warRootHandler;
	private WebInfHandler webInfHandler;
	private ByteArrayHandler webXmlHandler;
	private DelegatingHandler webInfLibHandler;
	
	private VFSContext railsAppDir;
	
	private VirtualFileHandler railsPublic;
	
	public RailsAppContext(String name, VFSContext railsAppDir) throws URISyntaxException, IOException {
		super(new URI( "rails://" + name + "/" ) );
		this.name = name;
		setUpWarRoot();
		setUpWebInf();
		setUpRailsApp( railsAppDir );
	}
	
	protected void setUpWarRoot() {
		this.warRootHandler = new WarRootHandler( this );
	}
	
	protected void setUpWebInf() throws IOException {
		this.webInfHandler  = new WebInfHandler( this );
		setUpWebXml();
		setUpWebInfLib();
	}
	
	protected void setUpWebInfLib() throws IOException {
		VirtualFileHandler rawWebInfLibHandler = new AssembledDirectoryHandler( this, null, "lib" );
		this.webInfLibHandler = new DelegatingHandler( this, webInfHandler, "lib", rawWebInfLibHandler );
	}
	
	protected void setUpWebXml() throws IOException {
		this.webXmlHandler = new ByteArrayHandler( this, webInfHandler, "web.xml", "howdy".getBytes() );
	}
	
	protected void setUpRailsApp(VFSContext railsAppDir) throws IOException {
		this.railsAppDir    = railsAppDir;
		this.railsPublic = new RailsPublicHandler( this );
	}

	public String getName() {
		return name;
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
	
	public VirtualFileHandler getRailsPublic() {
		return railsPublic;
	}
	public VirtualFileHandler getRawRailsPublic() throws IOException {
		return railsAppDir.getRoot().getChild("public");
	}

	public VirtualFileHandler getWebInf() {
		return webInfHandler;
	}
	
	public VirtualFileHandler getWebInfLib() {
		return webInfLibHandler;
	}

	public VirtualFileHandler getWebXml() {
		return webXmlHandler;
	}

}