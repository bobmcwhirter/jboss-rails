package org.jboss.ruby.enterprise.endpoints.databinding.simple;

import org.jboss.ruby.enterprise.endpoints.databinding.RubyType;
import org.jboss.ruby.enterprise.endpoints.databinding.RubyTypeSpace;

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
		
	protected void initialize(RubyTypeSpace typeSpace) {
		// nothing
	}
	
	public String getNewInstanceFragment() {
		return "nil";
	}
	

}
