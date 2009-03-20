/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.cxf.staxutils;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.helpers.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public final class StaxUtils {

    private static final Logger LOG = LogUtils.getL7dLogger(StaxUtils.class);
    
    private static final XMLInputFactory XML_NS_AWARE_INPUT_FACTORY = XMLInputFactory.newInstance();
    private static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory.newInstance();
    private static final XMLOutputFactory XML_OUTPUT_FACTORY = XMLOutputFactory.newInstance();
    
    private static final String XML_NS = "http://www.w3.org/2000/xmlns/";
    
    static {
        XML_INPUT_FACTORY.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
        XML_NS_AWARE_INPUT_FACTORY.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, true);
    }
    
    private StaxUtils() {
    }

    /**
     * Return a cached, namespace-aware, factory.
     * @return
     */
    public static XMLInputFactory getXMLInputFactory() {
        return getXMLInputFactory(true);
    }
    
    /**
     * Return a cached factory.
     * @param nsAware
     * @return
     */
    public static XMLInputFactory getXMLInputFactory(boolean nsAware) {
        return nsAware ? XML_NS_AWARE_INPUT_FACTORY : XML_INPUT_FACTORY;
    }
    
    /**
     * Return a new factory so that the caller can set sticky parameters.
     * @param nsAware
     * @return
     */
    public static XMLInputFactory createXMLInputFactory(boolean nsAware) {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, nsAware);
        return factory;
    }

    public static XMLOutputFactory getXMLOutputFactory() {
        return XML_OUTPUT_FACTORY;
    }

    public static XMLStreamWriter createXMLStreamWriter(Writer out) {
        try {
            return getXMLOutputFactory().createXMLStreamWriter(out);
        } catch (XMLStreamException e) {
            throw new RuntimeException("Cant' create XMLStreamWriter", e);
        }
    } 
    
    public static XMLStreamWriter createXMLStreamWriter(OutputStream out) {
        return createXMLStreamWriter(out, null);
    }

    public static XMLStreamWriter createXMLStreamWriter(OutputStream out, String encoding) {
        if (encoding == null) {
            encoding = "UTF-8";
        }

        try {
            return getXMLOutputFactory().createXMLStreamWriter(out, encoding);
        } catch (XMLStreamException e) {
            throw new RuntimeException("Cant' create XMLStreamWriter", e);
        }
    }
    
    public static XMLStreamWriter createXMLStreamWriter(Result r) {
        try {
            return getXMLOutputFactory().createXMLStreamWriter(r);
        } catch (XMLStreamException e) {
            throw new RuntimeException("Cant' create XMLStreamWriter", e);
        }
    }

    public static XMLStreamReader createFilteredReader(XMLStreamReader reader, StreamFilter filter) {
        try {
            return getXMLInputFactory().createFilteredReader(reader, filter);
        } catch (XMLStreamException e) {
            throw new RuntimeException("Cant' create XMLStreamReader", e);
        }
    }

    public static void nextEvent(XMLStreamReader dr) {
        try {
            dr.next();
        } catch (XMLStreamException e) {
            throw new RuntimeException("Couldn't parse stream.", e);
        }
    }

    public static boolean toNextText(DepthXMLStreamReader reader) {
        if (reader.getEventType() == XMLStreamReader.CHARACTERS) {
            return true;
        }

        try {
            int depth = reader.getDepth();
            int event = reader.getEventType();
            while (reader.getDepth() >= depth && reader.hasNext()) {
                if (event == XMLStreamReader.CHARACTERS && reader.getDepth() == depth + 1) {
                    return true;
                }
                event = reader.next();
            }
            return false;
        } catch (XMLStreamException e) {
            throw new RuntimeException("Couldn't parse stream.", e);
        }
    }
    public static boolean toNextTag(XMLStreamReader reader) {
        try {
            // advance to first tag.
            int x = reader.getEventType();
            while (x != XMLStreamReader.START_ELEMENT
                && x != XMLStreamReader.END_ELEMENT
                && reader.hasNext()) {
                x = reader.next();
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException("Couldn't parse stream.", e);
        }
        return true;
    }

    public static boolean toNextTag(DepthXMLStreamReader reader, QName endTag) {
        try {
            int depth = reader.getDepth();
            int event = reader.getEventType();
            while (reader.getDepth() >= depth && reader.hasNext()) {
                if (event == XMLStreamReader.START_ELEMENT && reader.getName().equals(endTag) 
                    && reader.getDepth() == depth + 1) {
                    return true;
                }
                event = reader.next();
            }
            return false;
        } catch (XMLStreamException e) {
            throw new RuntimeException("Couldn't parse stream.", e);
        }
    }    
    
    public static void writeStartElement(XMLStreamWriter writer, String prefix, String name, String namespace)
        throws XMLStreamException {
        if (prefix == null) {
            prefix = "";
        }

        if (namespace.length() > 0) {
            writer.writeStartElement(prefix, name, namespace);
            if (prefix.length() > 0) {
                writer.writeNamespace(prefix, namespace);
            } else {
                writer.writeDefaultNamespace(namespace);
            }
        } else {
            writer.writeStartElement(name);
            writer.writeDefaultNamespace("");
        }
    }

    /**
     * Returns true if currently at the start of an element, otherwise move
     * forwards to the next element start and return true, otherwise false is
     * returned if the end of the stream is reached.
     */
    public static boolean skipToStartOfElement(XMLStreamReader in) throws XMLStreamException {
        for (int code = in.getEventType(); code != XMLStreamReader.END_DOCUMENT; code = in.next()) {
            if (code == XMLStreamReader.START_ELEMENT) {
                return true;
            }
        }
        return false;
    }

    public static boolean toNextElement(DepthXMLStreamReader dr) {
        if (dr.getEventType() == XMLStreamReader.START_ELEMENT) {
            return true;
        }
        if (dr.getEventType() == XMLStreamReader.END_ELEMENT) {
            return false;
        }
        try {
            int depth = dr.getDepth();

            for (int event = dr.getEventType(); dr.getDepth() >= depth && dr.hasNext(); event = dr.next()) {
                if (event == XMLStreamReader.START_ELEMENT && dr.getDepth() == depth + 1) {
                    return true;
                } else if (event == XMLStreamReader.END_ELEMENT) {
                    depth--;
                }
            }

            return false;
        } catch (XMLStreamException e) {
            throw new RuntimeException("Couldn't parse stream.", e);
        }
    }

    public static boolean skipToStartOfElement(DepthXMLStreamReader in) throws XMLStreamException {
        for (int code = in.getEventType(); code != XMLStreamReader.END_DOCUMENT; code = in.next()) {
            if (code == XMLStreamReader.START_ELEMENT) {
                return true;
            }
        }
        return false;
    }

    public static Document copy(Document doc) 
        throws XMLStreamException, ParserConfigurationException {
        
        XMLStreamReader reader = createXMLStreamReader(doc);
        W3CDOMStreamWriter writer = new W3CDOMStreamWriter();
        copy(reader, writer);
        Document d = writer.getDocument();
        d.setDocumentURI(doc.getDocumentURI());
        return d;
    }
    public static void copy(Document doc, XMLStreamWriter writer) throws XMLStreamException {
        XMLStreamReader reader = createXMLStreamReader(doc);
        copy(reader, writer);
    }
    public static void copy(Element node, XMLStreamWriter writer) throws XMLStreamException {
        XMLStreamReader reader = createXMLStreamReader(node);
        copy(reader, writer);
    }
    
    /**
     * Copies the reader to the writer. The start and end document methods must
     * be handled on the writer manually. TODO: if the namespace on the reader
     * has been declared previously to where we are in the stream, this probably
     * won't work.
     * 
     * @param reader
     * @param writer
     * @throws XMLStreamException
     */
    public static void copy(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        copy(reader, writer, false);
    }
    public static void copy(XMLStreamReader reader, XMLStreamWriter writer,
                            boolean fragment) throws XMLStreamException {
        // number of elements read in
        int read = 0;
        int event = reader.getEventType();

        while (reader.hasNext()) {
            switch (event) {
            case XMLStreamConstants.START_ELEMENT:
                read++;
                writeStartElement(reader, writer);
                break;
            case XMLStreamConstants.END_ELEMENT:
                writer.writeEndElement();
                read--;
                if (read <= 0 && !fragment) {
                    return;
                }
                break;
            case XMLStreamConstants.CHARACTERS:
                writer.writeCharacters(reader.getText());
                break;
            case XMLStreamConstants.COMMENT:
                writer.writeComment(reader.getText());
                break;
            case XMLStreamConstants.CDATA:
                writer.writeCData(reader.getText());
                break;
            case XMLStreamConstants.START_DOCUMENT:
            case XMLStreamConstants.END_DOCUMENT:
            case XMLStreamConstants.ATTRIBUTE:
            case XMLStreamConstants.NAMESPACE:
                break;
            default:
                break;
            }
            event = reader.next();
        }
    }

    private static void writeStartElement(XMLStreamReader reader, XMLStreamWriter writer)
        throws XMLStreamException {
        String local = reader.getLocalName();
        String uri = reader.getNamespaceURI();
        String prefix = reader.getPrefix();
        if (prefix == null) {
            prefix = "";
        }

        
//        System.out.println("STAXUTILS:writeStartElement : node name : " + local +  " namespace URI" + uri);
        boolean writeElementNS = false;
        if (uri != null) {
            String boundPrefix = writer.getPrefix(uri);
            if (boundPrefix == null || !prefix.equals(boundPrefix)) {
                writeElementNS = true;
            }
        }

        // Write out the element name
        if (uri != null) {
            if (prefix.length() == 0 && StringUtils.isEmpty(uri)) {
                writer.writeStartElement(local);
                writer.setDefaultNamespace(uri);

            } else {
                writer.writeStartElement(prefix, local, uri);
                writer.setPrefix(prefix, uri);
            }
        } else {
            writer.writeStartElement(local);
        }

        // Write out the namespaces
        for (int i = 0; i < reader.getNamespaceCount(); i++) {
            String nsURI = reader.getNamespaceURI(i);
            String nsPrefix = reader.getNamespacePrefix(i);
            if (nsPrefix == null) {
                nsPrefix = "";
            }

            if (nsPrefix.length() == 0) {
                writer.writeDefaultNamespace(nsURI);
            } else {
                writer.writeNamespace(nsPrefix, nsURI);
            }

            if (nsURI.equals(uri) && nsPrefix.equals(prefix)) {
                writeElementNS = false;
            }
        }

        // Check if the namespace still needs to be written.
        // We need this check because namespace writing works
        // different on Woodstox and the RI.
        if (writeElementNS) {
            if (prefix == null || prefix.length() == 0) {
                writer.writeDefaultNamespace(uri);
            } else {
                writer.writeNamespace(prefix, uri);
            }
        }        
        
        // Write out attributes
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String ns = reader.getAttributeNamespace(i);
            String nsPrefix = reader.getAttributePrefix(i);
            if (ns == null || ns.length() == 0) {
                writer.writeAttribute(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
            } else if (nsPrefix == null || nsPrefix.length() == 0) {
                writer.writeAttribute(reader.getAttributeNamespace(i), reader.getAttributeLocalName(i),
                                      reader.getAttributeValue(i));
            } else {
                writer.writeAttribute(reader.getAttributePrefix(i), reader.getAttributeNamespace(i), reader
                    .getAttributeLocalName(i), reader.getAttributeValue(i));
            }

        }
    }

    public static void writeDocument(Document d, XMLStreamWriter writer, boolean repairing)
        throws XMLStreamException {
        writeDocument(d, writer, true, repairing);
    }

    public static void writeDocument(Document d, XMLStreamWriter writer, boolean writeProlog,
                                     boolean repairing) throws XMLStreamException {
        if (writeProlog) {
            writer.writeStartDocument();
        }
        
        Node node = d.getFirstChild();
        while (node != null) {
            if (writeProlog || node.getNodeType() == Node.ELEMENT_NODE) {
                writeNode(node, writer, repairing);
            }
            node = node.getNextSibling();
        }
        
        if (writeProlog) {
            writer.writeEndDocument();
        }
    }

    /**
     * Writes an Element to an XMLStreamWriter. The writer must already have
     * started the document (via writeStartDocument()). Also, this probably
     * won't work with just a fragment of a document. The Element should be the
     * root element of the document.
     * 
     * @param e
     * @param writer
     * @throws XMLStreamException
     */
    public static void writeElement(Element e, XMLStreamWriter writer, boolean repairing) 
        throws XMLStreamException {
        writeElement(e, writer, repairing, true);
    }

    /**
     * Writes an Element to an XMLStreamWriter. The writer must already have
     * started the document (via writeStartDocument()). Also, this probably
     * won't work with just a fragment of a document. The Element should be the
     * root element of the document.
     * 
     * @param e
     * @param writer
     * @param endElement true if the element should be ended
     * @throws XMLStreamException
     */
    public static void writeElement(Element e,
                                    XMLStreamWriter writer,
                                    boolean repairing,
                                    boolean endElement)
        throws XMLStreamException {
        String prefix = e.getPrefix();
        String ns = e.getNamespaceURI();
        String localName = e.getLocalName();

       
//        System.out.println("local name : " + localName + " URI: " + ns + " Prefix :" + prefix);
        if (prefix == null) {
            prefix = "";
        }
        if (localName == null) {
            localName = e.getNodeName();

            if (localName == null) {
                throw new IllegalStateException("Element's local name cannot be null!");
            }
        }

        String decUri = writer.getNamespaceContext().getNamespaceURI(prefix);
        boolean declareNamespace = decUri == null || !decUri.equals(ns);

        if (ns == null || ns.length() == 0) {
            writer.writeStartElement(localName);
            if (StringUtils.isEmpty(decUri)) {
                declareNamespace = false;
            }
        } else {
//            System.out.println("Calling writeStartElement for local name : " 
//            + localName + " URI: " + ns + " Prefix :" + prefix);
            writer.writeStartElement(prefix, localName, ns);
        }

        NamedNodeMap attrs = e.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Node attr = attrs.item(i);

            String name = attr.getLocalName();
            String attrPrefix = attr.getPrefix();
            if (attrPrefix == null) {
                attrPrefix = "";
            }
            if (name == null) {
                name = attr.getNodeName();
            }
     
            if ("xmlns".equals(attrPrefix)) {
//                System.out.println("WriteNamespace is called for prefix : " 
//                + name + " namespace :" + attr.getNodeValue());
                writer.writeNamespace(name, attr.getNodeValue());
                if (name.equals(prefix) && attr.getNodeValue().equals(ns)) {
                    declareNamespace = false;
                }
            } else {
                if ("xmlns".equals(name) && "".equals(attrPrefix)) {
                    writer.writeNamespace("", attr.getNodeValue());
                    if (attr.getNodeValue().equals(ns)) {
                        declareNamespace = false;
                    } else if (StringUtils.isEmpty(attr.getNodeValue())
                        && StringUtils.isEmpty(ns)) {
                        declareNamespace = false;
                    }
                } else {
                    String attns = attr.getNamespaceURI();
                    String value = attr.getNodeValue();
                    if (attns == null || attns.length() == 0) {
                        writer.writeAttribute(name, value);
                    } else if (attrPrefix == null || attrPrefix.length() == 0) {
                        writer.writeAttribute(attns, name, value);
                    } else {
                        writer.writeAttribute(attrPrefix, attns, name, value);
                    }                    
                }
            }
        }

        if (declareNamespace && repairing) {
            if (ns == null) {
                writer.writeNamespace(prefix, "");
            } else {
                writer.writeNamespace(prefix, ns);
            }
        }

        Node nd = e.getFirstChild();
        while (nd != null) {
            writeNode(nd, writer, repairing);
            nd = nd.getNextSibling();
        }       

        if (endElement) {
            writer.writeEndElement();
        }
    }

    public static void writeNode(Node n, XMLStreamWriter writer, boolean repairing) 
        throws XMLStreamException {
        if (n instanceof Element) {
            writeElement((Element)n, writer, repairing);
        } else if (n instanceof CDATASection) {
            writer.writeCData(((CDATASection)n).getData());
        } else if (n instanceof Text) {
            writer.writeCharacters(((Text)n).getNodeValue());
        } else if (n instanceof Comment) {
            writer.writeComment(((Comment)n).getData());
        } else if (n instanceof EntityReference) {
            writer.writeEntityRef(((EntityReference)n).getNodeValue());
        } else if (n instanceof ProcessingInstruction) {
            ProcessingInstruction pi = (ProcessingInstruction)n;
            writer.writeProcessingInstruction(pi.getTarget(), pi.getData());
        } else if (n instanceof Document) {
            writeDocument((Document)n, writer, repairing);
        } 
    }

    public static Document read(XMLStreamReader reader) throws XMLStreamException {
        Document doc = DOMUtils.createDocument();
        readDocElements(doc, reader, true);
        return doc;
    }
    
    public static Document read(DocumentBuilder builder, XMLStreamReader reader, boolean repairing) 
        throws XMLStreamException {
        Document doc = builder.newDocument();

        readDocElements(doc, reader, repairing);

        return doc;
    }

    /**
     * @param parent
     * @return
     */
    private static Document getDocument(Node parent) {
        return (parent instanceof Document) ? (Document)parent : parent.getOwnerDocument();
    }

    /**
     * @param parent
     * @param reader
     * @return
     * @throws XMLStreamException
     */
    private static Element startElement(Node parent, XMLStreamReader reader, boolean repairing)
        throws XMLStreamException {
        Document doc = getDocument(parent);

        Element e = doc.createElementNS(reader.getNamespaceURI(), reader.getLocalName());
        if (reader.getPrefix() != null) {
            e.setPrefix(reader.getPrefix());
        }

        e = (Element) parent.appendChild(e);

        for (int ns = 0; ns < reader.getNamespaceCount(); ns++) {
            String uri = reader.getNamespaceURI(ns);
            String prefix = reader.getNamespacePrefix(ns);

            declare(e, uri, prefix);
        }

        for (int att = 0; att < reader.getAttributeCount(); att++) {
            String name = reader.getAttributeLocalName(att);
            String prefix = reader.getAttributePrefix(att);
            if (prefix != null && prefix.length() > 0) {
                name = prefix + ":" + name;
            }

            Attr attr = doc.createAttributeNS(reader.getAttributeNamespace(att), name);
            attr.setValue(reader.getAttributeValue(att));
            e.setAttributeNode(attr);
        }

        if (repairing && !isDeclared(e, reader.getNamespaceURI(), reader.getPrefix())) {
            declare(e, reader.getNamespaceURI(), reader.getPrefix());
        }

        reader.next();

        readDocElements(e, reader, repairing);

        return e;
    }

    private static boolean isDeclared(Element e, String namespaceURI, String prefix) {
        Attr att;
        if (prefix != null && prefix.length() > 0) {
            att = e.getAttributeNodeNS(XML_NS, "xmlns:" + prefix);
        } else {
            att = e.getAttributeNode("xmlns");
        }

        if (att != null && att.getNodeValue().equals(namespaceURI)) {
            return true;
        }

        if (e.getParentNode() instanceof Element) {
            return isDeclared((Element)e.getParentNode(), namespaceURI, prefix);
        }

        return false;
    }

    /**
     * @param parent
     * @param reader
     * @throws XMLStreamException
     */
    public static void readDocElements(Node parent, XMLStreamReader reader, boolean repairing)
        throws XMLStreamException {
        Document doc = getDocument(parent);

        int event = reader.getEventType();
        while (reader.hasNext()) {
            switch (event) {
            case XMLStreamConstants.START_ELEMENT:
                startElement(parent, reader, repairing);
                
                if (parent instanceof Document) {
                    return;
                }
                break;
            case XMLStreamConstants.END_ELEMENT:
                return;
            case XMLStreamConstants.NAMESPACE:
                break;
            case XMLStreamConstants.ATTRIBUTE:
                break;
            case XMLStreamConstants.CHARACTERS:
                if (parent != null) {
                    parent.appendChild(doc.createTextNode(reader.getText()));
                }

                break;
            case XMLStreamConstants.COMMENT:
                if (parent != null) {
                    parent.appendChild(doc.createComment(reader.getText()));
                }

                break;
            case XMLStreamConstants.CDATA:
                parent.appendChild(doc.createCDATASection(reader.getText()));

                break;
            case XMLStreamConstants.PROCESSING_INSTRUCTION:
                parent.appendChild(doc.createProcessingInstruction(reader.getPITarget(), reader.getPIData()));

                break;
            case XMLStreamConstants.ENTITY_REFERENCE:
                parent.appendChild(doc.createProcessingInstruction(reader.getPITarget(), reader.getPIData()));

                break;
            default:
                break;
            }

            if (reader.hasNext()) {
                event = reader.next();
            }
        }
    }

    private static void declare(Element node, String uri, String prefix) {
        String qualname;
        if (prefix != null && prefix.length() > 0) {
            qualname = "xmlns:" + prefix;
        } else {
            qualname = "xmlns";
        }
        Attr attr = node.getOwnerDocument().createAttributeNS(XML_NS, qualname);
        attr.setValue(uri);
        node.setAttributeNodeNS(attr);
    }

    /**
     * @param in
     * @param encoding
     * @param ctx
     * @return
     */
    public static XMLStreamReader createXMLStreamReader(InputStream in, String encoding) {
        if (encoding == null) {
            encoding = "UTF-8";
        }

        try {
            return getXMLInputFactory().createXMLStreamReader(in, encoding);
        } catch (XMLStreamException e) {
            throw new RuntimeException("Couldn't parse stream.", e);
        }
    }

    /**
     * @param in
     * @return
     */
    public static XMLStreamReader createXMLStreamReader(InputStream in) {
        try {
            return getXMLInputFactory().createXMLStreamReader(in);
        } catch (XMLStreamException e) {
            throw new RuntimeException("Couldn't parse stream.", e);
        }
    }
    
    public static XMLStreamReader createXMLStreamReader(Element el) {
        return new W3CDOMStreamReader(el);
    }
    public static XMLStreamReader createXMLStreamReader(Document doc) {
        return new W3CDOMStreamReader(doc.getDocumentElement());
    }
    public static XMLStreamReader createXMLStreamReader(Source source) {
        try {
            if (source instanceof DOMSource) {
                DOMSource ds = (DOMSource)source;
                Node nd = ds.getNode();
                Element el = null;
                if (nd instanceof Document) {
                    el = ((Document)nd).getDocumentElement();
                } else if (nd instanceof Element) {
                    el = (Element)nd;
                }
                
                if (null != el) {
                    return new W3CDOMStreamReader(el);
                }
            }
            
            return getXMLInputFactory().createXMLStreamReader(source);
        } catch (XMLStreamException e) {
            throw new RuntimeException("Couldn't parse stream.", e);
        }
    }

    /**
     * @param reader
     * @return
     */
    public static XMLStreamReader createXMLStreamReader(Reader reader) {

        try {
            return getXMLInputFactory().createXMLStreamReader(reader);
        } catch (XMLStreamException e) {
            throw new RuntimeException("Couldn't parse stream.", e);
        }
    }

    /**
     * Reads a QName from the element text. Reader must be positioned at the
     * start tag.
     */
    public static QName readQName(XMLStreamReader reader) throws XMLStreamException {
        String value = reader.getElementText();
        if (value == null) {
            return null;
        }
        
        int index = value.indexOf(":");

        if (index == -1) {
            return new QName(value);
        }

        String prefix = value.substring(0, index);
        String localName = value.substring(index + 1);
        String ns = reader.getNamespaceURI(prefix);

        if ((prefix != null && ns == null) || localName == null) {
            throw new RuntimeException("Invalid QName in mapping: " + value);
        }

        if (ns == null) {
            return new QName(localName);
        }
        
        return new QName(ns, localName, prefix);
    }
    

    /**
     * Create a unique namespace uri/prefix combination.
     * 
     * @param nsUri
     * @return The namespace with the specified URI. If one doesn't exist, one
     *         is created.
     * @throws XMLStreamException
     */
    public static String getUniquePrefix(XMLStreamWriter writer, String namespaceURI, boolean declare)
        throws XMLStreamException {
        String prefix = writer.getPrefix(namespaceURI);
        if (prefix == null) {
            prefix = getUniquePrefix(writer);

            if (declare) {
                writer.setPrefix(prefix, namespaceURI);
                writer.writeNamespace(prefix, namespaceURI);
            }
        }
        return prefix;
    }

    public static String getUniquePrefix(XMLStreamWriter writer) {
        int n = 1;
        
        NamespaceContext nc = writer.getNamespaceContext();
        while (true) {
            String nsPrefix = "ns" + n;

            if (nc == null || nc.getNamespaceURI(nsPrefix) == null) {
                return nsPrefix;
            }

            n++;
        }
    }
    

    public static void printXmlFragment(XMLStreamReader reader) {
        try {
            LOG.info(XMLUtils.toString(StaxUtils.read(reader), 4));
        } catch (XMLStreamException e) {
            LOG.severe(e.getMessage());
        }
    }
    
    
    @SuppressWarnings("unchecked")
	private static void writeStartElementEvent(XMLEvent event, XMLStreamWriter writer) 
        throws XMLStreamException {
        StartElement start = event.asStartElement();
        QName name = start.getName();
        String nsURI = name.getNamespaceURI();
        String localName = name.getLocalPart();
        String prefix = name.getPrefix();
        
        if (prefix != null) {
            writer.writeStartElement(prefix, localName, nsURI);
        } else if (nsURI != null) {
            writer.writeStartElement(localName, nsURI);
        } else {
            writer.writeStartElement(localName);
        }
        Iterator<XMLEvent> it = start.getNamespaces();
        while (it != null && it.hasNext()) {
            writeEvent(it.next(), writer);
        }
        
        it = start.getAttributes();
        while (it != null && it.hasNext()) {
            writeAttributeEvent((Attribute)it.next(), writer);            
        }
    }
    private static void writeAttributeEvent(XMLEvent event, XMLStreamWriter writer) 
        throws XMLStreamException {
        
        Attribute attr = (Attribute)event;
        QName name = attr.getName();
        String nsURI = name.getNamespaceURI();
        String localName = name.getLocalPart();
        String prefix = name.getPrefix();
        String value = attr.getValue();

        if (prefix != null) {
            writer.writeAttribute(prefix, nsURI, localName, value);
        } else if (nsURI != null) {
            writer.writeAttribute(nsURI, localName, value);
        } else {
            writer.writeAttribute(localName, value);
        }
    }

    public static void writeEvent(XMLEvent event, XMLStreamWriter writer)
        throws XMLStreamException {

        switch (event.getEventType()) {
        case XMLEvent.START_ELEMENT:
            writeStartElementEvent(event, writer);
            break;
        case XMLEvent.END_ELEMENT:
            writer.writeEndElement();
            break;
        case XMLEvent.ATTRIBUTE: 
            writeAttributeEvent(event, writer);
            break;
        case XMLEvent.ENTITY_REFERENCE:
            writer.writeEntityRef(((javax.xml.stream.events.EntityReference)event).getName());
            break;
        case XMLEvent.DTD:
            writer.writeDTD(((DTD)event).getDocumentTypeDeclaration());
            break;
        case XMLEvent.PROCESSING_INSTRUCTION:
            if (((javax.xml.stream.events.ProcessingInstruction)event).getData() != null) {
                writer.writeProcessingInstruction(
                    ((javax.xml.stream.events.ProcessingInstruction)event).getTarget(), 
                    ((javax.xml.stream.events.ProcessingInstruction)event).getData());
            } else {
                writer.writeProcessingInstruction(
                    ((javax.xml.stream.events.ProcessingInstruction)event).getTarget());
            }
            break;
        case XMLEvent.NAMESPACE:
            if (((Namespace)event).isDefaultNamespaceDeclaration()) {
                writer.writeDefaultNamespace(((Namespace)event).getNamespaceURI());
            } else {
                writer.writeNamespace(((Namespace)event).getPrefix(),
                                      ((Namespace)event).getNamespaceURI());
            }
            break;
        case XMLEvent.COMMENT:
            writer.writeComment(((javax.xml.stream.events.Comment)event).getText());
            break;
        case XMLEvent.CHARACTERS:
        case XMLEvent.SPACE:
            writer.writeCharacters(event.asCharacters().getData());
            break;
        case XMLEvent.CDATA:
            writer.writeCData(event.asCharacters().getData());
            break;
        case XMLEvent.START_DOCUMENT:
            if (((StartDocument)event).encodingSet()) {
                writer.writeStartDocument(((StartDocument)event).getCharacterEncodingScheme(),
                                          ((StartDocument)event).getVersion());

            } else {
                writer.writeStartDocument(((StartDocument)event).getVersion());
            }
            break;
        case XMLEvent.END_DOCUMENT:
            writer.writeEndDocument();
            break;
        default:
            //shouldn't get here
        }
    }
}
