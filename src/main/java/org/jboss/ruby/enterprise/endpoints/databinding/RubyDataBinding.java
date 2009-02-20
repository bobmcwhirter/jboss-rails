package org.jboss.ruby.enterprise.endpoints.databinding;

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
import org.jboss.ruby.enterprise.endpoints.databinding.complex.RubyComplexType;
import org.jboss.ruby.enterprise.endpoints.databinding.simple.RubyBooleanType;
import org.jboss.ruby.enterprise.endpoints.databinding.simple.RubyDateTimeType;
import org.jboss.ruby.enterprise.endpoints.databinding.simple.RubyFloatType;
import org.jboss.ruby.enterprise.endpoints.databinding.simple.RubyIntegerType;
import org.jboss.ruby.enterprise.endpoints.databinding.simple.RubyStringType;
import org.jboss.ruby.runtime.RubyRuntimePool;
import org.w3c.dom.Node;

public class RubyDataBinding extends AbstractDataBinding {

	private static final Logger log = Logger.getLogger(RubyDataBinding.class);
	private static final Class<?>[] SUPPORTED_READER_FORMATS = new Class[] { Node.class };
	private static final Class<?>[] SUPPORTED_WRITER_FORMATS = new Class[] { Node.class };
	private static final String XML_SCHEMA_NS = "http://www.w3.org/2001/XMLSchema";
	
	private RubyRuntimePool rubyRuntimePool;
	
	private Map<QName,RubyType> typesByQName = new HashMap<QName,RubyType>();
	private Map<String,RubyType> typesByClassName = new HashMap<String,RubyType>();
	
	public RubyDataBinding(RubyRuntimePool rubyRuntimePool) {
		this.rubyRuntimePool = rubyRuntimePool;
		initializePrimitiveTypes();
	}
	
	private void initializePrimitiveTypes() {
		
		typesByQName.put( new QName( XML_SCHEMA_NS, "string" ),  RubyStringType.INSTANCE );
		typesByQName.put( new QName( XML_SCHEMA_NS, "boolean" ), RubyBooleanType.INSTANCE );
		
		typesByQName.put( new QName( XML_SCHEMA_NS, "int" ),  RubyIntegerType.INSTANCE );
		typesByQName.put( new QName( XML_SCHEMA_NS, "integer" ), RubyIntegerType.INSTANCE );
		
		typesByQName.put( new QName( XML_SCHEMA_NS, "decimal" ), RubyFloatType.INSTANCE );
		typesByQName.put( new QName( XML_SCHEMA_NS, "float" ), RubyFloatType.INSTANCE );
		typesByQName.put( new QName( XML_SCHEMA_NS, "double" ), RubyFloatType.INSTANCE );
		
		//typesByQName.put( new QName( XML_SCHEMA_NS, "duration" ), new RubyPrimitiveType( "Duration", "Duration.new" ) );
		typesByQName.put( new QName( XML_SCHEMA_NS, "dateTime" ), RubyDateTimeType.INSTANCE );
		//typesByQName.put( new QName( XML_SCHEMA_NS, "time" ), new RubyPrimitiveType( "Time", "Time.now") );
		//typesByQName.put( new QName( XML_SCHEMA_NS, "date" ), new RubyPrimitiveType( "Time", "Time.now") );
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
			return (DataWriter<T>) new RubyDataWriter<T>( this );
		} 
		if ( type == Node.class ) {
			return (DataWriter<T>) new RubyDataWriter<T>( this );
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
		
		for ( RubyType type : this.typesByQName.values() ) {
			type.initialize( this );
		}
		
	}

	private void loadType(QName name, XmlSchemaObject xsdType) {
		RubyComplexType type = new RubyComplexType( this, (XmlSchemaComplexType) xsdType );
		this.typesByQName.put( name, type );
		this.typesByClassName.put( type.getName(), type );
	}
	
	public RubyType getTypeByQName(QName name) {
		return this.typesByQName.get( name );
	}
	
	public RubyType getTypeByClassName(String name) {
		return this.typesByClassName.get( name );
	}
	
	public String getRubyClassDefinitions() {
		StringBuilder defs = new StringBuilder();
		for ( RubyType type : this.typesByQName.values() ) {
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
