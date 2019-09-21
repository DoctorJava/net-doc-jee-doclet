package net.jakartaee.tools.netdoc.model
import groovy.transform.ToString

@ToString
class Report {
	Info info
	List<Servlet> servlets
	List<Service> services	
	List<WebSocket> sockets
	List<NetConnection> connections	
}

@ToString
class Info {
	String title
	String version
}

@ToString
class Servlet {
	String className		// class is a reserved work.
	String packageName		// package is a reserved work.
	boolean hasWebXml = false
	List methods
	List<UrlPattern> urlPatterns
}

enum SERVICE_TYPE{REST, SOAP}

@ToString			// This was the original working version
class Service {
	String className		// class is a reserved work.
	String packageName		// package is a reserved work.
	SERVICE_TYPE type = SERVICE_TYPE.REST
	List<UrlPattern> urlPatterns
	List<RestMethod> methods
}

enum URLPATTERN_CONFIG{WebXml, Annotation}

@ToString
class UrlPattern{	
	String path
	URLPATTERN_CONFIG config
}

enum SOCKET_TYPE{Server, Client}

@ToString
class SocketEndpoint{
	String path
	SOCKET_TYPE type
}

@ToString
class RestMethod {
	String verb
	String method
	List<String> params
}

@ToString
class WebSocket{
	String className		// class is a reserved work.
	String packageName		// package is a reserved work.
	boolean hasWebXml = false
	List<SocketEndpoint> endpoints
}

@ToString
class NetConnection {
	String className		// class is a reserved work.
	String packageName		// package is a reserved work.
	boolean hasUrlConnection = false
	boolean hasServerSockets = false
	boolean hasSockets = false
}