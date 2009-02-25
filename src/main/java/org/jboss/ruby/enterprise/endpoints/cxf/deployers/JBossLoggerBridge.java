package org.jboss.ruby.enterprise.endpoints.cxf.deployers;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.apache.cxf.common.logging.AbstractDelegatingLogger;
import org.jboss.logging.Logger;

public class JBossLoggerBridge extends AbstractDelegatingLogger {

	private Logger logger;

	public JBossLoggerBridge(String name, String resourceBundleName) {
		super(name, resourceBundleName);
		this.logger = Logger.getLogger( name );
	}

	@SuppressWarnings("deprecation")
	@Override
	public Level getLevel() {
		if ( this.logger.isTraceEnabled() ) {
			return Level.FINEST;
		}
		
		if ( this.logger.isDebugEnabled() ) {
			return Level.FINE;
		}
		
		if ( this.logger.isInfoEnabled() ) {
			return Level.INFO;
		}
		
		return Level.ALL;
	}

	@Override
	protected void internalLogFormatted(String message, LogRecord logRecord) {
		Level level = logRecord.getLevel();
		
		if ( level == Level.FINEST || level == Level.FINER ) {
			logger.trace( message );
			return;
		}
		
		if ( level == Level.FINE ) {
			logger.debug( message );
			return;
		}
		
		if ( level == Level.WARNING ) {
			logger.warn( message );
			return;
		}
		
		if ( level == Level.SEVERE ) {
			logger.error( message );
			return;
		}
		
		logger.info( message );
	}

}
