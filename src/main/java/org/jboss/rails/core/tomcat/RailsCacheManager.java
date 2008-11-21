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

package org.jboss.rails.core.tomcat;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.core.ContainerBase;
import org.jboss.web.tomcat.service.session.JBossCacheManager;
import org.jboss.web.tomcat.service.session.distributedcache.spi.ClusteringNotSupportedException;

/** Workaround for bogus MBean naming in JBossCacheManager with root apps.
 * 
 * @author Bob McWhirter
 */
public class RailsCacheManager extends JBossCacheManager {

	public RailsCacheManager() throws ClusteringNotSupportedException {
		super();
	}

	@Override
	protected void registerManagerMBean() {
		try {
			MBeanServer server = getMBeanServer();

			String domain;
			if (container_ instanceof ContainerBase) {
				domain = ((ContainerBase) container_).getDomain();
			} else {
				domain = server.getDefaultDomain();
			}
			Context context = (Context)container_;
			
			String contextPath = context.getPath();
			if ( contextPath == null || contextPath.equals( "" ) ) {
				contextPath = "/";
			}

			String hostName = ((Host) container_.getParent()).getName();
			hostName = (hostName == null) ? "localhost" : hostName;
			ObjectName clusterName = new ObjectName(domain + ":type=Manager,host=" + hostName + ",path=" + contextPath );

			if (server.isRegistered(clusterName)) {
				log_.warn("MBean " + clusterName + " already registered");
				return;
			}

			objectName_ = clusterName;
			server.registerMBean(this, clusterName);

		} catch (Exception ex) {
			log_.error("Could not register " + getClass().getSimpleName() + " to MBeanServer", ex);
		}
	}

}
