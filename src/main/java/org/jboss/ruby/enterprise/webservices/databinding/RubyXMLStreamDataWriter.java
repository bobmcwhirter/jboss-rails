package org.jboss.ruby.enterprise.webservices.databinding;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.jboss.logging.Logger;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;

public class RubyXMLStreamDataWriter {

	private Logger log = Logger.getLogger(RubyXMLStreamDataWriter.class);

	private RubyDataBinding dataBinding;

	private int namespaceCounter = 0;

	public RubyXMLStreamDataWriter(RubyDataBinding dataBinding) {
		this.dataBinding = dataBinding;
	}

	public void write(XMLStreamWriter output, Object object, QName concreteName) throws XMLStreamException {
		if (object instanceof IRubyObject) {
			write(output, (IRubyObject) object, concreteName);
		} else if ( object instanceof String ) {
			write( output, (String) object, concreteName );
		} else {
			log.info( "unhandled: " + object + " --> " + concreteName );
		}
	}

	public void write(XMLStreamWriter output, IRubyObject object, QName concreteName) throws XMLStreamException {
		String rubyClassName = object.getMetaClass().getName();
		log.info("rubyClass=" + rubyClassName);

		RubyType type = dataBinding.getTypeByClassName(rubyClassName);
		log.info("type is: " + type);
		
		if ( type instanceof RubyComplexType ) {
			write( output, object, concreteName, (RubyComplexType) type );
		} else if ( type instanceof RubyPrimitiveType ) {
			write( output, object, concreteName, (RubyPrimitiveType) type );
		}
			
	}
	
	public void write(XMLStreamWriter output, String text, QName concreteName) throws XMLStreamException {
		boolean addedNamespace = false;
		if (output.getNamespaceContext().getPrefix(concreteName.getNamespaceURI()) == null) {
			output.writeNamespace("rubyns" + (++namespaceCounter), concreteName.getNamespaceURI());
			addedNamespace = true;
		}
		output.writeStartElement(concreteName.getNamespaceURI(), concreteName.getLocalPart());
		
		output.writeCharacters( text );
		
		output.writeEndElement();
		if (addedNamespace) {
			--namespaceCounter;
		}
	}
	
	public void write(XMLStreamWriter output, IRubyObject object, QName concreteName, RubyPrimitiveType type) throws XMLStreamException {
	}
	
	
	public void write(XMLStreamWriter output, IRubyObject object, QName concreteName, RubyComplexType type) throws XMLStreamException {

		boolean addedNamespace = false;
		if (output.getNamespaceContext().getPrefix(concreteName.getNamespaceURI()) == null) {
			output.writeNamespace("rubyns" + (++namespaceCounter), concreteName.getNamespaceURI());
			addedNamespace = true;
		}
		output.writeStartElement(concreteName.getNamespaceURI(), concreteName.getLocalPart());
		log.info("<" + concreteName + ">");
		if (type instanceof RubyComplexType) {
			if ( type.isArraySubclass() ) {
				int len = readRubyArrayLength( object );
				log.info( "array size: " + len );
				for ( int i = 0 ; i < len ; ++i ) {
					Object member = readRubyArrayMember( object, i);
					log.info( "member " + i + ": " + member );
					write(output, member, ((RubyComplexType)type).getArrayAttribute().getQName() );
				}
				
			} else {
				for (RubyAttribute a : ((RubyComplexType) type).getAttributes()) {
					Object attrValue = readRubyAttributeValue(object, a.getName());
					write(output, attrValue, a.getQName());
				}
			}
		}
		output.writeEndElement();
		if (addedNamespace) {
			--namespaceCounter;
		}
	}

	private int readRubyArrayLength(IRubyObject object) {
		Integer length = (Integer) JavaEmbedUtils.invokeMethod(object.getRuntime(), object, "length", new Object[] {}, Integer.class);
		if ( length != null ) {
			return length.intValue();
		}
		
		return 0;
	}
	
	private Object readRubyArrayMember(IRubyObject object, int index) {
		Object member =  JavaEmbedUtils.invokeMethod(object.getRuntime(), object, "[]", new Object[] { new Integer( index )}, Object.class);
		return member;
	}

	private Object readRubyAttributeValue(IRubyObject object, String name) {
		return JavaEmbedUtils.invokeMethod(object.getRuntime(), object, name, new Object[] {}, Object.class);
	}

}
