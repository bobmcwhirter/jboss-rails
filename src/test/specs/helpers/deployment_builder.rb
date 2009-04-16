#import org.jboss.virtual.plugins.context.memory.MemoryContextFactory

import org.jboss.deployers.spi.structure.StructureMetaDataFactory

class DeploymentBuilder
  
  def initialize(&block)
    puts "initializing deployment with #{block}" 
    @root_url = "vfsmemory://test/"
    @root_vfs = VFS.getRoot( URL.new( @root_url ) )
    @stack = []
    @metadata_paths = []
    puts "root is #{@root_vfs}"
    instance_eval &block if block 
    apply_structure
    puts "completed root is #{@root_vfs}"
    show_root
  end
  
  def apply_structure
    @structure = StructureMetaDataFactory.createStructureMetaData()
    context = StructureMetaDataFactory.createContextInfo()
    @metadata_paths.each do |path|
      puts "metadata path [#{path}]"
      context.addMetaDataPath( path ) 
    end
    @structure.addContext( context )
  end
  
  def structure
    @structure 
  end
  
  def root_vfs
    @root_vfs
  end
  
  def show_root
    show_node( @root_vfs, '' ) 
  end
  
  def show_node(node, indent)
    puts "#{indent}#{node.getPathName()} #{node.class.java_class}"
    node.getChildren().each do |child|
      show_node( child, "  #{indent}") 
    end
  end
  
  def root(opts={}, &block)
    dir( '', opts, &block ) 
  end
  
  def dir(name, opts={}, &block)
    @stack.push name 
    if ( opts[:metadata] ) 
      @metadata_paths << current_path 
    end
    puts "#{current_path} options #{opts.inspect}"
    Java::OrgJbossVirtualPluginsContextMemory::MemoryContextFactory.getInstance().createDirectory( URL.new( current_url ) )
    instance_eval &block if block 
    @stack.pop
  end
  
  def file(name, opts={}, &block)
    @stack.push name
    bytes = nil
    if ( opts[:read] ) 
      bytes = java.lang.String.new( read_deployment_file( opts[:read] ) )
    else
      if ( block.nil? )
        raise "file contents must be defined"
      else
        bytes = java.lang.String.new( instance_eval( &block ).to_s )
      end
    end
    
    ( bytes = java.lang.String.new( "" ) ) unless bytes
      
    puts "#{current_path} options #{opts.inspect}"
    puts "contents [#{bytes}]"
    Java::OrgJbossVirtualPluginsContextMemory::MemoryContextFactory.getInstance().putFile( URL.new( current_url ), bytes.getBytes() )
    @stack.pop
  end
  
  def read_deployment_file(path)
    File.read( "#{BASE_DIR}/src/test/resources/deployments/#{path}") 
  end
  
  def current_path
    @stack.join( "/" ) 
  end
  
  def current_url
    @root_url + current_path()
  end
  
  
  
end