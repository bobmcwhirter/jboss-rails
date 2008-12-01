package org.jboss.ruby.runtime;

import org.jruby.Ruby;

public class SharedRubyRuntimePool extends AbstractRubyRuntimePool {
	
	protected Ruby instance;
	
	public SharedRubyRuntimePool(RubyRuntimeFactory factory) {
		super( factory );
	}

	public void start() throws Exception {
		this.instance = factory.createRubyRuntime();
	}
	
	public void stop() {
		this.instance = null;
	}
	
	public Ruby borrowRuntime() {
		return this.instance;
	}

	public void returnRuntime(Ruby runtime) {
		// nothing
	}

}
