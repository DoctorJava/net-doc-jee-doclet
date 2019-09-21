package net.jakartaee.tools.netdoc.detectors

import com.sun.javadoc.AnnotationDesc
import com.sun.javadoc.ClassDoc
import com.sun.javadoc.MethodDoc
import com.sun.javadoc.Parameter
import com.sun.javadoc.RootDoc
import com.sun.javadoc.AnnotationDesc.ElementValuePair

import net.jakartaee.tools.netdoc.model.*
import net.jakartaee.tools.netdoc.Util

class ServletDetector {
	private static final String SERVLET_CLASS = "javax.servlet.GenericServlet";
	
	private static final String space = "           ";
	
	public List<Servlet> findServlets(RootDoc root){
		ClassDoc[] classDocs = root.classes();
		List<Servlet> servlets = new ArrayList<>();
		for ( ClassDoc cd : classDocs ) {
			Servlet s = getServlet(cd);
			
			if ( s == null) continue;	// Ignore classes that are not Servlets

			println("Got servlet: "+ s)
			servlets.add (s);			
		}
		return servlets;
	}

	private Servlet getServlet(ClassDoc cd) {
		ClassDoc cdServlet = cd.findClass(SERVLET_CLASS);
		
		println("Got Servlet interfaces: " + cdServlet.getMetaPropertyValues());

		if ( cd == null || cdServlet == null || !cd.subclassOf(cdServlet) ) return; 	// Ignore classes that are not subclasses of javax.servlet.GenericServlet
		
		System.out.println("Printing Servlet methods for CD: " + cd.methods(false));
		printMethods(cd);
		
		List<UrlPattern> urlPatterns = new ArrayList<>();
		String path = getAnnotations(cd);
		if ( path ) urlPatterns.add( new UrlPattern(path: path, config: URLPATTERN_CONFIG.Annotation));
		Servlet servlet = new Servlet(className: Util.getClassName(cd), packageName: Util.getPackageName(cd), urlPatterns: urlPatterns, methods: getMethodNames(cd));
		return servlet;
	}
	
		
	private void printMethods(ClassDoc cd) {
		System.out.print(space + "Methods --> " );
		for ( MethodDoc aMethod: Arrays.asList(cd.methods(false)) ) {
			System.out.print(", " + aMethod.name() );
		}
		System.out.println();
	}
	
	private List<String> getMethodNames(ClassDoc cd) {
		List<String> methods = new ArrayList<>();
		for ( MethodDoc aMethod: Arrays.asList(cd.methods(false)) ) {
			if ( "doGet".equals(aMethod.name() ) ) methods.add("GET");
			else if ( "doPost".equals(aMethod.name() ) ) methods.add("POST");
		}
		return methods;
	}
	
	private String getAnnotations(ClassDoc cd) {
		String returnStr;
		for ( AnnotationDesc ad : Arrays.asList(cd.annotations()) ) {
			for (ElementValuePair p : ad.elementValues()) {
				System.out.println("Checking AnnotationDesc name: " + p.element().name() + " value: " + p.value().value());
				if ( "urlPatterns".equals(p.element().name()) || "value".equals(p.element().name()) ) {
					String values =  p.value().value().toString();
					System.out.println("Got AnnotationDesc name: " + p.element().name() + " value: " + values);
					//annotations.add(p.value().value());
					//returnStr = values.replace("\"", "'");	
					returnStr = values;	
				}
			}
		}
		return returnStr;
	}
		

}
