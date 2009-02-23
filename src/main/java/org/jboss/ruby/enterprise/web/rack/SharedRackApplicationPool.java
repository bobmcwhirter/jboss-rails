package org.jboss.ruby.enterprise.web.rack;

public class SharedRackApplicationPool implements RackApplicationPool {
	
	private RackApplication app;

	public SharedRackApplicationPool(RackApplicationFactory factory) throws Exception {
		this.app = factory.createRackApplication();
	}

	public RackApplication borrowApplication() {
		return this.app;
	}

	public void releaseApplication(RackApplication app) {
		// intentionally left blank.
	}

}
