
require 'fileutils'

def determine_version
  glob = "#{project_root}/target/jboss-rails-*-deployer.dir"
  matches = Dir[ glob ]
  if ( matches.length != 1 )
    fail "*** run 'mvn package' first"
  end

  deployer_dir = matches.first

  version = nil
  base = File.basename( deployer_dir, ".dir" )
  if ( base =~ /^jboss-rails-(.+)-deployer$/ )
    version = $1
  end
  if ( version.nil? )
    fail "*** unable to determine version"
  end
  return version
end

def version
  $VERSION ||= determine_version
end

def project_root
  File.dirname( __FILE__ )
end

def deployer_build_dir
  "#{project_root}/target/jboss-rails-#{version}-deployer.dir"
end

def dist_stage
  "#{project_root}/target/dist-stage"
end

def deployer_dist_dir
  "#{dist_stage}/deployer"
end

def simple_deployer_dir
  "#{deployer_dist_dir}/jboss-rails.deployer"
end

task :dist do

  puts "Making distribution of #{deployer_build_dir}"

  FileUtils.rm_rf( deployer_dist_dir )
  FileUtils.mkdir_p( deployer_dist_dir )

  FileUtils.cp_r( deployer_build_dir, simple_deployer_dir )
  FileUtils.cp( "#{project_root}/INSTALL.txt", deployer_dist_dir )
  FileUtils.cp( "#{project_root}/README.markdown", deployer_dist_dir )
  Dir[ "#{project_root}/LICENSE*.txt" ].each do |l|
    FileUtils.cp( l, deployer_dist_dir )
  end

  FileUtils.chdir( "#{deployer_dist_dir}" ) do 
    puts `zip -r ../jboss-rails-deployer-#{version}.zip jboss-rails.deployer *.txt *.markdown`
  end
end

task :release_dist do
  version = determine_version

  FileUtils.chdir( "#{dist_stage}" ) do 
    puts `scp -q jboss-rails-deployer-#{version}.zip oddthesis.org:/opt/oddthesis/repo/`
  end
end
