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
package org.jboss.rails.metadata;

import java.io.File;
import java.io.IOException;

public class RailsMetaData {

	private String applicationName;
	private String environment;
	private String railsRoot;

	public RailsMetaData() {
		this.environment = "development";
	}

	public String getApplicationName() {
		checkApplicationName();
		return this.applicationName;
	}

	private void checkApplicationName() {
		if (this.applicationName == null) {
			File railsRootFile = new File(railsRoot);
			String name = railsRootFile.getName();
			if ( name.endsWith( ".rails" ) ) {
				name = name.substring(0, name.length() - 6 );
			}
			this.applicationName = name;
		}
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getEnvironment() {
		return this.environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getRailsRoot() {
		return this.railsRoot;
	}

	public void setRailsRoot(String railsRoot) {
		this.railsRoot = railsRoot;
	}

}
