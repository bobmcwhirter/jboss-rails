
require File.dirname( __FILE__ ) + '/spec_helper.rb'

describe WebInfHandler do

  before(:each) do 
    @context = RailsAppContextFactory.getInstance.createRoot( "ballast", File.dirname( __FILE__ ) + '/../ballast' )
    @web_inf = @context.get_web_inf
  end

  it "should serve web.xml from synthetic file" do
    web_xml = @web_inf.get_child( 'web.xml' )
    web_xml.should_not be_nil
  end

  it "web.xml should have WEB-INF/ as its parent" do
    web_xml = @web_inf.get_child( 'web.xml' )
    web_xml.get_parent.should eql( @web_inf )
    web_xml.get_parent.get_child( 'web.xml').should eql( web_xml )
  end


  it "should serve lib/ from an assembled directory" do
    lib = @web_inf.get_child( 'lib' )
    lib.should_not be_nil
  end

  it "should set lib/'s parentage as itself" do
    lib = @web_inf.get_child( 'lib' )
    lib.should_not be_nil
    lib.get_parent.should eql( @web_inf )
  end

  it "should server non-special paths from RAILS_ROOT" do 
    config = @web_inf.get_child( 'config' )
    config.should_not be_nil

    database_yml = @web_inf.get_child( 'config/database.yml' )
    database_yml.should_not be_nil
  end

  it "should provide 'natural' navigation between rails files" do
    database_yml = @web_inf.get_child( 'config/database.yml' )
    database_yml.should_not be_nil

    config = database_yml.get_parent
    rails_png = config.get_child( "../public/images/rails.png" )
    rails_png.should_not be_nil
  end
  
  
  it "should provide a complete enumeration of its children from RAILS_ROOT, web.xml, and lib/" do
    children = @web_inf.get_children(true)
    
    names = children.collect{|e| e.get_name }
    names.should include( 'lib' )
    names.should include( 'config' )
    names.should include( 'app' )
    names.should include( 'web.xml' )
  end
  
  it "should be the parent of its children" do
    children = @web_inf.get_children(true)
    children.each do |child|
      child.get_parent.to_uri.should eql( @web_inf.to_uri )
    end
  end

end
