
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
