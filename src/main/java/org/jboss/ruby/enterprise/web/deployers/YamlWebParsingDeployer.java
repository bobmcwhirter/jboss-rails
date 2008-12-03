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
package org.jboss.ruby.enterprise.web.deployers;

import java.util.Map;

import org.ho.yaml.Yaml;
import org.jboss.deployers.vfs.spi.deployer.AbstractVFSParsingDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.ruby.enterprise.web.metadata.RackWebMetaData;
import org.jboss.virtual.VirtualFile;

public class YamlWebParsingDeployer extends AbstractVFSParsingDeployer<RackWebMetaData>{

	public YamlWebParsingDeployer() {
		super( RackWebMetaData.class );
		setName( "jboss-web.yml" );
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected RackWebMetaData parse(VFSDeploymentUnit unit, VirtualFile file, RackWebMetaData root) throws Exception {
		
		Map<String,String> web = (Map<String, String>) Yaml.load( file.openStream() );
		
		
		String context = web.get( "context" );
		
		if ( context == null ) {
			context = "/";
		}
		
		String host = web.get( "host" );
		
		if ( host == null || host.trim().equals( "*") ) {
			host = "localhost";
		}
		
		
		RackWebMetaData webMetaData = new RackWebMetaData( context, host );
		return webMetaData;
	}

}
