package org.github.rdemf.rdf2ecore;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EcoreFactory;

public class EMFUtils {
	private static final String RDEMF = "http://www.rdemf.org/";

	public static String getName (String name){
		StringBuilder builder = new StringBuilder();
		boolean upperCase = false;
		for (int i = 0 ; i < name.length() ; i++){
			char theChar = name.charAt(i);
			if (Character.isSpaceChar(theChar)){
				upperCase = true;
			}
			else {
				if (upperCase){
					theChar = Character.toUpperCase(theChar);
					upperCase = false ;
				}
				builder.append(theChar);
			}
		}
		return name.replaceAll("\\s", "");
	}
	
	public static EAnnotation getURIAnnotation (String uri){
		EAnnotation annot = getRDEMFAnnot();
		annot.getDetails().put(RDEMF + "uri", uri);
		return annot;
	}

	private static EAnnotation getRDEMFAnnot() {
		EAnnotation annot = EcoreFactory.eINSTANCE.createEAnnotation();
		annot.setSource(RDEMF);
		return annot;
	}
}
