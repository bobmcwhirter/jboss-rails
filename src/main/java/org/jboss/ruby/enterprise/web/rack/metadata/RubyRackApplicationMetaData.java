package org.jboss.ruby.enterprise.web.rack.metadata;

public class RubyRackApplicationMetaData {
	
	private String rackUpScript;

	public RubyRackApplicationMetaData() {
		
	}
	
	public void setRackUpScript(String rackUpScript) {
		this.rackUpScript = rackUpScript;
	}

	public String getRackUpScript() {
		return this.rackUpScript;
	}
}
