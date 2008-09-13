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

package org.jboss.rails.vfs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.jboss.virtual.VirtualFile;
import org.jboss.virtual.plugins.context.AbstractVFSContext;
import org.jboss.virtual.plugins.context.DelegatingHandler;
import org.jboss.virtual.plugins.context.vfs.AssembledDirectoryHandler;
import org.jboss.virtual.plugins.context.vfs.ByteArrayHandler;
import org.jboss.virtual.spi.VFSContext;
import org.jboss.virtual.spi.VFSContextFactory;
import org.jboss.virtual.spi.VFSContextFactoryLocator;
import org.jboss.virtual.spi.VirtualFileHandler;

/**
 * The general VFSContext for stitching together a .war for a Ruby on Rails
 * application.
 * 
 * @author Bob McWhirter
 */
public class RailsAppContext extends AbstractVFSContext {

	private static final int MAX_WEB_XML_SIZE = 10240;

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
	private VirtualFileHandler railsRoot;

	/** Short-cut handler to the public/ RAILS_ROOT directory */
	private VirtualFileHandler railsPublic;

	private VirtualFileHandler metaInfHandler;

	private VirtualFileHandler jbossRailsYmlHandler;

	/**
	 * Construct with a name an RoR application directory context.
	 * 
	 * @param name
	 *            The name of the app, ultimately used for registering the
	 *            'host' of the rails:// URL
	 * @param railsAppDir
	 *            The RoR application directory
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	/*
	 * public RailsAppContext(String name, VirtualFileHandler railsAppDir)
	 * throws URISyntaxException, IOException { super(new URI( "rails://" + name
	 * + "/" ) ); this.name = name; setUpWarRoot(); setUpWebInf();
	 * setUpRailsApp( railsAppDir ); }
	 */

	public RailsAppContext(String name, VirtualFile railsRoot) throws URISyntaxException, IOException {
		super(new URI("rails://" + name + "/"));
		this.name = name;
		setUpWarRoot();
		setUpWebInf();
		setUpRailsApp(railsRoot);
		setUpMetaInf();
	}

	/**
	 * Set up the root handler.
	 */
	protected void setUpWarRoot() {
		this.warRootHandler = new WarRootHandler(this);
	}

	protected void setUpMetaInf() throws IOException, URISyntaxException {
		this.metaInfHandler = new MetaInfHandler( this );
		setUpJbossRailsYml();
	}
	
	protected void setUpJbossRailsYml() throws IOException, URISyntaxException {
		StringBuilder js = new StringBuilder();
		js.append( "rails: 2.0" );
		ByteArrayHandler jbossRailsYmlHandler = new ByteArrayHandler(this, metaInfHandler, "jboss-rails.yml", js.toString().getBytes() );
		this.jbossRailsYmlHandler = jbossRailsYmlHandler;
	}

	/**
	 * Set up the WEB-INF/ handler.
	 */
	protected void setUpWebInf() throws IOException {
		this.webInfHandler = new WebInfHandler(this);
		setUpWebXml();
		setUpWebInfLib();
	}

	/**
	 * Set up the WEB-INF/lib/ handler.
	 */
	protected void setUpWebInfLib() throws IOException {
		VirtualFileHandler rawWebInfLibHandler = new AssembledDirectoryHandler(this, null, "lib");
		this.webInfLibHandler = new DelegatingHandler(this, webInfHandler, "lib", rawWebInfLibHandler);
	}

	/**
	 * Set up the WEB-INF/web.xml handler.
	 */
	protected void setUpWebXml() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("org/jboss/rails/war/web.xml");

		int len = 0;
		int totalLen = 0;

		byte[] buf = new byte[MAX_WEB_XML_SIZE];

		while ((len = in.read(buf)) >= 0) {
			totalLen += len;
			if (totalLen > MAX_WEB_XML_SIZE) {
				throw new IOException("web.xml input too large: " + MAX_WEB_XML_SIZE);
			}
		}

		byte[] bytes = new byte[totalLen];

		System.arraycopy(buf, 0, bytes, 0, totalLen);

		this.webXmlHandler = new ByteArrayHandler(this, webInfHandler, "web.xml", bytes);
	}

	/**
	 * Set up the RoR application handler.
	 * 
	 * @param railsRoot
	 *            The handler to the RoR application directory.
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	protected void setUpRailsApp(VirtualFile railsRoot) throws IOException, URISyntaxException {

		URL railsRootUrl = railsRoot.toURL();

		VFSContextFactory factory = VFSContextFactoryLocator.getFactory(railsRootUrl);

		if (factory == null) {
			throw new IOException("unable to find context factory: " + railsRootUrl);
		}

		VFSContext railsRootContext = factory.getVFS(railsRootUrl);
		this.railsRoot = railsRootContext.getRoot();

		VFSContext railsPublicContext = factory.getVFS(new URL(railsRootUrl, "public"));
		DelegatingHandler railsPublicDelegate = new DelegatingHandler(this, warRootHandler, "", railsPublicContext.getRoot());
		this.railsPublic = railsPublicDelegate;
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
		return railsRoot;
	}

	public VirtualFileHandler getRailsPublic() {
		return railsPublic;
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

	public VirtualFileHandler getMetaInf() {
		return metaInfHandler;
	}

	public VirtualFileHandler getJBossRailsYml() {
		return jbossRailsYmlHandler;
	}

}