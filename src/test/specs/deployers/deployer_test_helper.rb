
import org.jboss.dependency.plugins.AbstractController
import org.jboss.deployers.plugins.deployers.DeployersImpl
import org.jboss.deployers.plugins.main.MainDeployerImpl
import org.jboss.deployers.structure.spi.StructureBuilder
import org.jboss.deployers.structure.spi.helpers.AbstractStructuralDeployers
import org.jboss.deployers.structure.spi.helpers.AbstractStructureBuilder
import org.jboss.deployers.plugins.managed.DefaultManagedDeploymentCreator
import org.jboss.deployers.spi.deployer.managed.ManagedObjectCreator
import org.jboss.deployers.spi.deployer.helpers.DefaultManagedObjectCreator
import org.jboss.kernel.plugins.bootstrap.basic.BasicBootstrap
import org.jboss.deployers.vfs.plugins.client.AbstractVFSDeployment
import org.jboss.virtual.VFS
import java.net.URL

module DeployerTestHelper
  
  def setup_microcontainer
    @bootstrap = BasicBootstrap.new()
    @bootstrap.run()
    @kernel = @bootstrap.getKernel()
    @controller = @kernel.getController();

    @main_deployer = create_main_deployer();
    puts "main deployer #{@main_deployer.inspect}"
  end
  
  def deploy(path)
    vfs_file = VFS.getRoot( URL.new( "file://#{path}" ) )
    deployment = new AbstractVFSDeployment.new(vfs_file)

    @main_deployer.addDeployment( deployment )    
    @main_deployer.process()
    @main_deployer.getDeploymentState( deployment.getName() )
  end
  
  def create_main_deployer()
    main_deployer = MainDeployerImpl.new
    structure = create_structural_deployers_holder
    main_deployer.setStructuralDeployers(structure);
    deployers = create_deployers_holder
    main_deployer.setDeployers(deployers);
    mdc = create_managed_deployment_creator();
    main_deployer.setMgtDeploymentCreator(mdc);
    create_deployers.each do |deployer|
      puts "adding #{deployer}"
      deployers.addDeployer( deployer )      
    end
    main_deployer 
  end
  
  def deployer_instances
    return []    
  end
  
  def create_structural_deployers_holder
    builder = AbstractStructureBuilder.new();
    structure = AbstractStructuralDeployers.new();
    structure.setStructureBuilder(builder);
    return structure;
  end
  
  def create_deployers_holder
    moc = DefaultManagedObjectCreator.new()
    di = DeployersImpl.new(@controller);
    di.setMgtObjectCreator(moc);
    di
  end
  
  def create_managed_deployment_creator()
    DefaultManagedDeploymentCreator.new();
  end
  
end