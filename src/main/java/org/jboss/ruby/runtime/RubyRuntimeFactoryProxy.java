package org.jboss.ruby.runtime;

import org.jruby.Ruby;

public class RubyRuntimeFactoryProxy implements RubyRuntimeFactory {
	
	private RubyRuntimeFactory delegate;

	public RubyRuntimeFactoryProxy() {
		
	}
	public RubyRuntimeFactoryProxy(RubyRuntimeFactory delegate) {
		this.delegate = delegate;
	}
	
	public void setDelegate(RubyRuntimeFactory delegate) {
		this.delegate = delegate;
	}
	
	public RubyRuntimeFactory getDelegate() {
		return this.delegate;
	}

	public Ruby createRubyRuntime() throws Exception {
		if ( this.delegate == null ) {
			throw new Exception( "No delegate" );
		}
		return this.delegate.createRubyRuntime();
	}

}
