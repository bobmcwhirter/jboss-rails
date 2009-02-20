package org.jboss.ruby.enterprise.endpoints.databinding;

public abstract class RubyType {
	
	private String name;

	public RubyType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	protected abstract void initialize(RubyDataBinding binding); 
	
	public abstract String getNewInstanceFragment();
	
	public abstract boolean isSimple();
	public abstract boolean isArraySubclass();

}
