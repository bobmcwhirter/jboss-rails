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
package org.jboss.ruby.enterprise.web.tomcat;

import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.deploy.FilterDef;
import org.apache.catalina.deploy.FilterMap;
import org.apache.catalina.startup.ContextConfig;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.logging.Logger;

/**
 * Configures the Catalina context for a ruby application.
 * 
 * @author Bob McWhirter
 */
public class RackContextConfig extends ContextConfig {

	/**
	 * Thread-local to pass meta-data between MC deployer and Catalina's
	 * deployment "stuff".
	 */
	public static ThreadLocal<Object> frameworkMetaData = new ThreadLocal<Object>();

	/** Our log. */
	private static Logger log = Logger.getLogger(RackContextConfig.class);

	/**
	 * Construct.
	 */
	public RackContextConfig() {
	}

	@Override
	protected void applicationWebConfig() {
		log.info("RackContextConfig:: applicationWebConfig()");
	}

	@Override
	protected void defaultWebConfig() {
		log.info("RackContextConfig:: defaultWebConfig()");

		setUpServletVersion();
		setUpParamValues();
		setUpDisplayName();
		setUpDistributable();
		setUpFilters();
		setUpLogin();
		setUpMimeMappings();
		setUpSecurity();
		setUpServlets();
		setUpJspMappings();
		setUpLocaleEncodings();
		setUpWelcomeFiles();
		setUpSessions();
	}

	private void setUpServletVersion() {
		if (log.isTraceEnabled()) {
			log.trace("setUpServletVersion()");
		}
		context.setPublicId("/javax/servlet/resources/web-app_2_4.dtd");
	}

	private void setUpParamValues() {
		if (log.isTraceEnabled()) {
			log.trace("setUpParamValues()");
		}
		// nothing
	}

	private void setUpDisplayName() {
		if (log.isTraceEnabled()) {
			log.trace("setUpDisplayName()");
		}
		// nothing
	}

	private void setUpDistributable() {
		if (log.isTraceEnabled()) {
			log.trace("setUpDistributable()");
		}
		context.setDistributable(true);
	}

	private void setUpFilters() {
		if (log.isTraceEnabled()) {
			log.trace("setUpFilters()");
		}
		setUpRackFilter();
	}

	private void setUpRackFilter() {
		if (log.isTraceEnabled()) {
			log.trace("setUpRackFilter()");
		}
		FilterDef filter = new FilterDef();
		filter.setFilterName("jruby-rack");
		filter.setFilterClass("org.jruby.rack.RackFilter");
		context.addFilterDef(filter);

		FilterMap filterMap = new FilterMap();
		filterMap.setFilterName("jruby-rack");
		filterMap.addURLPattern("/*");

		context.addFilterMap(filterMap);
	}

	private void setUpLogin() {
		if (log.isTraceEnabled()) {
			log.trace("setUpLogin()");
		}
		// TODO Auto-generated method stub
	}

	private void setUpMimeMappings() {
		if (log.isTraceEnabled()) {
			log.trace("setUpMimeMappings()");
		}
		// TODO Auto-generated method stub
	}

	private void setUpSecurity() {
		if (log.isTraceEnabled()) {
			log.trace("setUpSecurity()");
		}
		// TODO Auto-generated method stub
	}

	private void setUpServlets() {
		log.info("setUpServlets()");
		// TODO Auto-generated method stub
		setUpDefaultServlet();
	}

	private void setUpDefaultServlet() {
		log.info("setUpDefaultServlet()");
		Wrapper wrapper = context.createWrapper();
		wrapper.setName("jboss-rack-default");
		wrapper.addInitParameter("debug", "1");
		wrapper.setServletClass("org.apache.catalina.servlets.DefaultServlet");
		context.addChild(wrapper);

		context.addServletMapping("/*", "jboss-rack-default");
	}

	private void setUpJspMappings() {
		if (log.isTraceEnabled()) {
			log.trace("setUpJspMappings()");
		}
		// TODO Auto-generated method stub
	}

	private void setUpLocaleEncodings() {
		if (log.isTraceEnabled()) {
			log.trace("setUpLocaleEncodings()");
		}
		// TODO Auto-generated method stub
	}

	private void setUpWelcomeFiles() {
		if (log.isTraceEnabled()) {
			log.trace("setUpWelcomeFiles()");
		}
		((StandardContext) context).setReplaceWelcomeFiles(true);
		context.addWelcomeFile("index.html");
	}

	private void setUpSessions() {
		if (log.isTraceEnabled()) {
			log.trace("setUpSessions()");
		}
		// TODO Auto-generated method stub
	}

}
