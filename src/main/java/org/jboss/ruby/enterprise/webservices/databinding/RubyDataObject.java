package org.jboss.ruby.enterprise.webservices.databinding;

import org.jruby.runtime.builtin.IRubyObject;

public class RubyDataObject {

	private RubyType type;
	private IRubyObject object;

	public RubyDataObject(RubyType type, IRubyObject object) {
		this.type = type;
		this.object = object;
	}
	
	public String toString() {
		return "[RubyDataObject: type=" + type + "; object=" + object + "]";
	}

}
