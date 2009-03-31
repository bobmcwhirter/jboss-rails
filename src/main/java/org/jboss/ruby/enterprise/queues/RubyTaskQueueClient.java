package org.jboss.ruby.enterprise.queues;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.logging.Logger;

public class RubyTaskQueueClient {
	
	private static final Logger log = Logger.getLogger( RubyTaskQueueClient.class );
	
	private String destinationName;

	public RubyTaskQueueClient() {
		
	}
	
	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}
	
	public void enqueue(Object payload) throws NamingException, JMSException {
		InitialContext jndiContext = new InitialContext();
		ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("java:/ConnectionFactory" );
		Destination destination = (Destination) jndiContext.lookup("queue/" + destinationName);
		
		log.info( "using destination: " + destination );
		
		log.info( "connection factory: " + connectionFactory );
		
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false,  Session.AUTO_ACKNOWLEDGE);
        
        MessageProducer producer = session.createProducer( destination );
        
        Message message = session.createBytesMessage();
        
        log.info( "sending payload: " + payload );
        log.info( "sending message: " + message );
        
		producer.send( message );
	}

}
