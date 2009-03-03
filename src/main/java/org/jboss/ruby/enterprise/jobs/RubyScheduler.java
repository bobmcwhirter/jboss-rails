package org.jboss.ruby.enterprise.jobs;

import java.io.IOException;
import java.util.Properties;

import org.jboss.logging.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class RubyScheduler {

	private static final Logger log = Logger.getLogger(RubyScheduler.class);

	private String name;
	private Scheduler scheduler;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
	
	public Scheduler getScheduler() {
		return this.scheduler;
	}

	public void start() throws IOException, SchedulerException {
		log.info("Starting Ruby job scheduler: " + getName());
		Properties props = new Properties();
		props.load(this.getClass().getResourceAsStream("scheduler.properties"));
		props.setProperty(StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME, getName());
		StdSchedulerFactory factory = new StdSchedulerFactory(props);
		this.scheduler = factory.getScheduler();
		this.scheduler.start();
	}

	public void stop() throws SchedulerException {
		log.info("Stopping Ruby job scheduler: " + getName());
		this.scheduler.shutdown( true );
		log.info("Stopped Ruby job scheduler: " + getName());
	}

}
