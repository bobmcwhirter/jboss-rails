/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.rails.naming;

import java.io.File;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.apache.naming.resources.FileDirContext;


/** A resource context that adjusted for a public/ subdirectory and implicit index.html.
 * 
 * @author Bob McWhirter
 */
public class JBossFileDirContext extends FileDirContext {

	@Override
    public void setDocBase(String docBase) {
		super.setDocBase(docBase);
		base = new File( base, "public" );
    }
	
	@Override
	public String getDocBase() {
		return super.getDocBase() + "/public";
	}

	@Override
	public Object lookup(String name) throws NamingException {
		name = rewriteName( name );
		Object result = super.lookup(name);
		return result;
	}
	
	@Override
    public Attributes getAttributes(String name, String[] attrIds) throws NamingException {
    	name = rewriteName( name );
    	Attributes results = super.getAttributes( name, attrIds );
    	return results;
    }
	
	/** Rewrite the name if it signals an implicit welcome (index.html) file.
	 * 
	 * @param name The name to rewrite, maybe.
	 * @return The original or rewritten name, depending.
	 */
	private String rewriteName(String name) {
		if ( "/.html".equals( name ) ) {
			name = "/index.html";
		}
		return name;
	}
	

	
}