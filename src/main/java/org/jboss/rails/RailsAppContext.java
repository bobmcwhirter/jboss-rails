/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.rails;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.jboss.virtual.VirtualFile;
import org.jboss.virtual.plugins.context.AbstractVFSContext;
import org.jboss.virtual.plugins.context.DelegatingHandler;
import org.jboss.virtual.plugins.context.vfs.AssembledDirectoryHandler;
import org.jboss.virtual.plugins.context.vfs.ByteArrayHandler;
import org.jboss.virtual.spi.VirtualFileHandler;

/** The general VFSContext for stitching together a .war for a Ruby on Rails application.
 * 
 * @author Bob McWhirter
 */
public class RailsAppContext extends AbstractVFSContext {
	
	/** Name of this context, used for registering with the hostname of a URL */
	private String name;
	
	/** Handler the root of the .war file. */
	private WarRootHandler warRootHandler;
	
	/** Handler for the WEB-INF directory. */
	private WebInfHandler webInfHandler;
	
	/** Handler for synthesized web.xml */
	private ByteArrayHandler webXmlHandler;
	
	/** Handler for WEB-INF/lib/ */
	private DelegatingHandler webInfLibHandler;
	
	/** Handler pointing to on-disk RAILS_ROOT directory */
	private VirtualFileHandler railsAppDir;
	
	/** Short-cut handler to the public/ RAILS_ROOT directory */
	private VirtualFileHandler railsPublic;
	
	/** Construct with a name an RoR application directory context.
	 * 
	 * @param name The name of the app, ultimately used for registering the 'host' of the rails:// URL
	 * @param railsAppDir The RoR application directory
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public RailsAppContext(String name, VirtualFileHandler railsAppDir) throws URISyntaxException, IOException {
		super(new URI( "rails://" + name + "/" ) );
		this.name = name;
		setUpWarRoot();
		setUpWebInf();
		setUpRailsApp( railsAppDir );
	}
	
	public RailsAppContext(String name, VirtualFile railsAppDir) throws URISyntaxException, IOException {
		super(new URI( "rails://" + name + "/" ) );
		this.name = name;
		setUpWarRoot();
		setUpWebInf();
		setUpRailsApp( new VirtualFileDelegatingHandler( railsAppDir ) );
	}
	
	/** Set up the root handler. 
	 */
	protected void setUpWarRoot() {
		this.warRootHandler = new WarRootHandler( this );
	}
	
	/** Set up the WEB-INF/ handler. 
	 */
	protected void setUpWebInf() throws IOException {
		this.webInfHandler  = new WebInfHandler( this );
		setUpWebXml();
		setUpWebInfLib();
	}
	
	/** Set up the WEB-INF/lib/ handler. 
	 */
	protected void setUpWebInfLib() throws IOException {
		VirtualFileHandler rawWebInfLibHandler = new AssembledDirectoryHandler( this, null, "lib" );
		this.webInfLibHandler = new DelegatingHandler( this, webInfHandler, "lib", rawWebInfLibHandler );
	}
	
	/** Set up the WEB-INF/web.xml handler. 
	 */
	protected void setUpWebXml() throws IOException {
		this.webXmlHandler = new ByteArrayHandler( this, webInfHandler, "web.xml", "howdy".getBytes() );
	}
	
	/** Set up the RoR application handler.
	 * 
	 * @param railsAppDir The handler to the RoR application directory.
	 * @throws IOException
	 */
	protected void setUpRailsApp(VirtualFileHandler railsAppDir) throws IOException {
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
	
	public VirtualFileHandler getRailsRoot() throws IOException {
		return railsAppDir;
	}
	
	public VirtualFileHandler getRailsPublic() {
		return railsPublic;
	}
	public VirtualFileHandler getRawRailsPublic() throws IOException {
		return railsAppDir.getChild("public");
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