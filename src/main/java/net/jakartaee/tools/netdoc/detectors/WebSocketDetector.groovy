package net.jakartaee.tools.netdoc.detectors

import com.sun.javadoc.RootDoc
import com.sun.javadoc.AnnotationDesc
import com.sun.javadoc.ClassDoc
import com.sun.javadoc.MethodDoc
import com.sun.javadoc.Parameter
import com.sun.javadoc.AnnotationDesc.ElementValuePair

import net.jakartaee.tools.netdoc.model.*
import net.jakartaee.tools.netdoc.Util

class WebSocketDetector {
	private static final String SOCKET_ANNOTAION = "ServerEndpoint";
	
	public List<WebSocket> findWebSockets(RootDoc root){
		ClassDoc[] classDocs = root.classes();
		List<WebSocket> webSockets = new ArrayList<>();
		for ( ClassDoc cd : classDocs ) {
			WebSocket ws = getWebSockets(cd);
			
			if ( ws == null )  continue;	// Ignore classes that are not WebSocket
			
			webSockets.add (ws);			
		}
		return webSockets;
	}
	
	private WebSocket getWebSockets(ClassDoc cd) {
		if ( !isWebSocket(cd) ) return null;
		
		//List<String> annotationList = getAnnotations(cd);
		//boolean hasAnnotations = ( annotationList ? !annotationList.empty : false );

		List<SocketEndpoint> endpoints = new ArrayList<>();
		String path = getAnnotations(cd);
		if ( path ) endpoints.add( new SocketEndpoint(path: path, type: SOCKET_TYPE.Server));

		
		WebSocket webSocket = new WebSocket(className: Util.getClassName(cd), packageName: Util.getPackageName(cd), endpoints: endpoints );
		
		return webSocket;
	}
	
	private boolean isWebSocket(ClassDoc cd) {
		boolean hasWebSocket = false;
		for ( String annot : getAnnotations(cd)) {
			if ( annot.contains(SOCKET_ANNOTAION)) hasWebSocket = true;
		}
		return hasWebSocket;
	}
	
	private List<String> getAnnotations(ClassDoc cd) {
		List<String> annotations = new ArrayList<>();
		for ( AnnotationDesc ad : Arrays.asList(cd.annotations()) ) {
			annotations.add(ad.toString());
		}
		return annotations
	}
}
