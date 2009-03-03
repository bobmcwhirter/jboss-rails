package org.jboss.ruby.enterprise.crypto.deployers;

import java.util.Map;

import org.ho.yaml.Yaml;
import org.jboss.deployers.vfs.spi.deployer.AbstractVFSParsingDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.ruby.enterprise.crypto.metadata.CryptoMetaData;
import org.jboss.ruby.enterprise.crypto.metadata.CryptoStoreMetaData;
import org.jboss.virtual.VirtualFile;

public class CryptoYamlParsingDeployer extends AbstractVFSParsingDeployer<CryptoMetaData>{

	public CryptoYamlParsingDeployer() {
		super(CryptoMetaData.class);
		setName( "crypto.yml" );
	}

	@SuppressWarnings("unchecked")
	@Override
	protected CryptoMetaData parse(VFSDeploymentUnit unit, VirtualFile file, CryptoMetaData root) throws Exception {
		Map<String,Map<String,String>> crypto = (Map<String, Map<String,String>>) Yaml.load( file.openStream() );
		
		CryptoMetaData metaData = new CryptoMetaData();
		
		for ( String name : crypto.keySet() ) {
			CryptoStoreMetaData storeMetaData = new CryptoStoreMetaData();
			Map<String, String> store = crypto.get( name );
			storeMetaData.setName( name );
			storeMetaData.setStore( store.get( "store" ) );
			storeMetaData.setPassword( store.get( "password" ) );
			metaData.addCryptoStoreMetaData( storeMetaData );
		}
		
		return metaData;
	}

}
