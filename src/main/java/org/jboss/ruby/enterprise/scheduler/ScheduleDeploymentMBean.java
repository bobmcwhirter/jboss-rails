package org.jboss.ruby.enterprise.scheduler;

public interface ScheduleDeploymentMBean {
	
	public void start() throws Exception;
	public void stop();
	public String getStatus();

}
