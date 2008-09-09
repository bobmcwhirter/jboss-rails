
require File.dirname( __FILE__ ) + '/spec_helper.rb'

import org.jboss.virtual.VFS
import org.jboss.rails.RailsAppContextFactory
import org.jboss.rails.RailsAppContext
import org.jboss.rails.WarRootHandler

describe WarRootHandler do

  before(:each) do 
    @context = RailsAppContextFactory.getInstance.createRoot( "ballast", File.dirname( __FILE__ ) + '/../ballast' )
    @root = @context.get_war_root_handler
  end

  it "should serve from RAILS_ROOT/public for root-level non-WEB-INF requests" do
    index_html = @root.get_child( 'index.html' )
    index_html.should_not be_nil
  end

  it "should serve from RAILS_ROOT/public/** for nested non-WEB-INF requests" do
    application_js = @root.get_child( 'javascripts/application.js' )
    application_js.should_not be_nil
  end

  it "should return nil for non-existant root-lavel non-WEB-INF requests" do
    nonesuch = @root.get_child( 'nonesuch' )
    nonesuch.should be_nil
  end

  it "should return nil for non-existant nested root-lavel non-WEB-INF requests" do
    nonesuch = @root.get_child( 'nonesuch/nope' )
    nonesuch.should be_nil
  end

end
