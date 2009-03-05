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
package org.jboss.rails.runtime.deployers;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;
import org.jboss.ruby.runtime.RubyDynamicClassLoader;
import org.jboss.ruby.runtime.RuntimeInitializer;
import org.jruby.Ruby;
import org.jruby.RubyArray;

public class RailsRuntimeInitializer implements RuntimeInitializer {
	
	private static final Logger log = Logger.getLogger(RailsRuntimeInitializer.class );
	
	private String railsRoot;
	private String railsEnv;

	public RailsRuntimeInitializer(String railsRoot, String railsEnv) {
		this.railsRoot = railsRoot;
		this.railsEnv  = railsEnv;
	}

	public void initialize(RubyDynamicClassLoader cl, Ruby ruby) throws Exception {
		ruby.evalScriptlet( createProlog() );
		
		RubyArray rubyLoadPath = (RubyArray) ruby.getGlobalVariables().get( "$LOAD_PATH" );
		
		List<String> loadPaths = new ArrayList<String>();
		int len = rubyLoadPath.size();
		for ( int i = 0 ; i < len ; ++i ) {
			String path = (String) rubyLoadPath.get( i );
			loadPaths.add( path );
		}
		
		cl.addLoadPaths( loadPaths );
		
		ruby.evalScriptlet( createEpilog() );
	}
	
	public String createProlog() {
		return
			"RAILS_ROOT='" + railsRoot + "'\n" + 
			"RAILS_ENV='" + railsEnv + "'\n" + 
		    "require %q(org/jboss/rails/runtime/deployers/rails_init.rb)\n";
	}
	
	public String createEpilog() {
		return  "load %q(config/environment.rb)\n";
	}

}
