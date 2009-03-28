package org.jboss.ruby.enterprise.queues;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.logging.Logger;
import org.jboss.ruby.enterprise.queues.deployers.BaseRubyMessageListener;

public class RubyTaskQueueHandler {

	private static final Logger log = Logger.getLogger(RubyTaskQueueHandler.class);

	private String queueName;
	
	public RubyTaskQueueHandler() {

	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public String getQueueName() {
		return this.queueName;
	}
	
	public void start() throws NamingException, JMSException {
		log.info("starting queue handler for " + queueName);
		InitialContext jndiContext = new InitialContext();

		//ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("java:JmsXA" );
		ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("java:/ConnectionFactory" );
		Queue queue = (Queue) jndiContext.lookup("queue/" + queueName);
		
		log.info( "using queue: " + queue );
		
		log.info( "connection factory: " + connectionFactory );
		
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false,  Session.AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer( queue );
        
        BaseRubyMessageListener listener = new BaseRubyMessageListener();
		consumer.setMessageListener( listener );
		
		log.info( "added " + listener + " to " + queue );
	}

	public void stop() throws JMSException {
	}

}
