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
package org.jboss.ruby.enterprise.web.metadata;

public class RackWebMetaData {
	
	private String context = "/";
	private String host = "localhost";
	
	private String contextConfigClassName = "org.jboss.ruby.enterprise.web.tomcat.RackContextConfig";
	private String docBase;
	private Object frameworkMetaData;
	
	public RackWebMetaData() {
		
	}
	
	public RackWebMetaData(String context) {
		this.context = context;
	}
	
	public RackWebMetaData(String context, String host) {
		this.context = context;
		if ( host == null || host.equals( "*" ) ) {
			this.host = "localhost";
		} else {
			this.host = host;
		}
	}
	
	public Object getFrameworkMetaData() {
		return this.frameworkMetaData;
	}
	
	public void setFrameworkMetaData(Object frameworkMetaData) {
		this.frameworkMetaData = frameworkMetaData;
	}
	
	public String getContext() {
		return this.context;
	}
	
	public void setContext(String context) {
		this.context = context;
	}
	
	public String getHost() {
		return this.host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public String getContextConfigClassName() {
		return this.contextConfigClassName;
	}
	
	public void setContextConfigClassName(String contextConfigClassName) {
		this.contextConfigClassName = contextConfigClassName;
	}
	
	public String getDocBase() {
		return this.docBase;
	}
	
	public void setDocBase(String docBase) {
		this.docBase = docBase;
	}

	
	public String toString() {
		return "[WebMetaData: context=" + this.context + "; host=" + this.host + "]";
	}


}
