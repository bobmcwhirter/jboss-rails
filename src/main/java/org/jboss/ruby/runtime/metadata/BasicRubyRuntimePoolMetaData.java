package org.jboss.ruby.runtime.metadata;

public class BasicRubyRuntimePoolMetaData extends AbstractRubyRuntimePoolMetaData {
	
	private int minInstances;
	private int maxInstances;

	public BasicRubyRuntimePoolMetaData(int minInstances) {
		this( minInstances, -1 );
	}
	
	public BasicRubyRuntimePoolMetaData(int minInstances, int maxInstances) {
		this.minInstances = minInstances;
		this.maxInstances = maxInstances;
	}
	
	public int getMinInstances() {
		return this.minInstances;
	}
	
	public int getMaxInstances() {
		return this.maxInstances;
	}

}
