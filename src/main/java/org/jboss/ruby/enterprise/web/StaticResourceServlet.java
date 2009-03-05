/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.ruby.enterprise.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.servlets.DefaultServlet;
import org.jboss.logging.Logger;

public class StaticResourceServlet extends DefaultServlet {
	
	private static final long serialVersionUID = 7173759925797350928L;
	
	private static final Logger log = Logger.getLogger( StaticResourceServlet.class );
	
	private String resourceRoot;

	@Override
	public void init() throws ServletException {
		super.init();
		String resourceRoot = getServletConfig().getInitParameter( "resource.root" );
		this.resourceRoot = resourceRoot;
	}

	@Override
	protected String getRelativePath(HttpServletRequest arg0) {
		String path = super.getRelativePath(arg0);
		return resourceRoot + path;
	}

}
