package org.jboss.ruby.enterprise.webservices.databinding.simple;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class RubyDateTimeType extends RubySimpleType<Date> {
	
	public static final RubyDateTimeType INSTANCE = new RubyDateTimeType();
	
	private static DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	protected RubyDateTimeType() {
		super( null );
	}
	
	public Date read(String input) {
		try {
			return FORMAT.parse( input );
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String write(Object input) {
		if ( input instanceof Date ) {
			FORMAT.format( (Date) input );
		}
		return null;
	}


}
