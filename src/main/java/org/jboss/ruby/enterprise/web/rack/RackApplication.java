package org.jboss.ruby.enterprise.web.rack;

import javax.servlet.http.HttpServletRequest;

public interface RackApplication extends RackMiddleware {
	Object createEnvironment(HttpServletRequest request) throws Exception;
}
