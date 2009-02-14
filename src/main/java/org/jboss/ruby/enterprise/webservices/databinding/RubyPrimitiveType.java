package org.jboss.ruby.enterprise.webservices.databinding;

public class RubyPrimitiveType extends RubyType {

	private String defaultValue;

	public RubyPrimitiveType(String name, String defaultValue) {
		super(name);
		this.defaultValue = defaultValue;
	}
	
	public String getNewInstanceFragment() {
		return this.defaultValue;
	}

	@Override
	protected void initialize(RubyDataBinding binding) {
		// nothing
	}

	@Override
	public boolean isSimple() {
		return true;
	}

}
