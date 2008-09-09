
require File.dirname( __FILE__ ) + '/spec_helper.rb'

describe WarRootHandler do

  before(:each) do 
    @context = RailsAppContextFactory.getInstance.createRoot( "ballast", File.dirname( __FILE__ ) + '/../ballast' )
    @root = @context.get_war_root_handler
  end

  it "should have a rails:// URL" do
    @root.to_uri.to_string.should eql( "rails://ballast/" )
  end

  it "should be resolvable through java URL handlers" do 
    url = java.net.URL.new( "rails://ballast/" )
    url.to_s.should_not be_nil
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
