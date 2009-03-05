/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
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
