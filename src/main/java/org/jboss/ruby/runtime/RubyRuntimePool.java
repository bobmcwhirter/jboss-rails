package org.jboss.ruby.runtime;

import org.jruby.Ruby;

public interface RubyRuntimePool {

	Ruby borrowRuntime() throws InterruptedException;
	void returnRuntime(Ruby runtime);
}
