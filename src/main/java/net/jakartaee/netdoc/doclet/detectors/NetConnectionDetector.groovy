package net.jakartaee.netdoc.doclet.detectors

import com.sun.javadoc.ClassDoc
import com.sun.javadoc.RootDoc

import net.jakartaee.netdoc.doclet.Util
import net.jakartaee.netdoc.doclet.model.NetConnection
import net.jakartaee.tools.netdoc.doclet.model.*

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class NetConnectionDetector {
	private static final Logger logger = LoggerFactory.getLogger(NetConnectionDetector.class);
	private static final String NET_IMPORT_PATH = ".net.";
	private static final String URLCONNECTION_CLASS = "URLConnection";
	private static final String SERVER_SOCKET_CLASS = "ServerSocket";
	private static final String SOCKET_CLASS = "Socket";
	
	public List<NetConnection> findNetConnections(RootDoc root){
		ClassDoc[] classDocs = root.classes();
		List<NetConnection> netConnections = new ArrayList<>();
		for ( ClassDoc cd : classDocs ) {
			try {
				NetConnection nc = getNetConnection(cd);
				
				if ( nc == null )  continue;	// Ignore classes that are not Net Connections
	
				netConnections.add (nc);
			} catch (Exception e) {
				logger.error("Error in NetConnectionDetector.findNetConnections(): " + e.toString() + ": " + e.getStackTrace());
			}
		}
		return netConnections;
	}
	
	private NetConnection getNetConnection(ClassDoc cd) {
		boolean hasUrlImport = false;
		boolean hasServerSocketImport = false;
		boolean hasSocketImport = false;
		ArrayList<String> netImports = [];

		// importedClasses is deprecated because "Import declarations are implementation details that should not be exposed here. In addition, not all imported classes are imported through single-type-import declarations."
		// Does not pick up java.net.*
		// That would require parsing the file for .* imports, which should not be used in best practices java programming.

		for ( ClassDoc anImport : Arrays.asList(cd.importedClasses())) {
			String i = anImport.toString();
			if ( i.contains(NET_IMPORT_PATH) ) {
				if ( i.contains(URLCONNECTION_CLASS)) {
					hasUrlImport = true;
					netImports.add( anImport.toString());
				}
				else if ( i.contains(SERVER_SOCKET_CLASS)) {
					hasServerSocketImport = true;
					netImports.add( anImport.toString());
				}
				else if ( i.contains(SOCKET_CLASS)) {			// Checking ServerSocket first skips this condition if ServerSocket already detected
					hasSocketImport = true;
					netImports.add( anImport.toString());
				}
			}

		}
		if ( hasUrlImport || hasServerSocketImport || hasSocketImport ) return new NetConnection(className: Util.getClassName(cd), packageName: Util.getPackageName(cd), hasUrlConnection: true, hasServerSockets: hasServerSocketImport, hasSockets: hasSocketImport);

		return null;
	}
}
