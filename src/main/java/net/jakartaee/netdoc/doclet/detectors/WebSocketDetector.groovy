package net.jakartaee.netdoc.doclet.detectors

import com.sun.javadoc.RootDoc

import org.slf4j.Logger

import com.sun.javadoc.AnnotationDesc
import com.sun.javadoc.ClassDoc
import com.sun.javadoc.MethodDoc
import com.sun.javadoc.Parameter
import com.sun.javadoc.AnnotationDesc.ElementValuePair

import net.jakartaee.netdoc.doclet.Util
import net.jakartaee.netdoc.doclet.model.SOCKET_TYPE
import net.jakartaee.netdoc.doclet.model.SocketEndpoint
import net.jakartaee.netdoc.doclet.model.WebSocket
import net.jakartaee.tools.netdoc.doclet.model.*

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class WebSocketDetector {
	private static final Logger logger = LoggerFactory.getLogger(WebSocketDetector.class);
	private static final String SOCKET_ANNOTAION = "ServerEndpoint";
	
	public List<WebSocket> findWebSockets(RootDoc root){
		ClassDoc[] classDocs = root.classes();
		List<WebSocket> webSockets = new ArrayList<>();
		for ( ClassDoc cd : classDocs ) {
			try {
				WebSocket ws = getWebSockets(cd);

				if ( ws == null )  continue;	// Ignore classes that are not WebSocket

				webSockets.add (ws);
			} catch (Exception e) {
				logger.error("Error in WebSocketDetector.findWebSockets(): " + e.toString() + ": " + e.getStackTrace());
			}
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
