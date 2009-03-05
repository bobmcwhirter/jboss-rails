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
package org.jboss.ruby.enterprise.web.rack;

import org.jboss.logging.Logger;
import org.jboss.ruby.runtime.RubyRuntimeFactory;
import org.jruby.Ruby;

public class RubyRackApplicationFactory implements RackApplicationFactory {
	
	private static final Logger log = Logger.getLogger( RubyRackApplicationFactory.class );

	private RubyRuntimeFactory runtimeFactory;
	private String rackUpScript;

	public RubyRackApplicationFactory() {
		
	}
	
	public void setRubyRuntimeFactory(RubyRuntimeFactory runtimeFactory) {
		this.runtimeFactory = runtimeFactory;
	}
	
	public RubyRuntimeFactory getRubyRuntimeFactory() {
		return this.runtimeFactory;
	}
	
	public void setRackUpScript(String rackUpScript) {
		this.rackUpScript = rackUpScript;
	}
	
	public String getRackUpScript() {
		return this.rackUpScript;
	}
	
	public RackApplication createRackApplication() throws Exception {
		Ruby ruby = getRubyRuntimeFactory().createRubyRuntime();
		
		RubyRackApplication rackApp = new RubyRackApplication( ruby, rackUpScript );
		
		return rackApp;
	}

}
