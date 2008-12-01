package org.jboss.ruby.enterprise.web.deployers;

import java.util.Map;

import org.jboss.deployers.vfs.spi.deployer.AbstractVFSParsingDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.ruby.enterprise.web.metadata.RackWebMetaData;
import org.jboss.virtual.VirtualFile;
import org.jvyamlb.YAML;

public class YamlWebParsingDeployer extends AbstractVFSParsingDeployer<RackWebMetaData>{

	public YamlWebParsingDeployer() {
		super( RackWebMetaData.class );
		setName( "jboss-web.yml" );
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected RackWebMetaData parse(VFSDeploymentUnit unit, VirtualFile file, RackWebMetaData root) throws Exception {
		log.info( "parsingin " + file );
		
		Map<String,String> parsed = (Map<String, String>) YAML.load( file.openStream() );
		
		log.info( "value: " + parsed );
		
		String context = parsed.get( "context" );
		
		if ( context == null ) {
			context = "/";
		}
		
		String host = parsed.get( "host" );
		
		if ( host != null && host.trim().equals( "*") ) {
			host = null;
		}
		
		RackWebMetaData webMetaData = new RackWebMetaData( context, host );
		log.info( "parsed to: " + webMetaData );
		return webMetaData;
	}

}
