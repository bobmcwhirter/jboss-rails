package org.jboss.ruby.enterprise.endpoints.databinding;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;

import org.apache.cxf.Bus;
import org.apache.cxf.service.model.SchemaInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.service.model.ServiceSchemaInfo;
import org.apache.cxf.wsdl.WSDLManager;
import org.apache.cxf.wsdl11.SchemaUtil;
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
import org.jboss.ruby.runtime.RubyDynamicClassLoader;
import org.w3c.dom.Element;

public class RubyTypeSpace {

	private static final String XML_SCHEMA_NS = "http://www.w3.org/2001/XMLSchema";
	private static final Logger log = Logger.getLogger(RubyTypeSpace.class);
	
	private URL wsdlLocation;

	private Bus bus;
	private RubyDynamicClassLoader classLoader;
	
	private Map<String, Element> schemaList = new HashMap<String, Element>();
	private Map<QName,RubyType> typesByQName = new HashMap<QName,RubyType>();
	private Map<String,RubyType> typesByClassName = new HashMap<String,RubyType>();
	private String rubyPath;

	public RubyTypeSpace() {

	}
	
	public void setWsdlLocation(URL wsdlLocation) {
		this.wsdlLocation = wsdlLocation;
	}

	public URL getWsdlLocation() {
		return this.wsdlLocation;
	}

	public void setBus(Bus bus) {
		this.bus = bus;
	}

	public Bus getBus() {
		return bus;
	}
	
	public void setRubyDynamicClassLoader(RubyDynamicClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
	public RubyDynamicClassLoader getRubyDynamicClassLoader() {
		return this.classLoader;
	}
	
	public void setRubyPath(String rubyPath) {
		this.rubyPath = rubyPath;
	}
	
	public String getRubyPath() {
		return this.rubyPath;
	}

	public void start() throws WSDLException, MalformedURLException {
		
		initializePrimitiveTypes();

		ServiceSchemaInfo serviceSchemaInfo = setUpSchemas();

		List<SchemaInfo> schemas = serviceSchemaInfo.getSchemaInfoList();

		for (SchemaInfo schema : schemas) {
			XmlSchema xmlSchema = schema.getSchema();
			loadSchema( xmlSchema );
		}

		log.debug( getRubyClassDefinitions() );
		
		this.classLoader.putFile( rubyPath + ".rb", getRubyClassDefinitions() );
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
	
	ServiceSchemaInfo setUpSchemas() throws WSDLException {
		WSDLManager wsdlManager = bus.getExtension(WSDLManager.class);

		Definition def = wsdlManager.getDefinition(wsdlLocation);
		
		ServiceSchemaInfo serviceSchemaInfo = wsdlManager.getSchemasForDefinition(def);

		if (serviceSchemaInfo == null) {
			SchemaUtil schemaUtil = new SchemaUtil(bus, this.schemaList);
			ServiceInfo serviceInfo = new ServiceInfo();
			schemaUtil.getSchemas(def, serviceInfo);
			serviceSchemaInfo = new ServiceSchemaInfo();
			serviceSchemaInfo.setSchemaElementList(this.schemaList);
			serviceSchemaInfo.setSchemaCollection(serviceInfo.getXmlSchemaCollection());
			serviceSchemaInfo.setSchemaInfoList(serviceInfo.getSchemas());
			if (wsdlManager != null) {
				wsdlManager.putSchemasForDefinition(def, serviceSchemaInfo);
			}
		}

		schemaList.putAll(serviceSchemaInfo.getSchemaElementList());
		
		return serviceSchemaInfo;
	}
	
	@SuppressWarnings("unchecked")
	private void loadSchema(XmlSchema schema) {
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
		RubyComplexType type = new RubyComplexType( (XmlSchemaComplexType) xsdType );
		this.typesByQName.put( name, type );
		this.typesByClassName.put( type.getName(), type );
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

	public void stop() {

	}

	public RubyType getTypeByQName(QName qname) {
		return this.typesByQName.get( qname );
	}

	public RubyType getTypeByClassName(String className) {
		return this.typesByClassName.get( className );
	}
}
