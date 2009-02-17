package org.jboss.ruby.enterprise.webservices.databinding.simple;


public class RubyBooleanType extends RubySimpleType<Boolean> {
	
	public static final RubyBooleanType INSTANCE = new RubyBooleanType();
	
	protected RubyBooleanType() {
		super( null );
	}
	
	public Boolean read(String input) {
		if ( input.equalsIgnoreCase( "true" ) ) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	public String write(Boolean input) {
		return input.toString();
	}


}
