package org.github.rdemf.rdf2ecore;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.openrdf.repository.Repository;

public interface IRDF2Ecore {

	void transform(Repository repository, ResourceSet resourceSetImpl);

	ResourceSet transform(Repository repository);

	EPackage transform(Repository repository, String uri);

}
