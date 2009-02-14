package org.jboss.ruby.enterprise.webservices.databinding;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.jboss.logging.Logger;

public class RubyXMLStreamDataReader {
	private static final Logger log = Logger.getLogger(RubyXMLStreamDataReader.class);

	private RubyDataBinding dataBinding;

	public RubyXMLStreamDataReader(RubyDataBinding dataBinding) {
		this.dataBinding = dataBinding;
	}

	public Object read(XMLStreamReader input, RubyType type) throws XMLStreamException {
		log.info("read(" + input + ", " + type + ")  -- " + input.getEventType() );
		
		QName name = input.getName();
		
		log.info( "   QName: " + name );
		
		int eventType = input.next();
		
		eventType = readXMLAttributesAndNamespaces(input);
		
		while ( input.getEventType() == XMLStreamConstants.START_ELEMENT ) {
			readAttribute( input );
			input.nextTag();
		}

		return new RubyDataObject( type, null );
	}

	private int readAttribute(XMLStreamReader input) throws XMLStreamException {
		log.info( "readAttribute(" + input.getNamespaceURI() + ":" + input.getLocalName() + ")" );
		readXMLAttributesAndNamespaces(input);
		return 0;
	}

	private int readXMLAttributesAndNamespaces(XMLStreamReader input) throws XMLStreamException {
		int eventType = input.getEventType();
		if ( eventType == XMLStreamConstants.ATTRIBUTE || eventType == XMLStreamConstants.NAMESPACE ) {
			eventType = input.next();
		}
		return eventType;
	}
	
	

}
