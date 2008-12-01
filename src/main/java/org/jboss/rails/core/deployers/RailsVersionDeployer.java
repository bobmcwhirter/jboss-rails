package org.jboss.rails.core.deployers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.helpers.AbstractParsingDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.rails.core.metadata.RailsApplicationMetaData;
import org.jboss.rails.core.metadata.RailsVersionMetaData;
import org.jboss.virtual.VirtualFile;

public class RailsVersionDeployer extends AbstractParsingDeployer {
	
	private static final Logger log = Logger.getLogger( RailsVersionDeployer.class );
	
	public RailsVersionDeployer() {
		setInput(RailsApplicationMetaData.class );
		setOutput(RailsVersionMetaData.class);
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		log.info( "Deploying: " + unit.getName() );
		RailsApplicationMetaData railsMetaData = unit.getAttachment( RailsApplicationMetaData.class );
		VirtualFile railsRoot = railsMetaData.getRailsRoot();
		
		log.info( "Determining version of Rails for " + railsRoot );
		
		VirtualFile railsVersionFile = null;
		
		try {
			railsVersionFile = railsRoot.getChild( "/vendor/rails/railties/lib/rails/version.rb" );
			if ( railsVersionFile.exists() ) {
				// okay
			}
		} catch (IOException e) {
			throw new DeploymentException( e );
		}
		
			
		if ( true ) {
			Pattern majorPattern = Pattern.compile( "^\\s*MAJOR\\s*=\\s*([0-9]+)\\s*$" );
			Pattern minorPattern = Pattern.compile( "^\\s*MINOR\\s*=\\s*([0-9]+)\\s*$" );
			Pattern tinyPattern  = Pattern.compile( "^\\s*TINY\\s*=\\s*([0-9]+)\\s*$" );
			
			Integer major = null;
			Integer minor = null;
			Integer tiny  = null;
			
			BufferedReader in = null;
			try {
				InputStream inStream = railsVersionFile.openStream();
				InputStreamReader inReader = new InputStreamReader( inStream );
				in = new BufferedReader( inReader );
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
			} catch (IOException e) {
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
