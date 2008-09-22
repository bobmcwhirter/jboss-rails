package org.jboss.rails.catalina;

import java.io.File;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.apache.naming.resources.FileDirContext;

public class JBossFileDirContext extends FileDirContext {

	@Override
    public void setDocBase(String docBase) {
		super.setDocBase(docBase);
		base = new File( base, "public" );
    }
	
	public String getDocBase() {
		return super.getDocBase() + "/public";
	}

	@Override
	public Object lookup(String name) throws NamingException {
		name = rewriteName( name );
		Object result = super.lookup(name);
		System.err.println( "result ---> " + result );
		return result;
	}
	private String rewriteName(String name) {
		if ( "/.html".equals( name ) ) {
			name = "/index.html";
		}
		return name;
	}
	
    public Attributes getAttributes(String name, String[] attrIds) throws NamingException {
    	name = rewriteName( name );
    	Attributes results = super.getAttributes( name, attrIds );
    	return results;
    }

	
}
