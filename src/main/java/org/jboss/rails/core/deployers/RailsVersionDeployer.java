package org.jboss.rails.core.deployers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.helpers.AbstractParsingDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.rails.core.metadata.RailsMetaData;
import org.jboss.rails.core.metadata.RailsVersionMetaData;

public class RailsVersionDeployer extends AbstractParsingDeployer {
	
	private static final Logger log = Logger.getLogger( RailsVersionDeployer.class );
	
	public RailsVersionDeployer() {
		setInput(RailsMetaData.class);
		setOutput(RailsVersionMetaData.class);
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		RailsMetaData railsMetaData = unit.getAttachment( RailsMetaData.class );
		String railsRoot = railsMetaData.getRailsRoot();
		
		String railsVersionPath = railsRoot + "/vendor/rails/railties/lib/rails/version.rb";
		File railsVersionFile = new File( railsVersionPath );
		
		if ( railsVersionFile.exists() ) {
			
			Pattern majorPattern = Pattern.compile( "^\\s*MAJOR\\s*=\\s*([0-9]+)\\s*$" );
			Pattern minorPattern = Pattern.compile( "^\\s*MINOR\\s*=\\s*([0-9]+)\\s*$" );
			Pattern tinyPattern  = Pattern.compile( "^\\s*TINY\\s*=\\s*([0-9]+)\\s*$" );
			
			Integer major = null;
			Integer minor = null;
			Integer tiny  = null;
			
			BufferedReader in = null;
			try {
				in = new BufferedReader( new FileReader( railsVersionFile ) );
				String line = null;
				
				try {
					while ( ( line = in.readLine() ) != null ) {
						if ( major == null ) {
							Matcher matcher = majorPattern.matcher( line );
							if ( matcher.matches() ) {
								String value = matcher.group(1).trim();
								major = new Integer( value );
							}
						} else if ( minor == null ) {
							Matcher matcher = minorPattern.matcher( line );
							if ( matcher.matches() ) {
								String value = matcher.group(1).trim();
								minor = new Integer( value );
							}
						} else if ( tiny == null ) {
							Matcher matcher = tinyPattern.matcher( line );
							if ( matcher.matches() ) {
								String value = matcher.group(1).trim();
								tiny = new Integer( value );
							}
						}
					}
				} catch (IOException e) {
					throw new DeploymentException( e );
				}
			} catch (FileNotFoundException e) {
				throw new DeploymentException( e );
			} finally {
				if ( in != null ) {
					try {
						in.close();
					} catch (IOException e) {
						throw new DeploymentException( e );
					}
				}
			}
			RailsVersionMetaData railsVersionMetaData = new RailsVersionMetaData( major.intValue(), minor.intValue(), tiny.intValue() );
			unit.addAttachment( RailsVersionMetaData.class, railsVersionMetaData );
			log.info( "deploying version " + railsVersionMetaData );
		}
		
	}

}
