package org.jboss.ruby.enterprise.webservices.cxf;

import java.util.Collection;

import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.dom.DOMSource;

import org.apache.cxf.binding.soap.Soap11;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.saaj.SAAJInInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Attachment;
import org.apache.cxf.staxutils.StaxUtils;
import org.jboss.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class DebugSAAJInInterceptor extends SAAJInInterceptor {

	private static final Logger log = Logger.getLogger(DebugSAAJInInterceptor.class);

	public DebugSAAJInInterceptor() {

	}

	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		log.info("start handleMessage(" + message + ")");
		MessageFactory factory = null;
		try {
			if (message.getVersion() instanceof Soap11) {
				factory = MessageFactory.newInstance();
			} else {
				factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
			}
			SOAPMessage soapMessage = factory.createMessage();
			log.info( "soapMessage=" + soapMessage );
            message.setContent(SOAPMessage.class, soapMessage);
            log.info( "  ---- A" );
            
            SOAPPart part = soapMessage.getSOAPPart();
            log.info( "  ---- B" );
            
            Document node = (Document) message.getContent(Node.class);
            log.info( "  ---- C" );
            DOMSource source = new DOMSource(node);
            log.info( "  ---- D" );
            part.setContent(source);
            log.info( "  ---- E" );
            
            // TODO: setup mime headers
            Collection<Attachment> atts = message.getAttachments();
            if (atts != null) {
                for (Attachment a : atts) {
                    AttachmentPart ap = soapMessage.createAttachmentPart(a.getDataHandler());
                    
                    soapMessage.addAttachmentPart(ap);
                }
            }
            log.info( "  ---- F" );
            
            //replace header element if necessary
            if (message.hasHeaders()) {
                replaceHeaders(soapMessage, message);
            }
            log.info( "  ---- G" );
            if (soapMessage.getSOAPHeader() == null) {
                soapMessage.getSOAPPart().getEnvelope().addHeader();
            }
            log.info( "  ---- H    " + soapMessage.getClass() );
            XMLStreamReader xmlReader = message.getContent(XMLStreamReader.class);
            log.info( "  ---- Haaaaa" );
            SOAPBody soapBody = soapMessage.getSOAPBody();
            log.info( "  ---- soapBody=" + soapBody );
            log.info( "  ---- soapBody.class=" + soapBody.getClass() );
            StaxUtils.readDocElements(soapBody, xmlReader, true);
            log.info( "  ---- I" );
            DOMSource bodySource = new DOMSource(soapMessage.getSOAPPart().getEnvelope().getBody());
            log.info( "  ---- J" );
            xmlReader = StaxUtils.createXMLStreamReader(bodySource);
            xmlReader.nextTag();
            xmlReader.nextTag(); // move past body tag
            message.setContent(XMLStreamReader.class, xmlReader); 
            log.info( "  ---- K" );
		} catch (Exception e) {
			e.printStackTrace();
			log.error( e );
		}

		log.info("factory complete");

		//super.handleMessage(message);
		log.info("completed handleMessage(" + message + ")");
	}

}
