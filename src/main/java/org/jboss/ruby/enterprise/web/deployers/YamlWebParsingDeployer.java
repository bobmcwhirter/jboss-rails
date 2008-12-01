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
		log.info( "parsingin " + file );
		
		Map<String,String> web = (Map<String, String>) Yaml.load( file.openStream() );
		
		log.info( "web: " + web );
		
		String context = web.get( "context" );
		log.info( "RAW context: [" + context + "]");
		log.info( "DIRECT context: " + web.get( "context" ) );
		
		if ( context == null ) {
			context = "/";
		}
		
		log.info( "FINAL Context [" + context + "]" );
		
		String host = web.get( "host" );
		log.info( "RAW host: [" + host + "]");
		log.info( "DIRECT host: [" + web.get( "host" ) + "]");
		
		if ( host == null || host.trim().equals( "*") ) {
			host = "localhost";
		}
		
		log.info( "FINAL Host [" + host + "]" );
		
		RackWebMetaData webMetaData = new RackWebMetaData( context, host );
		log.info( "parsed to: " + webMetaData );
		return webMetaData;
	}

}
