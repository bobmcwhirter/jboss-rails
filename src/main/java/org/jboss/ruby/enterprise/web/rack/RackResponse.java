package org.jboss.ruby.enterprise.web.rack;

import javax.servlet.http.HttpServletResponse;

public interface RackResponse {
	public void respond(HttpServletResponse response);

}
