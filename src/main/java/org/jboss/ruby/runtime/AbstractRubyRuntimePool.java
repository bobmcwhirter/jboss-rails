package org.jboss.ruby.runtime;

public abstract class AbstractRubyRuntimePool implements RubyRuntimePool {
	
	protected RubyRuntimeFactory factory;
	
	public AbstractRubyRuntimePool(RubyRuntimeFactory factory) {
		this.factory = factory;
	}
	
	public RubyRuntimeFactory getRubyRuntimeFactory() {
		return this.factory;
	}

}
