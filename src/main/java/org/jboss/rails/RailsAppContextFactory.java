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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.jboss.virtual.VFS;
import org.jboss.virtual.VirtualFile;
import org.jboss.virtual.plugins.context.file.FileSystemContextFactory;
import org.jboss.virtual.spi.VFSContext;
import org.jboss.virtual.spi.VirtualFileHandler;

/** Factory for creating RailsAppContexts and registering them for rails:// URLs.
 * 
 * <p>
 * This factory installs a handler for rails:// URLs, and registers new contexts
 * using the hostname portion of the URL for retrieval by the handler.
 * </p>
 * 
 * @author Bob McWhirter
 */
public class RailsAppContextFactory {

	static {
		String pkgs = System.getProperty("java.protocol.handler.pkgs");

		if (pkgs == null) {
			pkgs = "org.jboss.rails.protocol";
		} else if (!pkgs.contains("org.jboss.rails.protocol")) {
			pkgs += "|org.jboss.rails.protocol";
		}

		System.setProperty("java.protocol.handler.pkgs", pkgs);

		try {
			URL.setURLStreamHandlerFactory(new org.jboss.net.protocol.URLStreamHandlerFactory());
		} catch (Exception e) {
			// ignore 
		}

	}

	/** Singleton instance. */
	private static final RailsAppContextFactory INSTANCE = new RailsAppContextFactory();

	/** Retrieve the singleton instance. 
	 * 
	 * @return The singleton instance.
	 */
	public static RailsAppContextFactory getInstance() {
		return INSTANCE;
	}

	/** Registry of Rails contexts. */
	private Map<String, RailsAppContext> railsApps = new HashMap<String, RailsAppContext>();

	/** Construct.
	 */
	protected RailsAppContextFactory() {
	}

	/** Create a new RailsAppContext given a name an path to a Rails app directory.
	 * 
	 * @param name The name of the rails application, used to register the context.
	 * @param railsAppPath Path to the directory containing the Rails app
	 * @return
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public RailsAppContext createRoot(String name, String railsAppPath) throws URISyntaxException, MalformedURLException, IOException {
		File railsAppDir = new File(railsAppPath);
		VFSContext railsAppDirContext = new FileSystemContextFactory().getVFS(railsAppDir.toURL());
		VirtualFileHandler railsRoot = railsAppDirContext.getRoot();
		RailsAppContext context = new RailsAppContext(name, railsRoot);
		railsApps.put(name, context);
		return context;
	}
	
	public RailsAppContext createRoot(String name, VirtualFile railsAppPath) throws MalformedURLException, IOException, URISyntaxException {
		RailsAppContext context = new RailsAppContext(name, railsAppPath);
		railsApps.put(name, context);
		return context;
	}

	/** Find a Rails VFS by the name.
	 * 
	 * @param name The name of the context.
	 * @return The found context, or null if none is registered under the name.
	 */
	public VFS getVFS(String name) {
		RailsAppContext context = railsApps.get( name );
		if ( context != null ) {
			return context.getVFS();
		}
		
		return null;
	}

	/** Find a Rails VFS by the name.
	 * 
	 * @param name The name of the context.
	 * @return The found context, or null if none is registered under the name.
	 */
	public static VFS find(String name) {
		return getInstance().getVFS(name);
	}
}
