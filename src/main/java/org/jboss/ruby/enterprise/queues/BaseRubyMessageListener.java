package org.jboss.ruby.enterprise.queues;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.jboss.logging.Logger;
import org.jboss.ruby.core.runtime.spi.RubyRuntimePool;
import org.jruby.Ruby;
import org.jruby.RubyModule;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;

public class BaseRubyMessageListener implements MessageListener {
	private static final Object[] EMPTY_OBJECT_ARRAY = {};

	private static final Logger log = Logger.getLogger(BaseRubyMessageListener.class);
	private RubyRuntimePool pool;
	private String queueClassName;
	private String classLocation;

	public BaseRubyMessageListener(RubyRuntimePool pool, String queueClassName, String classLocation) {
		this.pool = pool;
		this.queueClassName = queueClassName;
		this.classLocation = classLocation;
	}

	public void onMessage(Message message) {
		log.info("handling message: " + message);

		Ruby ruby = null;

		try {
			ruby = this.pool.borrowRuntime();

			loadQueueClassLocation(ruby);

			RubyModule queueClass = ruby.getClassFromPath(this.queueClassName);
			Object queueInstance = JavaEmbedUtils.invokeMethod(ruby, queueClass, "new", EMPTY_OBJECT_ARRAY, Object.class);

			log.info("queueInstance is " + queueInstance + " // " + queueInstance.getClass());

			String taskName = message.getStringProperty("TaskName");

			log.info("invoke task [" + taskName + "]");

			Object payload = ((ObjectMessage) message).getObject();

			if (message.getBooleanProperty("IsRubyMarshal")) {
				log.info("unmarshal ruby");
				RubyModule marshal = ruby.getClassFromPath("Marshal");
				payload = JavaEmbedUtils.invokeMethod(ruby, marshal, "restore", new Object[] { payload }, Object.class);
			}

			log.info("FINAL PAYLOAD [" + payload + "]");
			
			IRubyObject rubyQueueInstance = JavaEmbedUtils.javaToRuby( ruby, queueInstance );

			JavaEmbedUtils.invokeMethod(ruby, rubyQueueInstance, taskName, new Object[] { payload }, void.class);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			if (ruby != null) {
				this.pool.returnRuntime(ruby);
			}
		}

	}

	private void loadSupport(Ruby ruby) {
		String load = "load %q(jboss/queues/base_queue.rb)\n";
		ruby.evalScriptlet(load);
	}

	private void loadQueueClassLocation(Ruby ruby) {
		if (this.classLocation == null) {
			return;
		}
		loadSupport(ruby);
		String load = "load %q(" + this.classLocation + ".rb)\n";
		ruby.evalScriptlet(load);
	}

}
