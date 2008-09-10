
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

  it "should delegate for WEB-INF requests" do
    web_inf = @root.get_child( 'WEB-INF' )
    web_inf.should_not be_nil
    web_xml = web_inf.get_child( 'web.xml' )
    web_xml.should_not be_nil
  end

  it "should set the parentage of WEB-INF as itself" do
    web_inf = @root.get_child( 'WEB-INF' )
    web_inf.should_not be_nil
    web_inf.get_parent.should eql( @root )
  end


  it "should serve from RAILS_ROOT/public for root-level non-WEB-INF requests" do
    index_html = @root.get_child( 'index.html' )
    index_html.should_not be_nil
  end

  it "should serve root-level requests with appropriate parentage" do
    index_html = @root.get_child( 'index.html' )
    index_html.should_not be_nil
    index_html.get_parent.should eql( @root )
  end

  it "should serve from RAILS_ROOT/public/** for nested non-WEB-INF requests" do
    application_js = @root.get_child( 'javascripts/application.js' )
    application_js.should_not be_nil
  end

  it "should serve nested public requests with appropriate parentage" do
    application_js = @root.get_child( 'javascripts/application.js' )
    application_js.should_not be_nil

    javascripts = @root.get_child( 'javascripts' )
    javascripts.should_not be_nil
    application_js.get_parent.to_uri.should eql( javascripts.to_uri )

    javascripts.get_parent.should eql( @root )

  end

  it "should return nil for non-existant root-lavel non-WEB-INF requests" do
    nonesuch = @root.get_child( 'nonesuch' )
    nonesuch.should be_nil
  end

  it "should return nil for non-existant nested root-lavel non-WEB-INF requests" do
    nonesuch = @root.get_child( 'nonesuch/nope' )
    nonesuch.should be_nil
  end
  
  it "should provide a complete enumeration of its children from public/ and the WEB-INF/ directory" do
    children = @root.get_children(true)
    
    names = children.collect{|e| e.get_name }
    names.should include( 'WEB-INF' )
    names.should include( 'images' )
    names.should include( 'javascripts' )
    names.should include( 'index.html' )
    names.should include( '404.html' )
  end
  
  it "should be the parent of its children" do
    children = @root.get_children(true)
    children.each do |child|
      child.get_parent.should eql( @root )
    end
  end

end
