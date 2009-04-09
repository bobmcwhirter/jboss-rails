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
package org.jboss.ruby.enterprise.sip;

import org.jboss.kernel.Kernel;
import org.jboss.logging.Logger;
import org.jboss.ruby.enterprise.web.rack.spi.RackApplication;
import org.jboss.ruby.enterprise.web.rack.spi.RackApplicationPool;
import org.jruby.Ruby;
import org.mobicents.servlet.sip.message.SipServletRequestImpl;
import org.mobicents.servlet.sip.message.SipServletResponseImpl;
import org.mobicents.servlet.sip.ruby.SipRubyController;

/**
 * @author jean.deruelle@gmail.com
 *
 */
public class StandardSipRubyController implements SipRubyController {
	private static final Logger log = Logger.getLogger(StandardSipRubyController.class);
	private static final Object[] EMPTY_OBJECT_ARRAY = new Object[] {};
	private String name;
	private RackApplicationPool rackAppFactory;
	private String rackAppFactoryName;
	private Kernel kernel;
	
	public StandardSipRubyController(String name, String rackAppFactoryName, Kernel kernel) {
		this.name = name;
		this.kernel = kernel;
		this.rackAppFactoryName = rackAppFactoryName;
	}

	private void initEnv() {
		if(rackAppFactory == null) {
			org.jboss.kernel.spi.registry.KernelRegistryEntry entry = kernel.getRegistry().findEntry(rackAppFactoryName);
			if (entry != null) {
				rackAppFactory = (RackApplicationPool) entry.getTarget();
			}			
		}
	}
	
	public void routeSipRequestToRubyApp(SipServletRequestImpl request) {
		RackApplication rackApp = null;
		initEnv();		
		try {			
			rackApp = rackAppFactory.borrowApplication();
//			Object rackEnv = rackApp.createEnvironment(getServletContext(), request);
			rackApp.dispatchSipRequest(request, name);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rackApp != null) {
				rackAppFactory.releaseApplication(rackApp);
				rackApp = null;
			}
		}
	}	
	
	public void routeSipResponseToRubyApp(SipServletResponseImpl response) {
		
		RackApplication rackApp = null;
		initEnv();
		try {
			rackApp = rackAppFactory.borrowApplication();
//			Object rackEnv = rackApp.createEnvironment(getServletContext(), response);
			
			rackApp.dispatchSipResponse(response, name);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rackApp != null) {
				rackAppFactory.releaseApplication(rackApp);
				rackApp = null;
			}
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}	
	
	protected void loadSupport(Ruby runtime) {
		String supportScript = "require %q(org/jboss/ruby/enterprise/sip/sip_base_handler)\n";
		runtime.evalScriptlet(supportScript);
	}
}
