package org.jboss.ruby.enterprise.webservices.databinding.simple;

public class RubyIntegerType extends RubySimpleType<Long> {
	
	public static final RubyIntegerType INSTANCE = new RubyIntegerType();
	
	protected RubyIntegerType() {
		super( "Integer" );
	}
	
	public Long read(String input) {
		return Long.parseLong( input );
	}
	
	public String write(Long input) {
		return input.toString();
	}

}
