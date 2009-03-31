package org.jboss.ruby.enterprise.queues;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.logging.Logger;

public class RubyTaskQueueHandler {

	private static final Logger log = Logger.getLogger(RubyTaskQueueHandler.class);

	private String destinationName;

	private Connection connection;

	//private Session session;
	
	public RubyTaskQueueHandler() {

	}

	public void setQueueName(String destination) {
		this.destinationName = destination;
	}

	public String getQueueName() {
		return this.destinationName;
	}
	
	public void start() throws NamingException, JMSException {
		InitialContext jndiContext = new InitialContext();

		//ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("java:JmsXA" );
		ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("java:/ConnectionFactory" );
		Destination queue = (Destination) jndiContext.lookup("queue/" + destinationName);
		
        this.connection = connectionFactory.createConnection();
        Session session = connection.createSession(false,  Session.AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer( queue );
        
        BaseRubyMessageListener listener = new BaseRubyMessageListener();
		consumer.setMessageListener( listener );
		
		connection.start();
	}

	public void stop() throws JMSException {
		this.connection.close();
	}

}
