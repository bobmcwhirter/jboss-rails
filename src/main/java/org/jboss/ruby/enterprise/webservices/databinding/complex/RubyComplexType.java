package org.jboss.ruby.enterprise.webservices.databinding.complex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaGroupBase;
import org.apache.ws.commons.schema.XmlSchemaParticle;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.jboss.logging.Logger;
import org.jboss.ruby.enterprise.webservices.databinding.RubyDataBinding;
import org.jboss.ruby.enterprise.webservices.databinding.RubyType;

public class RubyComplexType extends RubyType {

	private static final Logger log = Logger.getLogger(RubyComplexType.class);

	private XmlSchemaComplexType xsdType;
	private List<RubyAttribute> attributes = new ArrayList<RubyAttribute>();

	private Map<QName, RubyAttribute> attributesByElementName = new HashMap<QName, RubyAttribute>();
	private Map<String, RubyAttribute> attributesByName = new HashMap<String, RubyAttribute>();

	public RubyComplexType(RubyDataBinding dataBinding, XmlSchemaComplexType xsdType) {
		super(xsdType.getName());
		this.xsdType = xsdType;
	}

	public boolean isSimple() {
		return attributes.size() <= 1;
	}

	@SuppressWarnings("unchecked")
	protected void initialize(RubyDataBinding dataBinding) {
		XmlSchemaParticle particle = xsdType.getParticle();
		if (particle instanceof XmlSchemaGroupBase) {
			XmlSchemaGroupBase group = (XmlSchemaGroupBase) particle;
			for (Iterator<XmlSchemaParticle> iter = group.getItems().getIterator(); iter.hasNext();) {
				XmlSchemaParticle sequenceParticle = iter.next();
				if (sequenceParticle instanceof XmlSchemaElement) {
					XmlSchemaElement element = (XmlSchemaElement) sequenceParticle;
					QName elementTypeName = element.getSchemaTypeName();
					RubyType rubyAttrType = dataBinding.getTypeByQName(elementTypeName);
					RubyAttribute rubyAttr = new RubyAttribute(rubyAttrType, element);
					log.info("add attr " + rubyAttr.getName() + " on " + getName());
					this.attributes.add(rubyAttr);
					this.attributesByElementName.put(element.getQName(), rubyAttr);
					this.attributesByName.put(rubyAttr.getRubyName(), rubyAttr);
				}
			}
		}
	}

	public String getNewInstanceFragment() {
		return getName() + ".new()";
	}

	public String toString() {
		return "[RubyType: name=" + getName() + "; isSimple=" + isSimple() + "; xsdType=" + xsdType + "; attributes=" + this.attributes
				+ "]";
	}

	public String toRubyClass() {
		StringBuilder builder = new StringBuilder();

		String superClass = "";

		builder.append("# " + xsdType.getQName() + "\n");

		if (isArraySubclass()) {
			builder.append("# Array of " + getArrayType().getName() + "\n");
			superClass = " < ::Array";
		}

		builder.append("class " + getName() + superClass + "\n");
		builder.append("\n");

		if (isArraySubclass()) {
			builder.append(" def build()\n");
			builder.append("    " + attributes.get(0).getType().getNewInstanceFragment() + "\n");
			builder.append(" end\n");
			builder.append(" \n");
			builder.append(" def create()\n");
			builder.append("    o = build()\n");
			builder.append("    self << o\n");
			builder.append("    o\n");
			builder.append(" end\n");
		} else {
			for (RubyAttribute a : attributes) {
				builder.append("  attr_accessor :" + a.getRubyName() + "\n");
			}

			builder.append("\n");

			builder.append("  def initialize()\n");
			for (RubyAttribute a : attributes) {
				builder.append("    " + a.getInitializerFragment() + "\n");
			}
			builder.append("  end\n");
		}

		builder.append("\n");
		builder.append("end\n");

		return builder.toString();
	}

	public boolean isArraySubclass() {
		if (attributes.size() == 1) {
			if (attributes.get(0).getMaxOccurs() > 1) {
				return true;
			}
		}
		return false;

	}

	public RubyAttribute getArrayAttribute() {
		return attributes.get(0);
	}

	public RubyType getArrayType() {
		return attributes.get(0).getType();
	}

	public RubyAttribute getAttribute(QName qname) {
		return this.attributesByElementName.get(qname);
	}

	public RubyAttribute getAttribute(String name) {
		return this.attributesByName.get(name);
	}

	public List<RubyAttribute> getAttributes() {
		return Collections.unmodifiableList( this.attributes );
	}

}
