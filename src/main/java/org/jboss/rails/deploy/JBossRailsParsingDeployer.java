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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jboss.classloading.spi.metadata.ClassLoadingMetaData;
import org.jboss.classloading.spi.metadata.ExportAll;
import org.jboss.classloading.spi.vfs.metadata.VFSClassLoaderFactory;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.vfs.spi.deployer.AbstractVFSParsingDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.metadata.javaee.spec.ParamValueMetaData;
import org.jboss.metadata.web.jboss.JBossServletMetaData;
import org.jboss.metadata.web.jboss.JBossServletsMetaData;
import org.jboss.metadata.web.jboss.JBossWebMetaData;
import org.jboss.metadata.web.spec.FilterMappingMetaData;
import org.jboss.metadata.web.spec.FilterMetaData;
import org.jboss.metadata.web.spec.FiltersMetaData;
import org.jboss.metadata.web.spec.ListenerMetaData;
import org.jboss.metadata.web.spec.ServletMappingMetaData;
import org.jboss.metadata.web.spec.WebMetaData;
import org.jboss.metadata.web.spec.WelcomeFileListMetaData;
import org.jboss.rails.vfs.RailsAppContextFactory;
import org.jboss.rails.vfs.TreeCopyingVisitor;
import org.jboss.system.server.ServerConfig;
import org.jboss.system.server.ServerConfigLocator;
import org.jboss.virtual.VFS;
import org.jboss.virtual.VirtualFile;

public class JBossRailsParsingDeployer extends AbstractVFSParsingDeployer<JBossWebMetaData> {
	static {
		RailsAppContextFactory.initializeRailsUrlHandling();
	}
	public JBossRailsParsingDeployer() {
		super(JBossWebMetaData.class);
		addOutput(ClassLoadingMetaData.class);
		setName("jboss-rails.yml");
		setTopLevelOnly(false);
	}

	@Override
	protected JBossWebMetaData parse(VFSDeploymentUnit unit, VirtualFile file, JBossWebMetaData root) throws Exception {
		log.info("Parsing " + file + " for " + unit.getRoot());
		VFSDeploymentUnit vfsUnit = (VFSDeploymentUnit) unit;
		JBossWebMetaData jbossWebMetaData = setUpJBossWebMetaData(vfsUnit);
		//setUpWebMetaData(vfsUnit, jbossWebMetaData);
		//ClassLoadingMetaData classLoadingMetaData = setUpClassLoadingMetaData(vfsUnit, vfsUnit.getRoot());
		expandWar( unit );
		return jbossWebMetaData;
	}

	private void expandWar(VFSDeploymentUnit unit) throws IOException, DeploymentException {
        ServerConfig config = ServerConfigLocator.locate();

        log.info( "simple name--->" + unit.getSimpleName() );
        File expWarFile = File.createTempFile("rails", "-exp.eor", config.getServerTempDeployDir());
        expWarFile.delete();
        if (expWarFile.mkdir() == false) {
           throw new DeploymentException("Was unable to mkdir: " + expWarFile);
        }
        log.debug("Unpacking war to: " + expWarFile);
        
        //JarWritingVisitor visitor = JarWritingVisitor.createVisitor( expWarFile );
        
        //unit.getRoot().visit( visitor );
        //visitor.close();
        //JarWritingVisitor.writeJar(unit.getRoot(), expWarFile);
        TreeCopyingVisitor.writeTree( unit.getRoot(), expWarFile );
       
        
		URL expandedUrl = expWarFile.toURL();
		unit.addAttachment("org.jboss.web.expandedWarURL", expandedUrl, URL.class);
		
	}

	private void setUpWebMetaData(VFSDeploymentUnit unit, JBossWebMetaData md) {

		initContextParams(md);
		initFilters(md);
		initFilterMappings(md);
		initListeners(md);
		initWelcomeFiles(md);
		initServlets(md);
		initServletMappings(md);
		//unit.addAttachment(WebMetaData.class, md);
	}

	private void initServletMappings(JBossWebMetaData md) {
		List<ServletMappingMetaData> servletMappings = new ArrayList<ServletMappingMetaData>();
		ServletMappingMetaData servletMapping = new ServletMappingMetaData();
		servletMapping.setServletName( "default-rails" );
		List<String> urlPatterns = new ArrayList<String>();
		urlPatterns.add( "/*" );
		servletMapping.setUrlPatterns( urlPatterns );
		servletMappings.add( servletMapping );
		md.setServletMappings(servletMappings);
		
	}

	private void initServlets(JBossWebMetaData md) {
		JBossServletsMetaData servlets = new JBossServletsMetaData();
		JBossServletMetaData servlet = new JBossServletMetaData(); 
		servlet.setServletName( "default-rails" );
		servlet.setServletClass( "org.jboss.rails.rack.JBossDefaultServlet");
		servlets.add( servlet );
		md.setServlets( servlets );
	}
	

	private void initContextParams(JBossWebMetaData md) {
		List<ParamValueMetaData> params = new ArrayList<ParamValueMetaData>();
		ParamValueMetaData railsEnv = new ParamValueMetaData();
		railsEnv.setParamName( "rails.env" );
		railsEnv.setParamValue( "development" );
		params.add( railsEnv );
		ParamValueMetaData publicRoot = new ParamValueMetaData();
		publicRoot.setParamName( "public.root" );
		publicRoot.setParamValue( "/" );
		params.add( publicRoot );
		md.setContextParams( params );
	}

	private JBossWebMetaData setUpJBossWebMetaData(VFSDeploymentUnit unit) {
		JBossWebMetaData md = new JBossWebMetaData();
		//JBossWebMetaData md = unit.getAttachment( JBossWebMetaData.class );
		
		
		setUpWebMetaData(unit, md);
		md.setServletVersion("2.4");
		
		unit.addAttachment(JBossWebMetaData.class, md);
		log.info( "JBW: " + md );
		
		log.info( "filters: " + md.getFilters() );

		return md;
	}

	private ClassLoadingMetaData setUpClassLoadingMetaData(VFSDeploymentUnit unit, VirtualFile eorRoot) throws MalformedURLException, URISyntaxException, IOException {
		
		ClassLoadingMetaData m = null;
		VFSClassLoaderFactory md = new VFSClassLoaderFactory();
		List<String> roots = new ArrayList<String>();
		roots.add(eorRoot.getChild("WEB-INF/lib").toURL().toString());

		addJRubyToClassLoader(roots);

		md.setRoots(roots);

		md.setExportAll(ExportAll.NON_EMPTY);
		md.setImportAll(true);
		

		log.info( "rails classloading roots: " + roots );

		return md;
	}

	private void addJRubyToClassLoader(List<String> roots) {
		File libDir = new File("/Users/bob/workspaces/jbossas/rails/target/jboss-rails-deployer.dir");

		for (File child : libDir.listFiles()) {
			try {
				VirtualFile vfsChild = VFS.getRoot(child.toURL());
				if (!vfsChild.getName().startsWith("jboss")) {
					roots.add(vfsChild.toURL().toString());
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}

	protected void initFilters(JBossWebMetaData md) {
		FilterMetaData filter = new FilterMetaData();
		filter.setFilterClass("org.jboss.rails.rack.JBossRackFilter" );
		filter.setFilterName("RackFilter");
		
		FiltersMetaData filters = new FiltersMetaData();
		filters.add(filter);
		md.setFilters(filters);
	}

	protected void initFilterMappings(JBossWebMetaData md) {
		FilterMappingMetaData filterMapping = new FilterMappingMetaData();
		List<String> urlPatterns = new ArrayList<String>();
		urlPatterns.add("/*");
		filterMapping.setUrlPatterns(urlPatterns);
		filterMapping.setFilterName("RackFilter");
		
		List<FilterMappingMetaData> filterMappings = new ArrayList<FilterMappingMetaData>();
		filterMappings.add( filterMapping );
		md.setFilterMappings(filterMappings);
	}

	protected void initListeners(JBossWebMetaData md) {
		List<ListenerMetaData> listeners = new ArrayList<ListenerMetaData>();
		ListenerMetaData listener = new ListenerMetaData();
		listener.setListenerClass("org.jruby.rack.rails.RailsServletContextListener");
		listeners.add(listener);
		md.setListeners(listeners);
	}

	private void initWelcomeFiles(JBossWebMetaData md) {
		WelcomeFileListMetaData welcomeFileList = new WelcomeFileListMetaData();
		List<String> welcomeFiles = new ArrayList<String>();
		welcomeFiles.add("index.html");
		welcomeFileList.setWelcomeFiles(welcomeFiles);
		md.setWelcomeFileList(welcomeFileList);
	}

}