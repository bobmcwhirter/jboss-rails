package org.jboss.ruby.enterprise.webservices.databinding;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.validation.Schema;

import org.apache.cxf.databinding.DataReader;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Attachment;
import org.apache.cxf.service.model.MessagePartInfo;
import org.jboss.logging.Logger;
import org.jruby.Ruby;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;

public class RubyDataReader<T> implements DataReader<T> {

	private static final Logger log = Logger.getLogger(RubyDataReader.class);

	private Collection<Attachment> attachments;
	private Map<String, Object> properties = new HashMap<String, Object>();
	private Schema schema;

	private RubyDataBinding dataBinding;
	private RubyXMLStreamDataReader reader;

	public RubyDataReader(RubyDataBinding dataBinding) {
		this.dataBinding = dataBinding;
		this.reader = new RubyXMLStreamDataReader();
	}

	public Object read(T input) {
		log.info("read(" + input + ")");
		return read(null, input);
	}

	public Object read(MessagePartInfo partInfo, T input) {
		log.info("read(" + partInfo + ", " + input + ")");
		if (input instanceof XMLStreamReader) {
			return read(partInfo, (XMLStreamReader) input);
		}
		return null;
	}

	private Object read(MessagePartInfo partInfo, XMLStreamReader input) {
		log.info("read(" + partInfo + ", (XMLStreamReader) " + input + ")");
		log.info("  partName: " + partInfo.getName());
		log.info("  typeName: " + partInfo.getTypeQName());
		Ruby runtime = null;
		try {
			runtime = dataBinding.getRubyRuntimePool().borrowRuntime();
			System.err.println( "-\n-\n-\n-\n" + dataBinding.getRubyClassDefinitions() + "\n-\n-\n-\n" );
			runtime.evalScriptlet( dataBinding.getRubyClassDefinitions() );
			RubyType type = dataBinding.getType(partInfo.getTypeQName());
			IRubyObject o = (IRubyObject) reader.read(runtime, input, type);
			String insp = (String) JavaEmbedUtils.invokeMethod( o.getRuntime(), o, "inspect", new Object[]{}, String.class );
			log.info( "READ TO:\n" + insp );
			return o;
		} catch (XMLStreamException e) {
			throw new Fault(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Fault(e);
		} finally {
			if (runtime != null) {
				dataBinding.getRubyRuntimePool().returnRuntime(runtime);
			}
		}
	}

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
		log.info("setSchema(" + schema + ")");
		this.schema = schema;
	}

}
