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
import org.jboss.ruby.core.runtime.RubyDynamicClassLoader;
import org.jboss.ruby.core.runtime.RuntimeInitializer;
import org.jboss.virtual.VirtualFile;
import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;

public class RailsRuntimeInitializer implements RuntimeInitializer {
	
	private VirtualFile railsRoot;
	private String railsEnv;

	public RailsRuntimeInitializer(VirtualFile railsRoot, String railsEnv) {
		this.railsRoot = railsRoot;
		this.railsEnv  = railsEnv;
	}
	
	public VirtualFile getRailsRoot() {
		return this.railsRoot;
	}
	
	public String getRailsEnv() {
		return this.railsEnv;
	}

	public void initialize(RubyDynamicClassLoader cl, Ruby ruby) throws Exception {
		String railsRootPath = railsRoot.toURL().getFile();
		if ( railsRootPath.endsWith( "/" ) ) {
			railsRootPath = railsRootPath.substring( 0, railsRootPath.length() - 1 );
		}
		
		Logger logger = Logger.getLogger( railsRootPath );
		IRubyObject rubyLogger = JavaEmbedUtils.javaToRuby( ruby,  logger );
		ruby.getGlobalVariables().set( "$JBOSS_RAILS_LOGGER", rubyLogger );
		ruby.evalScriptlet( createProlog( railsRootPath ) );
		
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
	
	protected String createProlog(String railsRootPath) {
		return
			"RAILS_ROOT='" + railsRootPath + "'\n" + 
			"RAILS_ENV='" + railsEnv + "'\n" + 
		    "require %q(org/jboss/rails/runtime/deployers/rails_init.rb)\n";
	}
	
	protected String createEpilog() {
		return  "load %q(config/environment.rb)\n";
	}

}
