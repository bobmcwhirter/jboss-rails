package org.jboss.ruby.enterprise.webservices.databinding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaParticle;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.jboss.logging.Logger;

public class RubyComplexType extends RubyType {

	private static final Logger log = Logger.getLogger(RubyComplexType.class);

	private XmlSchemaComplexType xsdType;
	private List<RubyAttribute> attributes = new ArrayList<RubyAttribute>();

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
		if (particle instanceof XmlSchemaSequence) {
			XmlSchemaSequence sequence = (XmlSchemaSequence) particle;
			for (Iterator<XmlSchemaParticle> iter = sequence.getItems().getIterator(); iter.hasNext();) {
				XmlSchemaParticle sequenceParticle = iter.next();
				if (sequenceParticle instanceof XmlSchemaElement) {
					XmlSchemaElement element = (XmlSchemaElement) sequenceParticle;
					String elementName = element.getName();
					QName elementTypeName = element.getSchemaTypeName();
					long minOccurs = element.getMinOccurs();
					long maxOccurs = element.getMaxOccurs();
					log.info("-->" + elementName + " is " + elementTypeName + " " + minOccurs + ".." + maxOccurs);
					RubyType rubyAttrType = dataBinding.getType(elementTypeName);
					log.info("RUBYTYPE: " + rubyAttrType);
					RubyAttribute rubyAttr = new RubyAttribute(rubyAttrType, element);
					this.attributes.add(rubyAttr);
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
			builder.append("# Array of " + getArrayType() + "\n" );
			superClass = " < ::Array";
		}

		builder.append("class " + getName() + superClass + "\n");

		if (!isArraySubclass()) {
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

	public String getArrayType() {
		return attributes.get(0).getType().getName();
	}

}
