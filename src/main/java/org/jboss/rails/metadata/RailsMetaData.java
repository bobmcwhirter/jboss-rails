/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.rails.metadata;

import java.io.File;

/**
 * Meta-data describing a Ruby-on-Rails application.
 * 
 * @author Bob McWhirter
 */
public class RailsMetaData {

	/** Name of the application. Derived from the deployment name. */
	private String applicationName;

	/** RAILS_ENV environment. */
	private String environment;

	/** RAILS_ROOT filesystem path. */
	private String railsRoot;

	/** Web context to deploy under. */
	private String context;

	/**
	 * Construct.
	 * 
	 * <p>
	 * The default environment of 'development' is used until/unless otherwise
	 * set.
	 * </p>
	 */
	public RailsMetaData() {
		this.environment = "development";
	}

	/**
	 * Retrieve the application name.
	 * 
	 * <p>
	 * The application-name, if not set, is derived from the directory or
	 * symlink name from the deploy/ directory.
	 * </p>
	 * 
	 * @return The application name.
	 */
	public String getApplicationName() {
		checkApplicationName();
		return this.applicationName;
	}

	/**
	 * Check to see if we need to construct an application name.
	 */
	private void checkApplicationName() {
		if (this.applicationName == null) {
			File railsRootFile = new File(railsRoot);
			String name = railsRootFile.getName();
			if (name.endsWith(".rails")) {
				name = name.substring(0, name.length() - 6);
			}
			this.applicationName = name;
		}
	}

	/**
	 * Set the application name.
	 * 
	 * @param applicationName
	 *            Defines the name of the application, for the deployment
	 *            context.
	 */
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	/**
	 * Retrieve the RAILS_ENV environment name.
	 * 
	 * @return The RAILS_ENV environment.
	 */
	public String getEnvironment() {
		return this.environment;
	}

	/**
	 * Set the RAILS_ENV environment name.
	 * 
	 * @param environment
	 *            The RAILS_ENV environment.
	 */
	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	/**
	 * Retrieve the RAILS_ROOT filesystem path.
	 * 
	 * @return The RAILS_ROOT filesystem path.
	 */
	public String getRailsRoot() {
		return this.railsRoot;
	}

	/**
	 * Set the RAILS_ROOT filesystem path.
	 * 
	 * @param railsRoot
	 *            The RAILS_ROOT filesystem path.
	 */
	public void setRailsRoot(String railsRoot) {
		this.railsRoot = railsRoot;
	}

	public String getContext() {
		return this.context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String toString() {
		return "[RailsMetaData: railsRoot=" + this.railsRoot 
		+ "; context=" + this.context 
		+ "; environment=" + this.environment;
	}
}
