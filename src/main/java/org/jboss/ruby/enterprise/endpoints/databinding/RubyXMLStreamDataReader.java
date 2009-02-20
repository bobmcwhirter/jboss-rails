package org.jboss.ruby.enterprise.endpoints.databinding;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.jboss.logging.Logger;
import org.jboss.ruby.enterprise.endpoints.databinding.complex.RubyAttribute;
import org.jboss.ruby.enterprise.endpoints.databinding.complex.RubyComplexType;
import org.jboss.ruby.enterprise.endpoints.databinding.simple.RubySimpleType;
import org.jruby.Ruby;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;

public class RubyXMLStreamDataReader {
	private static final Logger log = Logger.getLogger(RubyXMLStreamDataReader.class);

	public RubyXMLStreamDataReader() {
	}

	public Object read(Ruby runtime, XMLStreamReader input, RubyType type) throws XMLStreamException {
		log.info("TOP read(" + type.getName() + ")");
		log.info(" input: " + input.getEventType());
		log.info(" input: " + input.getName() );
		QName name = input.getName();
		Object result = null;
		if (type instanceof RubyComplexType) {
			log.info("read complex");
			result = readComplex(runtime, input, (RubyComplexType) type);
		} else if (type instanceof RubySimpleType) {
			log.info("read primitive");
			result = readPrimitive(input, (RubySimpleType<?>) type);
			log.info(" --->" + result);
		}
		skipToEnd(input, name);
		return result;
	}

	public Object readPrimitive(XMLStreamReader input, RubySimpleType<?> type) throws XMLStreamException {
		QName name = input.getName();
		
		readXMLAttributesAndNamespaces(input);
		
		String text = collectText(input);
		Object result = type.read( text );
		
		skipToEnd( input , name);
		
		return result;
	}
	
	public String collectText(XMLStreamReader input) throws XMLStreamException {
		int event = input.next();
		
		StringBuilder text = new StringBuilder();
		
		while ( event == XMLStreamConstants.CHARACTERS || event == XMLStreamConstants.CDATA || event == XMLStreamConstants.SPACE ) {
			text.append( input.getText() );
			event = input.next();
		}
		
		return text.toString();
	}

	public Object readComplex(Ruby runtime, XMLStreamReader input, RubyComplexType type) throws XMLStreamException {

		log.info("readComplexType(..., " + type.getName() + ")  -- " + input.getEventType());

		QName name = input.getName();

		log.info("   read.current: " + name);

		readXMLAttributesAndNamespaces(input);

		while (input.next() != XMLStreamConstants.START_ELEMENT) {
			if (input.getEventType() == XMLStreamConstants.END_ELEMENT) {
				return null;
			}
			log.info("skip: " + input.getEventType());
		}

		IRubyObject rubyObject = null;

		if (type.isArraySubclass()) {
			rubyObject = createRubyObject(runtime, type);
			RubyAttribute memberAttr = type.getArrayAttribute();
			while (input.getEventType() == XMLStreamConstants.START_ELEMENT) {
				readArrayMember(runtime, input, rubyObject, memberAttr);
				input.nextTag();
			}
			// FIXME
			// result = rubyObject;
		} else {
			rubyObject = createRubyObject(runtime, type);
			while (input.getEventType() == XMLStreamConstants.START_ELEMENT) {
				readAttribute(runtime, input, type, rubyObject);
				input.nextTag();
			}
			// result = rubyObject;
		}

		return rubyObject;
	}

	private void skipToEnd(XMLStreamReader input, QName name) throws XMLStreamException {
		log.info("skipToEnd(..., " + name);
		int type = 0;
		while (true) {
			type = input.getEventType();

			switch (type) {
			case (XMLStreamConstants.START_ELEMENT):
				log.info("skip START: " + input.getName());
				break;
			case (XMLStreamConstants.END_ELEMENT):
				log.info("found END: " + input.getName());
				if (input.getName().equals(name)) {
					log.info("  RETURN");
				} else {
					log.info("  SKIP");
				}
				return;
			default:
				log.info("skip: " + input.getEventType());
			}
			type = input.next();
		}
	}

	private void readArrayMember(Ruby runtime, XMLStreamReader input, IRubyObject rubyObject, RubyAttribute arrayAttr)
			throws XMLStreamException {
		log.info("readArrayMember(" + arrayAttr.getName() + ")");

		String name = input.getName().getLocalPart();

		if (!arrayAttr.getName().equals(name)) {
			throw new XMLStreamException("expected <" + arrayAttr.getName() + "> but found <" + name + ">");
		}

		RubyType memberType = arrayAttr.getType();

		Object memberValue = read(runtime, input, memberType);

		addRubyArrayMember(rubyObject, memberValue);
	}

	private void addRubyArrayMember(IRubyObject rubyObject, Object memberValue) {
		JavaEmbedUtils.invokeMethod(rubyObject.getRuntime(), rubyObject, "<<", new Object[] { memberValue }, void.class);
	}

	private void readAttribute(Ruby runtime, XMLStreamReader input, RubyComplexType ownerType, IRubyObject rubyObject)
			throws XMLStreamException {
		log.info("readAttribute(" + input.getNamespaceURI() + ":" + input.getLocalName() + ") on " + ownerType.getName());

		String name = input.getName().getLocalPart();
		RubyAttribute rubyAttr = ownerType.getAttribute(name);

		if (rubyAttr == null) {
			throw new XMLStreamException("no attribute for <" + name + ">");
		}

		RubyType attrType = rubyAttr.getType();

		Object attrValue = read(runtime, input, attrType);

		setRubyAttribute(rubyObject, name, attrValue);
	}

	private void setRubyAttribute(IRubyObject rubyObject, String name, Object attrValue) {
		JavaEmbedUtils.invokeMethod(rubyObject.getRuntime(), rubyObject, name + "=", new Object[] { attrValue }, void.class);
	}

	private int readXMLAttributesAndNamespaces(XMLStreamReader input) throws XMLStreamException {
		int eventType = input.getEventType();
		if (eventType == XMLStreamConstants.ATTRIBUTE || eventType == XMLStreamConstants.NAMESPACE) {
			eventType = input.next();
		}
		return eventType;
	}

	private IRubyObject createRubyObject(Ruby runtime, RubyType type) {
		IRubyObject object = runtime.evalScriptlet(type.getNewInstanceFragment());
		return object;
	}

}
