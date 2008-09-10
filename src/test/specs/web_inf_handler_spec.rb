# JBoss, Home of Professional Open Source
# Copyright 2006, Red Hat Middleware LLC, and individual contributors
# by the @authors tag. See the copyright.txt in the distribution for a
# full listing of individual contributors.
# 
# This is free software; you can redistribute it and/or modify it
# under the terms of the GNU Lesser General Public License as
# published by the Free Software Foundation; either version 2.1 of
# the License, or (at your option) any later version.
# 
# This software is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
# Lesser General Public License for more details.
# 
# You should have received a copy of the GNU Lesser General Public
# License along with this software; if not, write to the Free
# Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
# 02110-1301 USA, or see the FSF site: http://www.fsf.org.


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
