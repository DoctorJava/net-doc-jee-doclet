package com.websecuritylab.netdoc.doclet

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.sun.javadoc.RootDoc
import com.websecuritylab.netdoc.doclet.detectors.NetConnectionDetector
import com.websecuritylab.netdoc.doclet.detectors.RestServiceDetector
import com.websecuritylab.netdoc.doclet.detectors.ServletDetector
import com.websecuritylab.netdoc.doclet.detectors.SpringServiceDetector
import com.websecuritylab.netdoc.doclet.detectors.WebSocketDetector
import com.websecuritylab.netdoc.doclet.model.Info
import com.websecuritylab.netdoc.doclet.model.NetConnection
import com.websecuritylab.netdoc.doclet.model.Report
import com.websecuritylab.netdoc.doclet.model.Service
import com.websecuritylab.netdoc.doclet.model.Servlet
import com.websecuritylab.netdoc.doclet.model.WebSocket

import groovy.json.JsonOutput

class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	
	public static  boolean start(RootDoc doc) {
				
		logger.debug("--- Starting NetDoc JEE Doclet --- ");
		
		List<Servlet> servlets = new ServletDetector().findServlets(doc);
		logger.debug("Found Servlets: " + servlets);

		List<Service> services = new RestServiceDetector().findRestServices(doc);
		logger.debug("Found JEE REST Service: " + services);

		// TODO:  Trying to trap errors. But this doesn't seem to work
		//			Note: Got errors before I added to Spring-Sample gradle: 	implementation group: 'org.springframework.boot', name: 'spring-boot-loader', version: '2.1.9.RELEASE'

//		try{
			List<Service> springServices = new SpringServiceDetector().findSpringServices(doc);
			logger.debug("Found Spring Service: " + springServices);
		
			services.addAll(springServices);					// JEE and Spring have different detectors, but the NetDoc output treats them as equals
//		}catch(Exception e) {
//			log.debug("Error getting Spring Service: " + e);
//			
//		}

		List<WebSocket> sockets = new WebSocketDetector().findWebSockets(doc);
		logger.debug("Found Web Sockets: " + sockets);
		
		List<NetConnection> connections = new NetConnectionDetector().findNetConnections(doc);
		logger.debug("Found Net Connections: " + connections);

		
		Info myInfo = new Info(title: "MyAppName", version: "0.2")
		

		Report report = new Report( info: myInfo, 
									servlets: servlets, 
									services: services, 
									sockets: sockets, 
									connections: connections );
								

		//def OUT_JSON = "D:/dev/tools/NetDoc/net-doc-jee-report_"+myInfo.getTitle()+".json"
		//def OUT_HTML = "D:/dev/tools/NetDoc/net-doc-jee-report_"+myInfo.getTitle()+".html"
		
		def jOut = JsonOutput.toJson(report)
		//File outJson = new File(OUT_JSON)
		//outJson.write(JsonOutput.prettyPrint(jOut))
		
		//println('Done writing JSON report.')
		System.out.println(jOut);
			
		//log.debug("Got HTML: " + Util.convertJsToHtml(Util.convertJsonToJs(jOut)) );
		//File outHtml = new File(OUT_HTML)
		//outHtml.write( Util.convertJsToHtml( Util.convertJsonToJs(jOut) ) );

		//logger.debug("Wrote Report: " + OUT_JSON);
		logger.debug("--- Finished JeeScannerDoclet  ---");
		return true;
	}
}
