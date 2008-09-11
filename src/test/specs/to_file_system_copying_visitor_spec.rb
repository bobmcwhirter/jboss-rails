
require File.dirname( __FILE__ ) + '/spec_helper.rb'

require 'fileutils'

target_dir = File.dirname( __FILE__ ) + '/../../../target/tfscv.tmp/'

import org.jboss.rails.vfs.ToFileSystemCopyingVisitor

describe ToFileSystemCopyingVisitor do
  before( :each ) do 
    FileUtils.rm_rf( target_dir )
    @visitor = ToFileSystemCopyingVisitor.createVisitor( target_dir )  
  end
  
  it "should successfully create the directory if necessary" do
    File.exist?( target_dir ).should be_true
  end
  
  it "should be able to visit the entire tree" do
    @context = RailsAppContextFactory.getInstance.createRoot( "ballast", File.dirname( __FILE__ ) + '/../ballast' )
    @context.get_root.get_virtual_file.visit( @visitor )
  end

end

