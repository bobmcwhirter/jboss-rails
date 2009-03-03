package org.jboss.ruby.enterprise.endpoints.cxf;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.validation.Schema;

import org.apache.cxf.databinding.DataReader;
import org.apache.cxf.message.Attachment;
import org.apache.cxf.service.model.MessagePartInfo;
import org.jboss.logging.Logger;
import org.jboss.ruby.enterprise.endpoints.databinding.RubyType;
import org.jboss.ruby.enterprise.endpoints.databinding.RubyTypeSpace;
import org.jboss.ruby.enterprise.endpoints.databinding.RubyXMLStreamDataReader;
import org.jboss.ruby.runtime.RubyRuntimePool;
import org.jruby.Ruby;
import org.jruby.runtime.builtin.IRubyObject;

public class RubyDataReader<T> implements DataReader<T> {

	private static final Logger log = Logger.getLogger(RubyDataReader.class);

	private Collection<Attachment> attachments;
	private Map<String, Object> properties = new HashMap<String, Object>();
	private Schema schema;

	private RubyTypeSpace typeSpace;
	private RubyRuntimePool runtimePool;
	private String name;
	
	private RubyXMLStreamDataReader reader;

	public RubyDataReader(RubyTypeSpace typeSpace, RubyRuntimePool runtimePool, String name) {
		this.typeSpace = typeSpace;
		this.runtimePool = runtimePool;
		this.name = name;
		this.reader = new RubyXMLStreamDataReader();
	}

	public Object read(T input) {
		return read(null, input);
	}

	public Object read(MessagePartInfo partInfo, T input) {
		if (input instanceof XMLStreamReader) {
			return read(partInfo, (XMLStreamReader) input);
		}
		return null;
	}

	private Object read(MessagePartInfo partInfo, XMLStreamReader input) {
		Ruby runtime = null;
		RubyType type = typeSpace.getTypeByQName(partInfo.getTypeQName());
		try {
			runtime = runtimePool.borrowRuntime();
			runtime.evalScriptlet( "require %q(" + typeSpace.getRubyPath() + ")" );
			return (IRubyObject) reader.read(runtime, input, type);
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if ( runtime != null ) {
				runtimePool.returnRuntime( runtime );
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Object read(QName name, T node, Class type) {
		return read(node);
	}

	public void setAttachments(Collection<Attachment> attachments) {
		this.attachments = attachments;
	}

	public void setProperty(String name, Object value) {
		this.properties.put(name, value);
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}

}
