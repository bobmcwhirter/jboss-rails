package org.jboss.ruby.enterprise.web.rack;

public interface RackApplicationPool {
	
	RackApplication borrowApplication();
	void releaseApplication(RackApplication app);

}
