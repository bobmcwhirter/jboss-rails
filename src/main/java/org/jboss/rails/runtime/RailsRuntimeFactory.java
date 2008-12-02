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
package org.jboss.rails.runtime;

import org.jboss.logging.Logger;
import org.jboss.ruby.runtime.BasicRubyRuntimeFactory;
import org.jruby.Ruby;

public class RailsRuntimeFactory extends BasicRubyRuntimeFactory implements RailsRuntimeFactoryMBean {
	
	private static Logger log = Logger.getLogger( RailsRuntimeFactory.class );
	
	private String railsRoot;
	private String environment;

	public RailsRuntimeFactory(String railsRoot, String environment) {
		this.railsRoot = railsRoot;
		this.environment = environment;
	}
	
	public Ruby createRubyRuntime() throws Exception {
		Ruby ruby = super.createRubyRuntime();
		
		String initScript = "$LOAD_PATH << 'META-INF/jruby.home/lib/ruby/site_ruby/1.8'\n" +
        	"RAILS_ROOT='" + this.railsRoot + "'\n" + 
        	"RAILS_ENV='" + this.environment + "'\n" + 
        	"require \"#{RAILS_ROOT}/config/boot.rb\"\n";
		log.info( "initScript" + initScript );
		ruby.evalScriptlet( initScript );
		return ruby;
	}
}
