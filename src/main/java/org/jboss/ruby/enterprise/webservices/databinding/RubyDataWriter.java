package org.jboss.ruby.enterprise.webservices.databinding;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.validation.Schema;

import org.apache.cxf.databinding.DataWriter;
import org.apache.cxf.message.Attachment;
import org.apache.cxf.service.model.MessagePartInfo;
import org.jboss.logging.Logger;

public class RubyDataWriter<T> implements DataWriter<T> {
	
	private static final Logger log = Logger.getLogger( RubyDataWriter.class );

	private Collection<Attachment> attachments;
	private Map<String,Object> properties = new HashMap<String,Object>();
	private Schema schema;

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
	}

}
