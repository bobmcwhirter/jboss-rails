package org.jboss.ruby.enterprise.webservices.databinding;

public class RubyDataObject {

	private RubyType type;

	public RubyDataObject(RubyType type) {
		this.type = type;
	}
	
	public String toString() {
		return "[RubyDataObject: type=" + type + "]";
	}

}
