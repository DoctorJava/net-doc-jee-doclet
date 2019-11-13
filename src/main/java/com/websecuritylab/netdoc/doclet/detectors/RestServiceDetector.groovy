package com.websecuritylab.netdoc.doclet.detectors

import com.sun.javadoc.AnnotationDesc
import com.sun.javadoc.ClassDoc
import com.sun.javadoc.MethodDoc
import com.sun.javadoc.Parameter
import com.sun.javadoc.RootDoc
import com.sun.javadoc.AnnotationDesc.ElementValuePair
import com.websecuritylab.netdoc.doclet.Util
import com.websecuritylab.netdoc.doclet.model.RestMethod
import com.websecuritylab.netdoc.doclet.model.Service
import com.websecuritylab.netdoc.doclet.model.URLPATTERN_CONFIG
import com.websecuritylab.netdoc.doclet.model.UrlPattern

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RestServiceDetector {
	private static final Logger logger = LoggerFactory.getLogger(RestServiceDetector.class);
	private static final String JAXRS_PKG = "javax.ws.rs.";
	private static final String JAXRS_PATH = JAXRS_PKG + "Path";
	private static enum JAXRS_VERB {GET, PUT, POST, DELETE};
	
	//public Map<String, RestService> findRestServices(RootDoc root){
	public List<Service> findRestServices(RootDoc root){
		ClassDoc[] classDocs = root.classes();
		//Map<String, RestService> restServices = new HashMap<>();
		List<Service> restServices = new ArrayList<>();

		for ( ClassDoc cd : classDocs ) {

			try {
				Service rs = getRestService(cd);

				if ( rs == null )  continue;	// Ignore classes that are not RestServices

				//restServices.put (rs.className, rs);
				restServices.add (rs);
			} catch (Exception e) {
				logger.error("Error in RestServiceDetector.findRestServices(): " + e.toString() + ": " + e.getStackTrace());
			}
		}
		return restServices;
	}

	private Service getRestService(ClassDoc cd) {
		if ( cd.annotations() == null ) return null;
		String pathAnnotation = getJaxRsPath(cd.annotations());
		if ( pathAnnotation == null ) return null;
		List<UrlPattern> urlPatterns = new ArrayList<>();
		urlPatterns.add( new UrlPattern(path: pathAnnotation, config: URLPATTERN_CONFIG.Annotation));
		
		
		List<RestMethod> rsMethods= new ArrayList<>();
		
		for ( MethodDoc method: cd.methods() ) {
			String rsVerb = getJaxRsVerb(method.annotations());
			if ( rsVerb != null ) {
				
				List<String> paramNames = new ArrayList<>();
				
				for ( Parameter param : method.parameters()) {
					paramNames.add(param.name());
				}
				RestMethod rsm = new RestMethod(verb: rsVerb, urlPatterns: urlPatterns, method: method.name(), params: paramNames);
				rsMethods.add(rsm);
			}
		}
		return new Service(className: Util.getClassName(cd), packageName: Util.getPackageName(cd), urlPatterns: urlPatterns, methods : rsMethods);
	}

	private String getJaxRsPath(AnnotationDesc[] annotationDescs) {
		if ( annotationDescs == null ) return null;
		String rsPath = null;
		for ( AnnotationDesc ad : annotationDescs) {
			String typeNane = ad.annotationType().qualifiedName();
			if (!JAXRS_PATH.equals(typeNane) ) continue;
			for ( ElementValuePair evp : ad.elementValues() ) {
				rsPath = evp.value().value().toString();
			}		
		}
		return rsPath;
	}
	
	public String getJaxRsVerb( AnnotationDesc[] methodAnnotations ) {
		//log.debug("Looking for Verbs in annotations: " + methodAnnotations);
		for ( JAXRS_VERB verb : JAXRS_VERB.values()) {
			//log.debug("Looking for Verb: " + verb);
			for ( AnnotationDesc ad : methodAnnotations ) {
				String typeName = ad.annotationType().qualifiedName();
				if (typeName.equals( JAXRS_PKG + verb)) return verb;
			}
		}
		return null;
	}
}
