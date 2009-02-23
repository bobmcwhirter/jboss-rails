package org.jboss.ruby.enterprise.web.rack;

import java.io.InputStream;
import java.io.OutputStream;

public interface RackEnvironment {
	Object get(String key);
	InputStream getInput();
	OutputStream getErrors();

}
