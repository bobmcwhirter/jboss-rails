package org.jboss.ruby.enterprise.webservices.databinding;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.ws.commons.schema.XmlSchemaChoice;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaParticle;
import org.apache.ws.commons.schema.XmlSchemaSequence;

public class RubyBindingBuilder {
	
	private final String INDENT = "  ";
	private XmlSchemaComplexType xsdType;
	
	private StringBuilder rubyClass = new StringBuilder();
	private int indentLevel = 0;
	
	private Map<String,Boolean> attributes = new HashMap<String,Boolean>();

	public RubyBindingBuilder(XmlSchemaComplexType xsdType) {
		this.xsdType = xsdType;
	}
	
	protected void append(String... lines) {
		String indent = "";
		for ( int i = 0 ; i < indentLevel ; ++i ) {
			indent += INDENT;
		}
		for ( String line : lines ) {
			System.err.println(indent + line );
			rubyClass.append( indent + line );
			rubyClass.append( "\n" );
		}
	}
	
	protected void indent() {
		++this.indentLevel;
	}
	
	protected void dedent() {
		--this.indentLevel;
	}
	
	public void buildRubyClass() {
		
		append( "class " + xsdType.getName() );
		indent();
		
		XmlSchemaParticle particle = this.xsdType.getParticle();
		
		if ( particle instanceof XmlSchemaSequence ) {
			XmlSchemaSequence sequence = (XmlSchemaSequence) particle;
			
			buildAttributeAccessors( sequence );
			buildConstructors( sequence );
		}
		
		dedent();
		append( "end" );
		
	}

	@SuppressWarnings("unchecked")
	private void buildAttributeAccessors(XmlSchemaSequence sequence) {
		for ( Iterator<XmlSchemaObject> i = sequence.getItems().getIterator() ; i.hasNext() ; ) {
			XmlSchemaObject each = i.next();
			
			if ( each instanceof XmlSchemaElement ) {
				buildAttributeAccessor( (XmlSchemaElement) each );
			} else if ( each instanceof XmlSchemaChoice ) {
				buildAttributeAccessor( (XmlSchemaChoice) each );
			}
		}
		
	}
	
	private void buildAttributeAccessor(XmlSchemaElement element) {
		String attributeName = element.getName();
		long min = element.getMinOccurs();
		long max = element.getMaxOccurs();
		
		if ( max >= 1 ) {
			attributeName = pluralize( attributeName );
			this.attributes.put( attributeName, Boolean.TRUE );
		} else {
			this.attributes.put( attributeName, Boolean.FALSE );
		}
		
		append( "attr_accessor :" + attributeName );
	}

	private void buildAttributeAccessor(XmlSchemaChoice choice) {
		
	}
	
	private void buildConstructors(XmlSchemaSequence sequence) {
		buildDefaultConstructor( sequence );
	}

	private void buildDefaultConstructor(XmlSchemaSequence sequence) {
		append( "def initialize()" );
		indent();
		
		for ( String attributeName : this.attributes.keySet() ) {
			if ( this.attributes.get( attributeName ).booleanValue() ) {
				append( "@" + attributeName + " = []" );
			} else {
				append( "@" + attributeName + " = nil" );
			}
		}
		
		dedent();
		append( "end" );
	}
	
	private String pluralize(String attributeName) {
		if ( attributeName.endsWith( "s" ) ) {
			return attributeName + "es";
		}
		
		return attributeName + "s";
	}


}
