package net.jakartaee.tools.netdoc;

import com.sun.javadoc.ClassDoc;

public class Util {

	public static String getClassName(ClassDoc cd) {
		return cd.toString().substring( cd.toString().lastIndexOf(".")+1);
	}
	
	public static String getPackageName(ClassDoc cd) {
		return cd.toString().substring(0, cd.toString().lastIndexOf("."));
	}
	
//	public static String convertJsonToJs(String json) {
//		String bracketStr = json.replace("\"connections\":", " const connections = ")
//								.replace("\"servlets\":", " const servlets = ")
//							    .replace("\"services\":", " const services = ")
//							    .replace("\"sockets\":", " const sockets = ")
//							    .replace("\"info\":", " const info = ")
//							    .replace(", const", "; const");								// Replace commas with semi-colon between the objects.  Semicolon necessary if no line breaks
//		return bracketStr.substring(1, bracketStr.length() - 1);							// Need to remove the first and last brackets {}
//		
//	}
//	
//	public static String convertJsToHtml(String js) {
//		final String before = "<!DOCTYPE html><html><title>Net Doc</title> <script type=\"text/javascript\" src=\"js/templates/servlet.js\"></script> <script type=\"text/javascript\" src=\"js/templates/service.js\"></script> <script type=\"text/javascript\" src=\"js/templates/connection.js\"></script> <script type=\"text/javascript\" src=\"js/templates/socket.js\"></script> <link rel=\"stylesheet\" href=\"netdoc.css\">";
//		final String after = "<body><h1><center>Net Doc Report</center></h1><div id=\"app\"></div> <script>document.getElementById(\"app\").innerHTML=`<h1>Servlets</h1><ul>${servlets.map(servletTemplate).join(\"\")}</ul><h1>Web Services</h1><ul>${services.map(serviceTemplate).join(\"\")}</ul><h1>Net Connections</h1><ul>${connections.map(connectionTemplate).join(\"\")}</ul><h1>Web Sockets</h1><ul>${sockets.map(socketTemplate).join(\"\")}</ul>`;</script> Done</body>";
//		return before + "<script>" + js + "</script>" + after;
//	}
}
