package org.jboss.ruby.enterprise.webservices.databinding.simple;

public class RubyStringType extends RubySimpleType<String> {
	
	public static final RubyStringType INSTANCE = new RubyStringType();
	
	protected RubyStringType() {
		super( "String" );
	}
	
	public String read(String input) {
		return input;
	}
	
	public String write(Object input) {
		if ( input instanceof String ) {
			return (String) input;
		}
		return null;
	}

}
