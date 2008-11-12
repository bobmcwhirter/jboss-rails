
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

def project_root
  File.dirname( __FILE__ )
end

def dist_stage
  "#{project_root}/target/dist-stage"
end

def simple_dir
  "#{dist_stage}/jboss-rails.deployer"
end

task :dist do
  version = determine_version

  deployer_dir = "#{project_root}/target/jboss-rails-#{version}-deployer.dir"

  puts "Making distribution of #{deployer_dir}"

  puts "Version #{version}"

  FileUtils.rm_rf( dist_stage )
  FileUtils.mkdir( dist_stage )

  FileUtils.cp_r( deployer_dir, simple_dir )
  FileUtils.cp( "#{project_root}/INSTALL.txt", dist_stage )
  FileUtils.cp( "#{project_root}/README.markdown", dist_stage )
  Dir[ "#{project_root}/LICENSE*.txt" ].each do |l|
    FileUtils.cp( l, dist_stage )
  end

  FileUtils.chdir( "#{dist_stage}" ) do 
    puts `zip -r jboss-rails-deployer-#{version}.zip jboss-rails.deployer *.txt *.markdown`
  end
end

task :release_dist do
  version = determine_version

  FileUtils.chdir( "#{dist_stage}" ) do 
    puts `scp jboss-rails-deployer-#{version}.zip oddthesis.org:/opt/oddthesis/repo/`
  end
end
