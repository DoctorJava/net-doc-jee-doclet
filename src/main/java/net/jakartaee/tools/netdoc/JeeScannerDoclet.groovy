package net.jakartaee.tools.netdoc

import com.sun.javadoc.RootDoc

import net.jakartaee.tools.netdoc.detectors.ServletDetector
import net.jakartaee.tools.netdoc.detectors.WebSocketDetector
import net.jakartaee.tools.netdoc.detectors.NetConnectionDetector
import net.jakartaee.tools.netdoc.detectors.RestServiceDetector
import net.jakartaee.tools.netdoc.model.*
import net.jakartaee.tools.netdoc.Util

import groovy.json.JsonOutput

class JeeScannerDoclet {
	public static  boolean start(RootDoc doc) {
				
		System.out.println("--- Starting NetDoc JEE Doclet ---");
		
		List<Servlet> servlets = new ServletDetector().findServlets(doc);
		System.out.println("Found Servlets: " + servlets);

		List<Service> services = new RestServiceDetector().findRestServices(doc);
		System.out.println("Found REST Service: " + services);
		
		List<WebSocket> sockets = new WebSocketDetector().findWebSockets(doc);
		System.out.println("Found Web Sockets: " + sockets);
		
		List<NetConnection> connections = new NetConnectionDetector().findNetConnections(doc);
		System.out.println("Found Net Connections: " + connections);
		
		Info myInfo = new Info(title: "MyAppName", version: "0.2")
		
		Report report = new Report( info: myInfo, 
									servlets: servlets, 
									services: services, 
									sockets: sockets, 
									connections: connections );
								

		def OUT_JSON = "D:/dev/tools/NetDoc/net-doc-jee-report_"+myInfo.getTitle()+".json"
		def OUT_HTML = "D:/dev/tools/NetDoc/net-doc-jee-report_"+myInfo.getTitle()+".html"
		
		def jOut = JsonOutput.toJson(report)
		File outJson = new File(OUT_JSON)
		outJson.write(JsonOutput.prettyPrint(jOut))
		
		//System.out.println("Got HTML: " + Util.convertJsToHtml(Util.convertJsonToJs(jOut)) );
		File outHtml = new File(OUT_HTML)
		outHtml.write( Util.convertJsToHtml( Util.convertJsonToJs(jOut) ) );

		System.out.println("Wrote Report: " + OUT_JSON);
		System.out.println("--- Finished JeeScannerDoclet  ---");
		return true;
	}
}
