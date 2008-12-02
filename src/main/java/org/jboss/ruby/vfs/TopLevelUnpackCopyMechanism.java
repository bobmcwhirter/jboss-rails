package org.jboss.ruby.vfs;

import java.io.IOException;

import org.jboss.logging.Logger;
import org.jboss.virtual.plugins.copy.AbstractCopyMechanism;
import org.jboss.virtual.spi.VirtualFileHandler;

public class TopLevelUnpackCopyMechanism extends AbstractCopyMechanism {
	
	private static Logger log = Logger.getLogger(TopLevelUnpackCopyMechanism.class );

	@Override
	protected String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isAlreadyModified(VirtualFileHandler handler) throws IOException {
		log.info( "isAlreadyModified( " + handler + " )" );
		return handler.getParent() != null;
	}

	@Override
	protected boolean replaceOldHandler(VirtualFileHandler parent, VirtualFileHandler oldHandler, VirtualFileHandler newHandler) throws IOException {
		return true;
	}

}
