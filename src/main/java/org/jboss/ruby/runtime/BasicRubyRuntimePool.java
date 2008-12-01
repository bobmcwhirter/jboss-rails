package org.jboss.ruby.runtime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jruby.Ruby;

public class BasicRubyRuntimePool extends AbstractRubyRuntimePool {

	private List<Ruby> instances = new ArrayList<Ruby>();
	private Set<Ruby> availableInstances = new HashSet<Ruby>();
	private int minInstances = 0;
	private int maxInstances = -1;
	private int timeout = 30;

	public BasicRubyRuntimePool(RubyRuntimeFactory factory) {
		super(factory);
	}

	public void setMinInstances(int minInstances) {
		this.minInstances = minInstances;
	}

	public void setMaxInstances(int maxInstances) {
		this.maxInstances = maxInstances;
	}
	
	/** Time-out to fetch an instance.
	 * 
	 * @param timeout Time-out length, in seconds.
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public synchronized void start() throws Exception {
		prepopulateRuntimes();
	}

	private void prepopulateRuntimes() throws Exception {
		for (int i = 0; i < this.minInstances; ++i) {
			Ruby ruby = factory.createRubyRuntime();
			this.instances.add( ruby );
			this.availableInstances.add( ruby );
		}
	}

	public void stop() {
		this.instances.clear();
	}

	public synchronized Ruby borrowRuntime() throws Exception {
		if (availableInstances.isEmpty()) {
			if (this.maxInstances < 0 || this.instances.size() < this.maxInstances) {
				Ruby runtime = factory.createRubyRuntime();
				this.instances.add(runtime);
				return runtime;
			}
			if (this.instances.size() >= this.maxInstances) {
				while (availableInstances.isEmpty()) {
					wait( this.timeout * 1000 );
				}
			}
		}

		Iterator<Ruby> iterator = availableInstances.iterator();
		Ruby ruby = iterator.next();
		iterator.remove();
		return ruby;
	}

	public synchronized void returnRuntime(Ruby ruby) {
		this.availableInstances.add( ruby );
		notifyAll();
	}

}
