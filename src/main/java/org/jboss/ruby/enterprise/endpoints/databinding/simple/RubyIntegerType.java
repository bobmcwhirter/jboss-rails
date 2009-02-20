package org.jboss.ruby.enterprise.endpoints.databinding.simple;

public class RubyIntegerType extends RubySimpleType<Long> {
	
	public static final RubyIntegerType INSTANCE = new RubyIntegerType();
	
	protected RubyIntegerType() {
		super( "Integer" );
	}
	
	public Long read(String input) {
		return Long.parseLong( input );
	}
	
	public String write(Object input) {
		if ( input instanceof Long || input instanceof Integer || input instanceof Short ) {
			return input.toString();
		}
		return null;
	}

}
