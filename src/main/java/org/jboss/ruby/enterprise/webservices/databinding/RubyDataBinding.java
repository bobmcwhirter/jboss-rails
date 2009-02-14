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
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectTable;
import org.jboss.logging.Logger;
import org.w3c.dom.Node;

public class RubyDataBinding extends AbstractDataBinding {

	private static final Logger log = Logger.getLogger(RubyDataBinding.class);
	private static final Class<?>[] SUPPORTED_READER_FORMATS = new Class[] { Node.class };
	private static final Class<?>[] SUPPORTED_WRITER_FORMATS = new Class[] { Node.class };
	private static final String XML_SCHEMA_NS = "http://www.w3.org/2001/XMLSchema";
	
	private Map<QName,RubyType> types = new HashMap<QName,RubyType>();

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
		
	}

	private void loadType(QName name, XmlSchemaObject item) {
		log.info( "loadType(" + name + ", " + item );
		RubyType type = new RubyType( name.getLocalPart() );
		this.types.put( name, type );
	}
	
	RubyType getType(QName name) {
		return this.types.get( name );
	}

}
