package com.websecuritylab.netdoc.doclet.detectors

import org.slf4j.Logger

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
import com.sun.javadoc.AnnotationValue

import org.slf4j.LoggerFactory

class SpringServiceDetector {
	private static final Logger logger = LoggerFactory.getLogger(SpringServiceDetector.class);
	private static final String SPRING_PKG = "org.springframework.web.bind.annotation.";
	private static final String SPRING_PATH = SPRING_PKG + "RestController";
	//private static final String SPRING_MAPPING = SPRING_PKG + "RequestMapping";
	
	public enum mappingPREFIX { GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE };    // This is from org.springframework.web.bind.annotation.RequestMethod
	private static enum SPRING_MAPPING {RequestMapping, GetMapping, PostMapping, PutMapping, DeleteMapping, PatchMapping};
	
	public List<Service> findSpringServices(RootDoc root){
		// System.out.println("Looking for Spring Servies.");
		ClassDoc[] classDocs = root.classes();
		List<Service> restServices = new ArrayList<>();
		for ( ClassDoc cd : classDocs ) {
			try {
				Service rs = getRestService(cd);

				if ( rs == null )  continue;	// Ignore classes that are not RestServices

				restServices.add (rs);
			} catch (Exception e) {
				logger.error("Error in SpringServiceDetector.findSpringServices(): " + e.toString() + ": " + e.getStackTrace());
			}
		}
		return restServices;
	}

	private Service getRestService(ClassDoc cd) {
		if ( cd.annotations() == null ) return null;
		String pathAnnotation = getSpringControllerPath(cd.annotations());
		if ( pathAnnotation == null ) return null;
		
		List<UrlPattern> urlPatterns = new ArrayList<>();
		urlPatterns.add( new UrlPattern(path: pathAnnotation, config: URLPATTERN_CONFIG.Annotation));

		
		// System.out.println("Found Spring Controller: " + cd.toString());
		List<RestMethod> rsMethods= new ArrayList<>();
		for ( MethodDoc method: cd.methods() ) {
		    // System.out.println("    Looking for Methods: " + method);
			List<RestMethod> gotmethods  = getSpringControllerMethods(method.annotations());
			// System.out.println("    Looking for "+method+" Methods: " + gotmethods);
			rsMethods.addAll(gotmethods);
//			if ( rsVerb != null ) {
//				List<String> paramNames = new ArrayList<>();
//				for ( Parameter param : method.parameters()) {
//					paramNames.add(param.name());
//				}
//				RestMethod rsm = new RestMethod(verb: rsVerb,method: method.name(), params: paramNames);
//				rsMethods.add(rsm);
//			}
		}
		//return new Service(className: cd.toString(), urlPattern: pathAnnotation, restMethods : rsMethods);
		return new Service(className: Util.getClassName(cd), packageName: Util.getPackageName(cd), urlPatterns: urlPatterns, methods : rsMethods);
	}

	private String getSpringControllerPath(AnnotationDesc[] annotationDescs) {
		//// System.out.println("Looking for Spring Controller Path in annotations: " + annotationDescs);
		if ( annotationDescs == null ) return null;
		String rsPath = null;
		for ( AnnotationDesc ad : annotationDescs) {
			String typeNane = ad.annotationType().qualifiedName();
			//// System.out.println("    Checking AnnotationDesc ("+(SPRING_PATH.equals(typeNane))+"): " + typeNane);
			
			if (SPRING_PATH.equals(typeNane) ) return typeNane;
			
//			if (!SPRING_PATH.equals(typeNane) ) continue;
//			for ( ElementValuePair evp : ad.elementValues() ) {
//				rsPath = evp.value().value().toString();
//				// System.out.println("        Checking path: " + rsPath);
//			}		
			
		}
		return rsPath;
	}
	
	public List<RestMethod> getSpringControllerMethods( AnnotationDesc[] methodAnnotations ) {
		List<RestMethod> rsMethods= new ArrayList<>();
		// System.out.println("        Looking for Spring Methods in annotations: " + methodAnnotations);
		for ( SPRING_MAPPING mapping : SPRING_MAPPING.values()) {
			for ( AnnotationDesc ad : methodAnnotations ) {
				
				String fullName = ad.annotationType().qualifiedName();
			    String mappingName = fullName.substring(fullName.lastIndexOf(".")+1 );
				//// System.out.println("            Checking ("+( mapping.toString().equals( mappingName ))+")MAPPING("+mappingName+"): " + mapping.toString());
				
				if ( mapping.toString().equals( mappingName )) {
					// System.out.println("                Got "+mappingName+" Mapping: " + ad);
					String path = null;
					List<String> verbs = new ArrayList<>();
					String mappingType = mappingName.replace("Mapping","").toUpperCase();

					List<String> urlPatterns = new ArrayList<>();

					for ( ElementValuePair evp : ad.elementValues() ) {

						switch(evp.element().name().toString())
						{
							case "value":
								path = evp.value().value().toString();
								urlPatterns.add(path);
								break;
							case "method":									// Example {org.springframework.web.bind.annotation.RequestMethod.GET, org.springframework.web.bind.annotation.RequestMethod.POST}
								String methodsStr = evp.value().value().toString().replace("{","").replace("}","").replace("[","").replace("]","");  // remove brackets
								List<String> methodList = Arrays.asList(methodsStr.split(","))
								// System.out.println("!!!!!!!!!Got methodList: " + methodList);
								for ( String method : methodList) {
									verbs.add(method.substring(method.lastIndexOf(".")+1));
								}
								break;
							default:
								logger.debug("Got unknown annotation element: " + evp);
						}
					}
					
					if ( verbs.size() == 0 ) {
						//if ("REQUEST".equals(mappingType) ) verbs.addAll(Arrays.asList(mappingPREFIX.values()));
						if ("REQUEST".equals(mappingType) ) verbs.add(mappingPREFIX.values());
						else verbs.add(mappingType);
					}

					// System.out.println(" >>>>>>>>>>Ended with ("+eValue+") verbs: " + verbs);
					for ( String verb : verbs) {
						RestMethod rsm = new RestMethod(verb: verb, urlPatterns: urlPatterns, params: null);
						rsMethods.add(rsm);
					}
				}
			}
		}
		return rsMethods;
	}
}
