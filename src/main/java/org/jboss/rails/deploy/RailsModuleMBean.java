package org.jboss.rails.deploy;

public interface RailsModuleMBean {

	public void create() throws Exception;

	public void start() throws Exception;

	public void stop() throws Exception;

	public void destroy() throws Exception;
}
