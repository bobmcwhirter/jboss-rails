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
        'config', 
        'lib' ].each do |name|
        f = rails_root.get_child( name )
        f.should_not be_nil
        f.name.should eql( name )
      end

      rails_root.get_child( 'no_such_directory' ).should be_nil
      rails_root.get_child( 'path/to/no_such_directory' ).should be_nil
    end

    it "should provide navigation between peers" do
      rails_root = @context.get_rails_root

      app_dir = rails_root.get_child( "app" )
      app_dir.should_not be_nil

      lib_dir = rails_root.get_child( "lib" )
      lib_dir.should_not be_nil
 
      app_dir.get_child( "../lib" ).should eql( lib_dir )

      database_yml = app_dir.get_child( "../config/database.yml" )
      database_yml.should_not be_nil
      database_yml.get_path_name.should eql( 'config/database.yml' )
    end

  end

end
