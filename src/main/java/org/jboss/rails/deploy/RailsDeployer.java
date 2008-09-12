/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.rails.deploy;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.classloading.spi.metadata.ClassLoadingMetaData;
import org.jboss.classloading.spi.metadata.ExportAll;
import org.jboss.classloading.spi.vfs.metadata.VFSClassLoaderFactory;
import org.jboss.deployers.vfs.spi.deployer.AbstractVFSParsingDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.metadata.web.spec.FilterMappingMetaData;
import org.jboss.metadata.web.spec.FilterMetaData;
import org.jboss.metadata.web.spec.FiltersMetaData;
import org.jboss.metadata.web.spec.ListenerMetaData;
import org.jboss.metadata.web.spec.WebMetaData;
import org.jboss.virtual.VFS;
import org.jboss.virtual.VirtualFile;

public class RailsDeployer extends AbstractVFSParsingDeployer<WebMetaData> implements RailsDeployerMBean
{
	public RailsDeployer() {
		super( WebMetaData.class );
		addOutput( ClassLoadingMetaData.class );
		//setName( "jboss-rails" );
		setSuffix("rails");
		//setAllInputs(true);
	}
	
	/*
	@Override
	protected boolean accepts(VFSDeploymentUnit unit) throws DeploymentException {
		boolean accepted = unit.getSimpleName().endsWith( ".rails" );
		log.info( "accepts(" + unit.getSimpleName() + ") -> " + accepted + "     " + unit.getName() );
		return accepted;
	}
	*/

	@Override
	protected WebMetaData parse(VFSDeploymentUnit unit, VirtualFile file, WebMetaData root) throws Exception {
		log.info( "parse(" + unit.getSimpleName() + ", " + file + ",...)" );
		//DeploymentUnit railsApp = unit.addComponent( "rails.war" );
		WebMetaData md = new WebMetaData();
		
		initFilters(md);
		initFilterMappings(md);
		initListeners(md);
		unit.addAttachment( WebMetaData.class, md);
		
		ClassLoadingMetaData clmd = initClassLoader();
		unit.addAttachment(ClassLoadingMetaData.class, clmd);
		return md;
	}
	
	
	
	/*
	protected void initDescriptionGroupMetaData(WebMetaData md) {
		DescriptionGroupMetaData descriptionGroup = new DescriptionGroupMetaData();
		DescriptionsImpl descriptions = new DescriptionsImpl();
		DescriptionImpl description = new DescriptionImpl();
		description.setDescription( "Ruby on Rails Web App" );
		descriptions.add( description );
		descriptionGroup.setDescriptions(descriptions);
		md.setDescriptionGroup( descriptionGroup );
	}
	*/
	
	private ClassLoadingMetaData initClassLoader() {
		VFSClassLoaderFactory md = new VFSClassLoaderFactory();
		List<String> roots = new ArrayList<String>();
		
		addJRubyToClassLoader( roots );
		md.setRoots(roots);
		
	    md.setExportAll(ExportAll.NON_EMPTY);
	    md.setImportAll(true);
		
		//this.
		return md;
	}



	private void addJRubyToClassLoader(List<String> roots) {
		File libDir = new File( "/Users/bob/workspaces/jbossas/rails/target/jboss-rails-deployer.dir" );
		
		for ( File child : libDir.listFiles() ) {
			try {
				VirtualFile vfsChild = VFS.getRoot( child.toURL() );
				roots.add( vfsChild.toURL().toString() );
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}

	protected void initFilters(WebMetaData md) {
		FiltersMetaData filters = new FiltersMetaData();
		FilterMetaData filter = new FilterMetaData();
		filter.setFilterClass( "org.jruby.rack.RackFilter" );
		filter.setFilterName( "RackFilter" );
		filters.add( filter );
		md.setFilters(filters);
	}
	
	protected void initFilterMappings(WebMetaData md) {
		List<FilterMappingMetaData> filterMappings = new ArrayList<FilterMappingMetaData>();
		FilterMappingMetaData filterMapping = new FilterMappingMetaData();
		filterMapping.setFilterName("RackFilter");
		List<String> urlPatterns = new ArrayList<String>();
		urlPatterns.add( "/*" );
		filterMapping.setUrlPatterns( urlPatterns );
		md.setFilterMappings(filterMappings);
	}
	
	protected void initListeners(WebMetaData md) {
		List<ListenerMetaData> listeners = new ArrayList<ListenerMetaData>();
		ListenerMetaData listener = new ListenerMetaData();
		listener.setListenerClass("org.jruby.rack.rails.RailsServletContextListener");
		listeners.add( listener );
		md.setListeners(listeners);
	}
}