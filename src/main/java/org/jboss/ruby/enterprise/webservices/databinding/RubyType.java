package org.jboss.ruby.enterprise.webservices.databinding;

public class RubyType {
	
	private String name;

	public RubyType(String name) {
		this.name = name;
		
	}
	
	public String toString() {
		return "[RubyType: name=" + name + "]";
	}

}
