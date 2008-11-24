package org.jboss.ruby.runtime.metadata;

public class AbstractRubyRuntimePoolMetaData {
	
	private String name;
	
	public AbstractRubyRuntimePoolMetaData() {
		
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}

}
