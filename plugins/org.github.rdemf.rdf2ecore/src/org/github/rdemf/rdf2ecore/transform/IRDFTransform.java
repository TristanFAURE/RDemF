package org.github.rdemf.rdf2ecore.transform;

import java.util.Map;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.openrdf.model.Value;
import org.openrdf.repository.Repository;

public interface IRDFTransform {

	EClassifier transformIntoClass(Value value, Repository repository);

	EPackage transformIntoEPackage(String uri, Repository repository);

	void fillDefaultURIs(Map<String, EClassifier> classifierURIs);

}
