
require 'java'
import org.jboss.virtual.VFS
import org.jboss.rails.RailsAppContext
import java.net.URL

URL.setURLStreamHandlerFactory( org.jboss.net.protocol.URLStreamHandlerFactory.new() )

describe RailsAppContext do

  before(:each) do 
    puts RailsAppContext
    @context = RailsAppContext.new( "ballast", File.dirname( __FILE__ ) + '/../ballast' )
    @root = @context.root
    #@root = VirtualFile.new( @context.root )
  end

  it "should provide the contents of public/ at the root of the .war"  do
    puts "#####"
    puts @root.inspect
    puts "#####"
  end
  it "should provide WEB-INF/web.xml synthetically"
  it "should provide WEB-INF/** from the rails app directory"
end
