package org.jboss.ruby.enterprise.web.rack;

public interface RackMiddleware {
	RackResponse call(Object env);
}
