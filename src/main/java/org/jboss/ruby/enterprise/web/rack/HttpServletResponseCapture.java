package org.jboss.ruby.enterprise.web.rack;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class HttpServletResponseCapture extends HttpServletResponseWrapper {

	private int status;

	public HttpServletResponseCapture(HttpServletResponse response) {
		super(response);
	}

	@Override
	public void sendError(int status, String message) throws IOException {
		this.status = status;
	}

	@Override
	public void sendError(int status) throws IOException {
		this.status = status;
	}

	@Override
	public void sendRedirect(String path) throws IOException {
		this.status = 302;
		super.sendRedirect(path);
	}

	@Override
	public void setStatus(int status) {
		this.status = status;
		if (!isError()) {
			super.setStatus(status);
		}
	}

	@Override
	public void setStatus(int status, String message) {
		this.status = status;
		if (!isError()) {
			super.setStatus(status, message);
		}
	}

	@Override
	public void flushBuffer() throws IOException {
		if (!isError()) {
			super.flushBuffer();
		}
	}

	boolean isError() {
		return status >= 400;
	}
}
