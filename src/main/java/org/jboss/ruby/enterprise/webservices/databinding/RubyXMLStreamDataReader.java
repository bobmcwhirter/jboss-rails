package org.jboss.ruby.enterprise.webservices.databinding;

import javax.xml.namespace.QName;
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
		log.info("read(" + input + ", " + type + ")");
		QName name = input.getName();

		log.info("name: " + name);
		log.info(" hasNext: " + input.hasNext());
		log.info(" hasText: " + input.hasText());
		if (input.hasText()) {
			log.info(" text: " + input.getText());
		}
		
		log.info( "START_ELEMENT: " + XMLStreamReader.START_ELEMENT );
		log.info( "END_ELEMENT: " + XMLStreamReader.END_ELEMENT );

		while (input.hasNext()) {
			int eventType = input.next();

			switch (eventType) {
			case XMLStreamReader.START_DOCUMENT:
				log.info("START_DOCUMENT");
				break;
			case XMLStreamReader.END_DOCUMENT:
				log.info("END_DOCUMENT");
				break;
			case XMLStreamReader.START_ELEMENT:
				log.info("START_ELEMENT: " + input.getName());
				break;
			case XMLStreamReader.END_ELEMENT:
				log.info("END_ELEMENT: " + input.getName());
				break;
			case XMLStreamReader.ATTRIBUTE:
				log.info("ATRIBUTE: " + input.getAttributeCount() );
				break;
			case XMLStreamReader.NAMESPACE:
				log.info("NAMESPACE: " + input.getNamespaceURI() + " // " + input.getPrefix() );
				break;
			case XMLStreamReader.CHARACTERS:
			case XMLStreamReader.CDATA:
			case XMLStreamReader.COMMENT:
			case XMLStreamReader.SPACE:
				log.info("TEXTY: " + input.getText());
				break;
			default:
				log.info( "unknown: " + eventType );
			}
		}

		return new RubyDataObject( type );
	}

}
