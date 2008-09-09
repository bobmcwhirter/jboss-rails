
require 'java'
import java.net.URL

begin
  URL.setURLStreamHandlerFactory( org.jboss.net.protocol.URLStreamHandlerFactory.new() )
rescue Error=>e
end
