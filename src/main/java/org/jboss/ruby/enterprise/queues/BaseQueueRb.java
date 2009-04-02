package org.jboss.ruby.enterprise.queues;

import org.jboss.logging.Logger;

public class BaseQueueRb {
	
	private Logger logger;

	public BaseQueueRb() {
		
	}
	
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	public Logger getLogger() {
		return this.logger;
	}

}
