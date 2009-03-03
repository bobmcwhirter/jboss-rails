package org.jboss.ruby.enterprise.jobs;

import org.jboss.logging.Logger;

public class BaseJobRb {
	
	private Logger logger;

	public BaseJobRb() {
		
	}
	
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	public Logger getLogger() {
		return this.logger;
	}

}
