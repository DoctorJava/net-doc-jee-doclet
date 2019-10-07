package net.jakartaee.tools.netdoc

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.sun.javadoc.RootDoc

import net.jakartaee.tools.netdoc.detectors.ServletDetector
import net.jakartaee.tools.netdoc.detectors.WebSocketDetector
import net.jakartaee.tools.netdoc.detectors.NetConnectionDetector
import net.jakartaee.tools.netdoc.detectors.RestServiceDetector
import net.jakartaee.tools.netdoc.detectors.SpringServiceDetector
import net.jakartaee.tools.netdoc.model.*
import net.jakartaee.tools.netdoc.Util

import groovy.json.JsonOutput

class JeeScannerDoclet {
	private static final Logger log = LoggerFactory.getLogger(JeeScannerDoclet.class);
	
	public static  boolean start(RootDoc doc) {
				
		log.debug("--- Starting NetDoc JEE Doclet --- ");
		
		List<Servlet> servlets = new ServletDetector().findServlets(doc);
		log.debug("Found Servlets: " + servlets);

		List<Service> services = new RestServiceDetector().findRestServices(doc);
		log.debug("Found JEE REST Service: " + services);

		// TODO:  Trying to trap errors. But this doesn't seem to work
		//			Note: Got errors before I added to Spring-Sample gradle: 	implementation group: 'org.springframework.boot', name: 'spring-boot-loader', version: '2.1.9.RELEASE'

//		try{
			List<Service> springServices = new SpringServiceDetector().findSpringServices(doc);
			log.debug("Found Spring Service: " + springServices);
		
			services.addAll(springServices);					// JEE and Spring have different detectors, but the NetDoc output treats them as equals
//		}catch(Exception e) {
//			log.debug("Error getting Spring Service: " + e);
//			
//		}

		List<WebSocket> sockets = new WebSocketDetector().findWebSockets(doc);
		log.debug("Found Web Sockets: " + sockets);
		
		List<NetConnection> connections = new NetConnectionDetector().findNetConnections(doc);
		log.debug("Found Net Connections: " + connections);

		
		Info myInfo = new Info(title: "MyAppName", version: "0.2")
		

		Report report = new Report( info: myInfo, 
									servlets: servlets, 
									services: services, 
									sockets: sockets, 
									connections: connections );
								

		def OUT_JSON = "D:/dev/tools/NetDoc/net-doc-jee-report_"+myInfo.getTitle()+".json"
		def OUT_HTML = "D:/dev/tools/NetDoc/net-doc-jee-report_"+myInfo.getTitle()+".html"
		
		def jOut = JsonOutput.toJson(report)
		//File outJson = new File(OUT_JSON)
		//outJson.write(JsonOutput.prettyPrint(jOut))
		
		//println('Done writing JSON report.')
		System.out.println(jOut);
			
		//log.debug("Got HTML: " + Util.convertJsToHtml(Util.convertJsonToJs(jOut)) );
		//File outHtml = new File(OUT_HTML)
		//outHtml.write( Util.convertJsToHtml( Util.convertJsonToJs(jOut) ) );

		log.debug("Wrote Report: " + OUT_JSON);
		log.debug("--- Finished JeeScannerDoclet  ---");
		return true;
	}
}
