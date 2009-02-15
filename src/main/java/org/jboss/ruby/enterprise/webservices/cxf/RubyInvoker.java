package org.jboss.ruby.enterprise.webservices.cxf;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageContentsList;
import org.apache.cxf.service.invoker.Invoker;
import org.jboss.logging.Logger;
import org.jboss.ruby.enterprise.webservices.RubyWebServiceHandler;
import org.jboss.ruby.enterprise.webservices.databinding.RubyDataObject;

public class RubyInvoker implements Invoker {

	private static final Logger log = Logger.getLogger(RubyInvoker.class);
	private RubyWebServiceHandler handler;

	public RubyInvoker(RubyWebServiceHandler handler) {
		this.handler = handler;
	}

	public RubyWebServiceHandler getHandler() {
		return this.handler;
	}

	public Object invoke(Exchange exchange, Object in) {
		Principal principal = (Principal) exchange.getInMessage().get("wss4j.principal.result");
		if (in instanceof MessageContentsList) {
			String operationName = getOperationName(exchange);
			MessageContentsList mcl = (MessageContentsList) in;
			Object request = mcl.get(0);
			Object response = handler.invoke(principal, operationName, request);
			return new MessageContentsList(response);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private String getOperationName(Exchange exchange) {
		Map<String, List<String>> headers = (Map<String, List<String>>) exchange.getInMessage().get(Message.PROTOCOL_HEADERS);
		List<String> operationNameHeader = headers.get("SOAPAction");
		String operationName = operationNameHeader.get(0);
		log.info("operationname=" + operationName);
		return operationName;
	}

}
