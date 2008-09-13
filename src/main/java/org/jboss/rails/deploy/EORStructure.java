package org.jboss.rails.deploy;

import java.io.IOException;
import java.util.List;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.structure.ContextInfo;
import org.jboss.deployers.vfs.plugins.structure.AbstractVFSStructureDeployer;
import org.jboss.deployers.vfs.spi.structure.StructureContext;
import org.jboss.virtual.VirtualFile;
import org.jboss.virtual.VirtualFileFilter;
import org.jboss.virtual.VisitorAttributes;
import org.jboss.virtual.plugins.vfs.helpers.SuffixMatchFilter;

public class EORStructure extends AbstractVFSStructureDeployer {

	/** The default filter which allows jars/jar directories */
	public static final VirtualFileFilter DEFAULT_WEB_INF_LIB_FILTER = new SuffixMatchFilter(".jar", VisitorAttributes.DEFAULT);

	/** The web-inf/lib filter */
	private VirtualFileFilter webInfLibFilter = DEFAULT_WEB_INF_LIB_FILTER;

	public EORStructure() {
		setRelativeOrder(900);
	}

	public boolean determineStructure(StructureContext structureContext) throws DeploymentException {
		try {
			dump( structureContext.getFile(), "" );
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		VirtualFile file = structureContext.getFile();
		try {
			if (file.getChild("META-INF/jboss-rails.yml") == null) {
				return false;
			}
		} catch (IOException e) {
			throw new DeploymentException(e);
		}

		log.info("found jboss-rails.yml");
		ContextInfo context = createContext(structureContext, new String[] { "META-INF" });
		try {
			addClassPath(structureContext, file, false, true, context);
			VirtualFile webinfLib = file.getChild("WEB-INF/lib");
			if (webinfLib != null) {
				List<VirtualFile> archives = webinfLib.getChildren(webInfLibFilter);
				for (VirtualFile jar : archives)
					addClassPath(structureContext, jar, true, true, context);
			}
		} catch (IOException e) {
			throw new DeploymentException(e);
		}

		return true;
	}

	private void dump(VirtualFile root, String prefix) throws IOException {
		System.err.println( prefix + root.getName() );
		for ( VirtualFile child : root.getChildren() ) {
			dump( child, "  " + prefix );
		}
	}
}
