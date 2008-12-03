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
package org.jboss.rails.web.tomcat;

import org.apache.catalina.core.StandardContext;
import org.jboss.logging.Logger;
import org.jboss.rails.core.metadata.RailsApplicationMetaData;
import org.jboss.rails.web.rack.JBossRailsServletContextListener;
import org.jboss.ruby.enterprise.web.tomcat.RackContextConfig;

/**
 * Configures the Catalina context for a rails application.
 * 
 * @author Bob McWhirter
 */
public class RailsContextConfig extends RackContextConfig {

	/** Our log. */
	private static Logger log = Logger.getLogger(RailsContextConfig.class);

	/**
	 * Construct.
	 */
	public RailsContextConfig() {
	}

	@Override
	protected void applicationWebConfig() {
		log.info("RailsContextConfig:: applicationWebConfig()");
		setUpApplicationParamValues();
		setUpListeners();
		setUpWelcomeFiles();
	}
	
	/**
	 * Set the application-specific parameter-values from the meta-data.
	 */
	private void setUpApplicationParamValues() {
		
		setUpRailsRoot();
		setUpRailsEnv();
		setUpRack();
	}

	private void setUpRailsEnv() {
		RailsApplicationMetaData metaData = getRailsApplicationMetaData();
		String environment = metaData.getRailsEnv();
		context.addParameter("rails.env", environment);
	}

	private void setUpRailsRoot() {
		RailsApplicationMetaData metaData = getRailsApplicationMetaData();
		context.addParameter("root.path", metaData.getRailsRootPath() );
		context.addParameter("rails.root", metaData.getRailsRootPath() );
		context.addParameter("public.root", metaData.getRailsRootPath() + "/public" );
		log.info( "set rails.root to " + metaData.getRailsRootPath() );
	}
	
	private void setUpRack() {
		context.addParameter( "jruby.max.runtimes", "3" );
		context.addParameter( "jruby.min.runtimes", "3" );
		context.addParameter( "jruby.runtime.timeout.sec", "30" );
	}

	private void setUpListeners() {
		if (log.isTraceEnabled()) {
			log.trace("setUpListeners()");
		}
		context.addApplicationListener( JBossRailsServletContextListener.class.getName() );
	}

	private void setUpWelcomeFiles() {
		if (log.isTraceEnabled()) {
			log.trace("setUpWelcomeFiles()");
		}
		((StandardContext) context).setReplaceWelcomeFiles(true);
		context.addWelcomeFile("index.html");
	}

	
	protected RailsApplicationMetaData getRailsApplicationMetaData() {
		return (RailsApplicationMetaData) RackContextConfig.frameworkMetaData.get();
	}
}