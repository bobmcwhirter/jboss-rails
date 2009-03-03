package org.jboss.ruby.enterprise.endpoints.cxf;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageContentsList;
import org.apache.cxf.service.invoker.Invoker;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.MessagePartInfo;
import org.jboss.logging.Logger;
import org.jboss.ruby.enterprise.endpoints.RubyEndpointHandler;

public class RubyEndpointInvoker implements Invoker {

	private static final Logger log = Logger.getLogger(RubyEndpointInvoker.class);
	private RubyEndpointHandler handler;

	public RubyEndpointInvoker(RubyEndpointHandler handler) {
		this.handler = handler;
	}

	public RubyEndpointHandler getHandler() {
		return this.handler;
	}

	public Object invoke(Exchange exchange, Object in) {
		
        BindingOperationInfo bop = exchange.get(BindingOperationInfo.class);
        MessagePartInfo partInfo = bop.getOutput().getMessageParts().get( 0 );
        
        QName responseType = partInfo.getTypeQName();
        
		Principal principal = (Principal) exchange.getInMessage().get("wss4j.principal.result");
		
		if (in instanceof MessageContentsList) {
			String operationName = getOperationName(exchange);
			MessageContentsList mcl = (MessageContentsList) in;
			Object request = mcl.get(0);
			Object response = handler.invoke(principal, operationName, request, responseType );
			return new MessageContentsList(response);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private String getOperationName(Exchange exchange) {
		Map<String, List<String>> headers = (Map<String, List<String>>) exchange.getInMessage().get(Message.PROTOCOL_HEADERS);
		List<String> operationNameHeader = headers.get("SOAPAction");
		String operationName = operationNameHeader.get(0);
		if ( operationName.startsWith( "\"" ) ) {
			operationName = operationName.substring( 1 );
		}
		
		if ( operationName.endsWith( "\"" ) ) {
			operationName = operationName.substring(0, operationName.length() - 1 );
		}
		return operationName;
	}

}
