package org.jboss.ruby.enterprise.webservices.databinding;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.jboss.logging.Logger;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;

public class RubyXMLStreamDataWriter {
	
	private Logger log = Logger.getLogger( RubyXMLStreamDataWriter.class );
	
	private RubyDataBinding dataBinding;
	
	private int namespaceCounter = 0;
	
	public RubyXMLStreamDataWriter(RubyDataBinding dataBinding) {
		this.dataBinding = dataBinding;
	}
	
	public void write(XMLStreamWriter output, Object object, QName concreteName) throws XMLStreamException {
		if ( object instanceof IRubyObject ) {
			write( output, (IRubyObject) object, concreteName );
		}
	}
	public void write(XMLStreamWriter output, IRubyObject object, QName concreteName) throws XMLStreamException {
		String rubyClassName = object.getMetaClass().getName();
		log.info( "rubyClass=" + rubyClassName );
		
		RubyType type = dataBinding.getTypeByClassName( rubyClassName );
		log.info( "type is: " + type );
		
		boolean addedNamespace = false;
		if ( output.getNamespaceContext().getPrefix( concreteName.getNamespaceURI() ) == null ) {
			output.writeNamespace( "rubyns" + ( ++namespaceCounter ), concreteName.getNamespaceURI() );
			addedNamespace = true;
		}
		output.writeStartElement( concreteName.getNamespaceURI(),  concreteName.getLocalPart() );
		log.info( "<" + concreteName + ">" );
		if ( type instanceof RubyComplexType ) {
			for ( RubyAttribute a : ((RubyComplexType)type).getAttributes() ) {
				IRubyObject attrValue = readRubyAttributeValue( object, a.getName() );
				write( output, attrValue, a.getQName() );
			}
		}
		output.writeEndElement();
		if ( addedNamespace ) {
			--namespaceCounter;
		}
	}

	private IRubyObject readRubyAttributeValue(IRubyObject object, String name) {
		return (IRubyObject) JavaEmbedUtils.invokeMethod( object.getRuntime(), object, name, new Object[]{}, Object.class );
	}

}
