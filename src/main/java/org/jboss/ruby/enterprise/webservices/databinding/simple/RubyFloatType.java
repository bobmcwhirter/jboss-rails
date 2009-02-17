package org.jboss.ruby.enterprise.webservices.databinding.simple;

public class RubyFloatType extends RubySimpleType<Double> {
	
	public static final RubyFloatType INSTANCE = new RubyFloatType();
	
	protected RubyFloatType() {
		super( "Float" );
	}
	
	public Double read(String input) {
		return Double.parseDouble( input );
	}
	
	public String write(Double input) {
		return input.toString();
	}

}
