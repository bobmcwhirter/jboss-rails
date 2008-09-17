package org.jboss.rails.rack;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.jruby.rack.RackFilter;

public class JBossRackFilter extends RackFilter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		System.err.println( "DO FILTER " + request );
		super.doFilter(request, response, chain);
		System.err.println( "DONE FILTER " + request );
	}
	
	

}
