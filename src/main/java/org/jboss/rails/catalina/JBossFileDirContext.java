package org.jboss.rails.catalina;

import java.io.File;

import javax.naming.NamingException;

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

	
}
