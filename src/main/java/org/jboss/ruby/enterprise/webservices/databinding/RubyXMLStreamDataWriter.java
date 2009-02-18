package org.jboss.ruby.enterprise.webservices.databinding;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.jboss.logging.Logger;
import org.jboss.ruby.enterprise.webservices.databinding.complex.RubyAttribute;
import org.jboss.ruby.enterprise.webservices.databinding.complex.RubyComplexType;
import org.jboss.ruby.enterprise.webservices.databinding.simple.RubyBooleanType;
import org.jboss.ruby.enterprise.webservices.databinding.simple.RubyFloatType;
import org.jboss.ruby.enterprise.webservices.databinding.simple.RubyIntegerType;
import org.jboss.ruby.enterprise.webservices.databinding.simple.RubySimpleType;
import org.jboss.ruby.enterprise.webservices.databinding.simple.RubyStringType;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;

public class RubyXMLStreamDataWriter {

	private Logger log = Logger.getLogger(RubyXMLStreamDataWriter.class);

	private RubyDataBinding dataBinding;

	private int namespaceCounter = 0;

	public RubyXMLStreamDataWriter(RubyDataBinding dataBinding) {
		this.dataBinding = dataBinding;
	}
	
	public RubyType determineType(Object object) {
		RubyType type = null;
		
		if ( object instanceof IRubyObject ) {
			String className = ((IRubyObject)object).getMetaClass().getName();
			type = dataBinding.getTypeByClassName( className );
		} else if ( object instanceof String ) {
			return RubyStringType.INSTANCE;
		} else if ( object instanceof Float || object instanceof Double ) {
			return RubyFloatType.INSTANCE;
		} else if ( object instanceof Long || object instanceof Integer || object instanceof Short ) {
			return RubyIntegerType.INSTANCE;
		} else if ( object instanceof Boolean ) {
			return RubyBooleanType.INSTANCE;
		}
		
		return type;
	}

	public void write(XMLStreamWriter output, Object object, QName concreteName) throws XMLStreamException {
		log.info( "[top] write(" + object + ")" );
		RubyType type = determineType( object );
		log.info( "[top] write(..) type is : " + type );
		
		if (type instanceof RubyComplexType) {
			writeComplexWithType(output, (IRubyObject) object, concreteName, (RubyComplexType) type );
		} else if ( type instanceof RubySimpleType ) {
			writeSimpleWithType( output, object, concreteName, (RubySimpleType<?>) type );
		} else {
			log.info( "unhandled: " + object + " --> " + concreteName );
		}
	}
	
	public void writeWithType(XMLStreamWriter output, Object object, QName concreteName, RubyType type) throws XMLStreamException {
		log.info( "writeWithType(" + object + "," + concreteName + "," + type.getName() + ")" );
		if ( type instanceof RubyComplexType ) {
			writeComplexWithType( output, (IRubyObject) object, concreteName, (RubyComplexType) type );
		} else if ( type instanceof RubySimpleType ) {
			writeSimpleWithType( output, object, concreteName, (RubySimpleType<?>) type );
		} else {
			log.info( "unknown type: " + type );
		}
		
	}
	
	public void writeSimpleWithType(XMLStreamWriter output, Object object, QName concreteName, RubySimpleType<?> type) throws XMLStreamException {
		log.info( "writeSimpleWithType(" + object + "," + type.getName() );
		
		String textual = type.write( object );
		
		boolean addedNamespace = false;
		if (output.getNamespaceContext().getPrefix(concreteName.getNamespaceURI()) == null) {
			output.writeNamespace("rubyns" + (++namespaceCounter), concreteName.getNamespaceURI());
			addedNamespace = true;
		}
		output.writeStartElement(concreteName.getNamespaceURI(), concreteName.getLocalPart());
		
		output.writeCharacters( textual );
		
		output.writeEndElement();
		if (addedNamespace) {
			--namespaceCounter;
		}
	}
	
	public void writeComplexWithType(XMLStreamWriter output, IRubyObject object, QName concreteName, RubyComplexType type) throws XMLStreamException {
		log.info( "writeComplexWithType(" + object + "," + concreteName + "," + type.getName() + ")" );
		boolean addedNamespace = false;
		if (output.getNamespaceContext().getPrefix(concreteName.getNamespaceURI()) == null) {
			output.writeNamespace("rubyns" + (++namespaceCounter), concreteName.getNamespaceURI());
			addedNamespace = true;
		}
		output.writeStartElement(concreteName.getNamespaceURI(), concreteName.getLocalPart());
		log.info("<" + concreteName + ">");
		if (type instanceof RubyComplexType) {
			if ( type.isArraySubclass() ) {
				RubyType memberType = type.getArrayType();
				int len = readRubyArrayLength( object );
				log.info( "array size: " + len );
				for ( int i = 0 ; i < len ; ++i ) {
					Object member = readRubyArrayMember( object, i);
					log.info( "member " + i + ": " + member );
					writeWithType(output, member, ((RubyComplexType)type).getArrayAttribute().getQName(), memberType );
				}
			} else {
				for (RubyAttribute a : ((RubyComplexType) type).getAttributes()) {
					Object attrValue = readRubyAttributeValue(object, a.getName());
					writeWithType(output, attrValue, a.getQName(), a.getType() );
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
