package org.jboss.ruby.runtime;

import org.jruby.Ruby;

public interface RuntimeInitializer {
	
	public void initialize(RubyDynamicClassLoader cl, Ruby ruby) throws Exception;

}
