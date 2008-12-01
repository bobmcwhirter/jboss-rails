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
package org.jboss.ruby.runtime;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;
import org.jruby.javasupport.JavaEmbedUtils;

public class BasicRubyRuntimeFactory implements RubyRuntimeFactory {
	
	public BasicRubyRuntimeFactory() {
		
	}

	public Ruby createRubyRuntime() throws Exception {
		RubyInstanceConfig config = new RubyInstanceConfig();

		List<String> loadPaths = new ArrayList<String>();

		try {
			String binjruby = RubyInstanceConfig.class.getResource("/META-INF/jruby.home/bin/jruby").toURI().getSchemeSpecificPart();
			config.setJRubyHome(binjruby.substring(0, binjruby.length() - 10));
		} catch (Exception e) {	
			// ignore
		}

		config.setEnvironment( getEnvironment() );
		config.setOutput( getOutput() );
		config.setError( getError() );

		Ruby runtime = JavaEmbedUtils.initialize(loadPaths, config);
		return runtime;
	}
	
	public Map<Object,Object> getEnvironment() {
		return Collections.emptyMap();
	}
	
	public PrintStream getOutput() {
		return System.out;
	}
	
	public PrintStream getError() {
		return System.err;
	}

}
