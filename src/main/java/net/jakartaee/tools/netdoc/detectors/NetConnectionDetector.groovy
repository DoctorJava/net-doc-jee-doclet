package net.jakartaee.tools.netdoc.detectors

import com.sun.javadoc.ClassDoc
import com.sun.javadoc.RootDoc

import net.jakartaee.tools.netdoc.model.*
import net.jakartaee.tools.netdoc.Util

class NetConnectionDetector {
	private static final String NET_IMPORT_PATH = ".net.";
	private static final String URLCONNECTION_CLASS = "URLConnection";
	private static final String SERVER_SOCKET_CLASS = "ServerSocket";
	private static final String SOCKET_CLASS = "Socket";
	
	public List<NetConnection> findNetConnections(RootDoc root){
		ClassDoc[] classDocs = root.classes();
		List<NetConnection> netConnections = new ArrayList<>();
		for ( ClassDoc cd : classDocs ) {
			NetConnection nc = getNetConnection(cd);
			
			if ( nc == null )  continue;	// Ignore classes that are not Net Connections

			netConnections.add (nc);
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
			//System.out.println("Got Import: " + i);
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
