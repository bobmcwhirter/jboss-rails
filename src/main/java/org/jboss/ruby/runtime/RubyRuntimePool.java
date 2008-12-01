package org.jboss.ruby.runtime;

import org.jruby.Ruby;

public interface RubyRuntimePool {

	Ruby borrowRuntime() throws Exception;
	void returnRuntime(Ruby runtime);
}
