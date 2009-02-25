package org.jboss.ruby.enterprise.endpoints.cxf;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.validation.Schema;

import org.apache.cxf.databinding.DataWriter;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Attachment;
import org.apache.cxf.service.model.MessagePartInfo;
import org.jboss.logging.Logger;
import org.jboss.ruby.enterprise.endpoints.databinding.RubyTypeSpace;
import org.jboss.ruby.enterprise.endpoints.databinding.RubyXMLStreamDataWriter;

public class RubyDataWriter<T> implements DataWriter<T> {
	
	private static final Logger log = Logger.getLogger( RubyDataWriter.class );

	private Collection<Attachment> attachments;
	private Map<String,Object> properties = new HashMap<String,Object>();
	private Schema schema;
	
	private RubyXMLStreamDataWriter streamWriter;
	
	public RubyDataWriter(RubyTypeSpace typeSpace) {
		this.streamWriter = new RubyXMLStreamDataWriter( typeSpace );
	}
	
	public void setAttachments(Collection<Attachment> attachments) {
		this.attachments = attachments;
	}

	public void setProperty(String name, Object value) {
		this.properties.put( name, value );
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	public void write(Object object, T output) {
		log.info( "write(" + object + ", " + output + ")" );
		write( object, null, output );
	}

	public void write(Object object, MessagePartInfo partInfo, T output) {
		log.info( "write(" + object + ", " + partInfo + ", " + output + ")" );
		
		if ( output instanceof XMLStreamWriter ) {
			write( object, partInfo, (XMLStreamWriter) output );
		}
	}

	private void write(Object object, MessagePartInfo partInfo, XMLStreamWriter output) {
		try {
			streamWriter.write(output, object, partInfo.getConcreteName() );
		} catch (XMLStreamException e) {
			e.printStackTrace();
			throw new Fault( e );
		}
	}

}
