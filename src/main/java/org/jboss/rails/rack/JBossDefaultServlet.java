package org.jboss.rails.rack;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.catalina.servlets.DefaultServlet;
import org.jboss.logging.Logger;

public class JBossDefaultServlet extends DefaultServlet {
	/** The log */
	protected Logger log = Logger.getLogger(getClass());

	@Override
	public void init(ServletConfig config) throws ServletException {
		log.info("init!");
		super.init(config);
	}

	@Override
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		System.err.println("service!");
		log("service! (log)");
		log.info("service! via logging");
		try {
			super.service(req, res);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
