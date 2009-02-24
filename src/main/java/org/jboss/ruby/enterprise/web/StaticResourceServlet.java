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
