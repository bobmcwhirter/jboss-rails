package org.jboss.ruby.core.deployers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.vfs.spi.deployer.AbstractSimpleVFSRealDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.ruby.core.runtime.metadata.RubyLoadPathMetaData;
import org.jboss.virtual.VirtualFile;

public abstract class AbstractRubyLoadPathDescriber<T> extends AbstractSimpleVFSRealDeployer<T>{

	public AbstractRubyLoadPathDescriber(Class<T> input) {
		super( input );
		setStage( DeploymentStages.DESCRIBE );
		addOutput(RubyLoadPathMetaData.class);
	}
	
	protected void addLoadPath(VFSDeploymentUnit unit, URL url) {
		RubyLoadPathMetaData loadPathMetaData = new RubyLoadPathMetaData();
		loadPathMetaData.setURL( url );
		unit.addAttachment( RubyLoadPathMetaData.class.getName() + "$" + url, loadPathMetaData, RubyLoadPathMetaData.class );
	}
	
	protected void addLoadPath(VFSDeploymentUnit unit, VirtualFile file) throws MalformedURLException, URISyntaxException {
		addLoadPath( unit, file.toURL() );
	}
	
	protected void addLoadPath(VFSDeploymentUnit unit, String path) throws IOException, URISyntaxException {
		VirtualFile child = unit.getRoot().getChild( path );
		
		if ( child != null ) {
			addLoadPath( unit, child );
		}
	}
	
	
	

}
