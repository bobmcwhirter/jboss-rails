package org.jboss.ruby.enterprise.queues;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.jboss.logging.Logger;

public class BaseRubyMessageListener implements MessageListener {
	
	private static final Logger log = Logger.getLogger( BaseRubyMessageListener.class );
	public BaseRubyMessageListener() {
		
	}

	public void onMessage(Message message) {
		log.info( "handling message: " + message );
		
	}

}
