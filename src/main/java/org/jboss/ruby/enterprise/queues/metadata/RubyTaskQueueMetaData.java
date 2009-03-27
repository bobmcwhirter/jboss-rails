package org.jboss.ruby.enterprise.queues.metadata;

public class RubyTaskQueueMetaData {
	
	private String queueClassName;

	public RubyTaskQueueMetaData() {
		
	}
	
	public void setQueueClassName(String queueClassName) {
		this.queueClassName = queueClassName;
	}
	
	public String getQueueClassName() {
		return this.queueClassName;
	}
	
	public String toString() {
		return "[RubyTaskQueue: queueClassName=" + queueClassName + "]";
	}

}
