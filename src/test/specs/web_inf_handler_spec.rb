
require File.dirname( __FILE__ ) + '/spec_helper.rb'

import org.jboss.virtual.VFS
import org.jboss.rails.RailsAppContextFactory
import org.jboss.rails.RailsAppContext
import org.jboss.rails.WebInfHandler

describe WebInfHandler do

  before(:each) do 
    @context = RailsAppContextFactory.getInstance.createRoot( "ballast", File.dirname( __FILE__ ) + '/../ballast' )
    @web_inf = @context.get_web_inf
  end

  it "should serve web.xml from synthetic file" do
    web_xml = @web_inf.get_child( 'web.xml' )
    web_xml.should_not be_nil
  end

end
