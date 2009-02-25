package org.jboss.ruby.enterprise.endpoints.cxf;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.cxf.databinding.AbstractDataBinding;
import org.apache.cxf.databinding.DataReader;
import org.apache.cxf.databinding.DataWriter;
import org.apache.cxf.service.Service;
import org.jboss.logging.Logger;
import org.jboss.ruby.enterprise.endpoints.databinding.RubyTypeSpace;
import org.jboss.ruby.runtime.RubyRuntimePool;
import org.w3c.dom.Node;

public class RubyDataBinding extends AbstractDataBinding {

	private static final Logger log = Logger.getLogger(RubyDataBinding.class);
	private static final Class<?>[] SUPPORTED_READER_FORMATS = new Class[] { Node.class };
	private static final Class<?>[] SUPPORTED_WRITER_FORMATS = new Class[] { Node.class };
	
	private String name;
	private RubyRuntimePool runtimePool;
	private RubyTypeSpace typeSpace;
	
	public RubyDataBinding(RubyRuntimePool runtimePool, String name) {
		this.runtimePool = runtimePool;
		this.name = name;
	}
	
	public void setRubyTypeSpace(RubyTypeSpace typeSpace) {
		this.typeSpace = typeSpace;
	}
	
	public RubyTypeSpace getRubyTypeSpace() {
		return this.typeSpace;
	}

	public <T> DataReader<T> createReader(Class<T> type) {
		log.info( "createReader(" + type + ")" );
		if ( type == XMLStreamReader.class ) {
			return new RubyDataReader<T>( this.typeSpace, this.runtimePool, name );
		}
		if ( type == Node.class ) {
			return new RubyDataReader<T>( this.typeSpace, this.runtimePool, name );
		}
		return null;
	}

	public <T> DataWriter<T> createWriter(Class<T> type) {
		log.info( "createWriter(" + type + ")" );
		if ( type == XMLStreamWriter.class ) {
			return (DataWriter<T>) new RubyDataWriter<T>( this.typeSpace );
		} 
		if ( type == Node.class ) {
			return (DataWriter<T>) new RubyDataWriter<T>( this.typeSpace );
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
		
	}

}
