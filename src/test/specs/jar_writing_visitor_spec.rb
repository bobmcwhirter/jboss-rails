
require File.dirname( __FILE__ ) + '/spec_helper.rb'

jar_file = File.dirname( __FILE__ ) + '/../../../target/jwv.jar'

import org.jboss.rails.vfs.JarWritingVisitor

describe JarWritingVisitor do
  before( :each ) do 
    FileUtils.rm_rf( jar_file )
  end
  
  it "should be able to visit the entire tree" do
    @context = RailsAppContextFactory.getInstance.createRoot( "ballast", File.dirname( __FILE__ ) + '/../ballast' )
    JarWritingVisitor.write_jar( @context.get_root.get_virtual_file, jar_file )
  end
  
end

