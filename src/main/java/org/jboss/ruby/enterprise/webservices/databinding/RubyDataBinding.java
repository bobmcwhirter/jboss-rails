package org.jboss.ruby.enterprise.webservices.databinding;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.cxf.common.xmlschema.SchemaCollection;
import org.apache.cxf.databinding.AbstractDataBinding;
import org.apache.cxf.databinding.DataReader;
import org.apache.cxf.databinding.DataWriter;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectTable;
import org.jboss.logging.Logger;
import org.jboss.ruby.runtime.RubyRuntimePool;
import org.jruby.Ruby;
import org.w3c.dom.Node;

public class RubyDataBinding extends AbstractDataBinding {

	private static final Logger log = Logger.getLogger(RubyDataBinding.class);
	private static final Class<?>[] SUPPORTED_READER_FORMATS = new Class[] { Node.class };
	private static final Class<?>[] SUPPORTED_WRITER_FORMATS = new Class[] { Node.class };
	private static final String XML_SCHEMA_NS = "http://www.w3.org/2001/XMLSchema";
	
	private RubyRuntimePool rubyRuntimePool;
	
	private Map<QName,RubyType> types = new HashMap<QName,RubyType>();
	
	public RubyDataBinding(RubyRuntimePool rubyRuntimePool) {
		this.rubyRuntimePool = rubyRuntimePool;
		initializePrimitiveTypes();
	}
	
	private void initializePrimitiveTypes() {
		RubyPrimitiveType unimplementedPrimitive = new RubyPrimitiveType( "Unimplemented", "nil" );
		
		types.put( new QName( XML_SCHEMA_NS, "string" ),  new RubyPrimitiveType( "String", "''" ) );
		types.put( new QName( XML_SCHEMA_NS, "boolean" ), new RubyPrimitiveType( "Boolean", "false" ) );
		types.put( new QName( XML_SCHEMA_NS, "int" ),  new RubyPrimitiveType( "Integer", "0" ) );
		types.put( new QName( XML_SCHEMA_NS, "integer" ), new RubyPrimitiveType( "Integer", "0" ) );
		types.put( new QName( XML_SCHEMA_NS, "decimal" ), unimplementedPrimitive );
		types.put( new QName( XML_SCHEMA_NS, "float" ), unimplementedPrimitive );
		types.put( new QName( XML_SCHEMA_NS, "double" ), unimplementedPrimitive ); 
		types.put( new QName( XML_SCHEMA_NS, "duration" ), unimplementedPrimitive );
		types.put( new QName( XML_SCHEMA_NS, "dateTime" ), unimplementedPrimitive );
		types.put( new QName( XML_SCHEMA_NS, "time" ), unimplementedPrimitive );
		types.put( new QName( XML_SCHEMA_NS, "date" ), unimplementedPrimitive );
	}

	public <T> DataReader<T> createReader(Class<T> type) {
		log.info( "createReader(" + type + ")" );
		if ( type == XMLStreamReader.class ) {
			return new RubyDataReader<T>( this );
		}
		if ( type == Node.class ) {
			return new RubyDataReader<T>( this );
		}
		return null;
	}

	public <T> DataWriter<T> createWriter(Class<T> type) {
		log.info( "createWriter(" + type + ")" );
		if ( type == XMLStreamWriter.class ) {
			return (DataWriter<T>) new RubyDataWriter<T>();
		} 
		if ( type == Node.class ) {
			return (DataWriter<T>) new RubyDataWriter<T>();
		}
		return null;
	}

	public Class<?>[] getSupportedReaderFormats() {
		return SUPPORTED_READER_FORMATS;
	}

	public Class<?>[] getSupportedWriterFormats() {
		return SUPPORTED_WRITER_FORMATS;
	}

	public void initialize(Service service) {
		log.info( "initialize(" + service + ")" );
		for ( ServiceInfo serviceInfo : service.getServiceInfos() ) {
			loadSchemas( serviceInfo );
		}
	}

	private void loadSchemas(ServiceInfo serviceInfo) {
		log.info( "loadSchema(" + serviceInfo + ")" );
		SchemaCollection schemas = serviceInfo.getXmlSchemaCollection();
		for ( XmlSchema schema : schemas.getXmlSchemas() ) {
			if ( schema.getTargetNamespace().equals( XML_SCHEMA_NS ) ) {
				log.info( "skip XMLSchema" );
				continue;
			}
			loadSchema( schema );
		}
	}

	@SuppressWarnings("unchecked")
	private void loadSchema(XmlSchema schema) {
		log.info( "loadSchema(" + schema + ")" );
		XmlSchemaObjectTable types = schema.getSchemaTypes();
		
		for ( Iterator<QName> i = types.getNames(); i.hasNext() ; ) {
			QName name = i.next();
			loadType( name, types.getItem( name ) );
		}
		
		for ( RubyType type : this.types.values() ) {
			type.initialize( this );
		}
		
	}

	private void loadType(QName name, XmlSchemaObject xsdType) {
		RubyComplexType type = new RubyComplexType( this, (XmlSchemaComplexType) xsdType );
		this.types.put( name, type );
	}
	
	RubyType getType(QName name) {
		return this.types.get( name );
	}
	
	public String getRubyClassDefinitions() {
		StringBuilder defs = new StringBuilder();
		for ( RubyType type : this.types.values() ) {
			if ( type instanceof RubyComplexType ) {
				defs.append( ((RubyComplexType)type).toRubyClass() );
				defs.append( "\n" );
			}
		}
		
		return defs.toString();
	}

	public RubyRuntimePool getRubyRuntimePool() {
		return this.rubyRuntimePool;
	}

}
