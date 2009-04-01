package org.jboss.ruby.enterprise.queues;

import javax.jms.BytesMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.jboss.logging.Logger;
import org.jboss.ruby.runtime.RubyRuntimePool;
import org.jruby.Ruby;
import org.jruby.RubyModule;
import org.jruby.javasupport.JavaEmbedUtils;

public class BaseRubyMessageListener implements MessageListener {
	private static final Object[] EMPTY_OBJECT_ARRAY = {};
	
	private static final Logger log = Logger.getLogger( BaseRubyMessageListener.class );
	private RubyRuntimePool pool;
	private String queueClassName;
	private String classLocation;
	
	public BaseRubyMessageListener(RubyRuntimePool pool, String queueClassName, String classLocation) {
		this.pool = pool;
		this.queueClassName = queueClassName;
		this.classLocation = classLocation;
	}
	
	public void onMessage(Message message) {
		log.info( "handling message: " + message );
		
		Ruby ruby = null;
		
		try {
			ruby = this.pool.borrowRuntime();
			
			loadQueueClassLocation(ruby);
			
			RubyModule queueClass = ruby.getClassFromPath( this.queueClassName );
			Object queueInstance = JavaEmbedUtils.invokeMethod(ruby, queueClass, "new", EMPTY_OBJECT_ARRAY, Object.class );
			
			String taskName = message.getStringProperty( "TaskName" );
			
			log.info( "invoke task [" + taskName + "]" );
			
			
		} catch (Exception e) {
			log.error( e.getMessage(), e );
		} finally {
			if ( ruby != null ) {
				this.pool.returnRuntime( ruby );
			}
		}
		
	}

	private void loadQueueClassLocation(Ruby ruby) {
		if ( this.classLocation == null ) {
			return;
		}
		String load = "load %q(" + this.classLocation + ".rb)\n";
		ruby.evalScriptlet( load );
	}

}
