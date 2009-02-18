package org.jboss.ruby.enterprise.webservices.databinding.simple;

import org.jboss.ruby.enterprise.webservices.databinding.RubyDataBinding;
import org.jboss.ruby.enterprise.webservices.databinding.RubyType;

public abstract class RubySimpleType<T> extends RubyType {
	
	public RubySimpleType(String name) {
		super(name);
	}
	
	public abstract T read(String input);
	public abstract String write(Object input);
	
	@Override
	public boolean isArraySubclass() {
		return false;
	}

	@Override
	public boolean isSimple() {
		return true;
	}
		
	protected void initialize(RubyDataBinding binding) {
		// nothing
	}
	
	public String getNewInstanceFragment() {
		return "nil";
	}
	

}
