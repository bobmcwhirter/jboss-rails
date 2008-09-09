
require 'java'
import java.net.URL
import java.lang.System

import org.jboss.virtual.VFS
import org.jboss.rails.RailsAppContextFactory
import org.jboss.rails.RailsAppContext
import org.jboss.rails.WarRootHandler
import org.jboss.rails.WebInfHandler


begin
  pkgs = System.get_property( 'java.protocol.handler.pkgs' )
  if ( pkgs.nil? )
    pkgs = "org.jboss.rails.protocol"
  else
    unless ( pkgs =~ /org\.jboss\.rails\.protocol/ )
      pkgs = "#{pkgs}|org.jboss.rails.protocol"
    end
  end
  pkgs = System.set_property( 'java.protocol.handler.pkgs', pkgs )
  URL.setURLStreamHandlerFactory( org.jboss.net.protocol.URLStreamHandlerFactory.new() )
rescue Error=>e
end
