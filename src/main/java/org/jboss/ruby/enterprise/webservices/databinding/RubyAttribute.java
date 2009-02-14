package org.jboss.ruby.enterprise.webservices.databinding;

import org.apache.ws.commons.schema.XmlSchemaElement;

public class RubyAttribute {
	
	private RubyType type;
	private XmlSchemaElement xsdElement;
	private String rubyName;

	public RubyAttribute(RubyType type, XmlSchemaElement xsdElement) {
		this.type = type;
		this.xsdElement = xsdElement;
		
		if ( this.type == null ) {
			throw new RuntimeException( xsdElement.toString() );
		}
	}
	
	public String getName() {
		return this.xsdElement.getName();
	}
	
	public String getRubyName() {
		if ( this.rubyName == null ) {
			if ( isPossiblyMultiple() ) {
				this.rubyName = pluralize( getName() );
			} else {
				this.rubyName = getName();
			}
		}
		
		return this.rubyName;
	}
	
	public String getInitializerFragment() {
		if ( isPossiblyMultiple() ) {
			return "@" + getRubyName() + " = [] # array of " + type.getName();
		} else if ( isPossiblyNil() ) {
			return "@" + getRubyName() + " = nil # optional " + type.getName();
		} else {
			return "@" + getRubyName() + " = " + type.getNewInstanceFragment();
		}
	}
	
	public boolean isPossiblyMultiple() {
		return this.xsdElement.getMaxOccurs() > 1;
	}
	
	public boolean isPossiblyNil() {
		if ( isPossiblyMultiple() ) {
			return false;
		}
		return this.xsdElement.getMinOccurs() == 0;
	}
	
	public String toString() {
		return "[RubyAttribute: name=" + getName() + "; type=" + this.type + "; xsdElement=" + xsdElement + "]\n" + getInitializerFragment() + "\n";
	}
	
	public static String pluralize(String in) {
		if ( in.endsWith( "s" ) ) {
			return in + "es";
		}
		
		return in + "s";
	}
	
	public static String capitalize(String in) {
		if ( in.length() > 1 ) { 
			return in.substring(0,1).toUpperCase() + in.substring(1);
		} else if ( in.length() == 1 ) {
			return in.toUpperCase();
		}
		return in;
	}

	public long getMinOccurs() {
		return xsdElement.getMinOccurs();
	}
	
	public long getMaxOccurs() {
		return xsdElement.getMaxOccurs();
	}

	public RubyType getType() {
		return this.type;
	}

}
