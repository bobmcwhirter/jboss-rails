
require 'java'
import org.jboss.virtual.VFS
import org.jboss.rails.RailsAppContextFactory
import org.jboss.rails.RailsAppContext
import java.net.URL

URL.setURLStreamHandlerFactory( org.jboss.net.protocol.URLStreamHandlerFactory.new() )

describe RailsAppContext do

  before(:each) do 
    @context = RailsAppContextFactory.getInstance.createRoot( "ballast", File.dirname( __FILE__ ) + '/../ballast' )
  end

  describe "when working with RAILS_ROOT-related activities" do 

    it "should not have a parent" do
      @context.get_rails_root.get_parent.should be_nil
    end

    it "should not provide access to higher parts of the filesystem" do
      lambda() {
        @context.get_rails_root.get_child( "../" )
      }.should raise_error
    end

    it "should provide access to the RAILS_ROOT of the application" do 
      rails_root = @context.get_rails_root
      [ 'public', 
        'app', 
        'lib' ].each do |name|
        f = rails_root.get_child( name )
        f.should_not be_nil
        f.name.should eql( name )
      end

      rails_root.get_child( 'no_such_directory' ).should be_nil
      rails_root.get_child( 'path/to/no_such_directory' ).should be_nil
    end

  end

  it "should provide the contents of public/ at the root of the .war"  
  it "should provide WEB-INF/web.xml synthetically"
  it "should provide WEB-INF/** from the rails app directory"
end
