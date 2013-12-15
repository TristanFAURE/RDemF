package org.github.rdemf.rdf2ecore.transform.impl;

import java.util.Map;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.github.rdemf.rdf2ecore.EMFUtils;
import org.github.rdemf.rdf2ecore.RDFUtils;
import org.github.rdemf.rdf2ecore.transform.IRDFTransform;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;

public class RDFTransform implements IRDFTransform {

	@Override
	public EClassifier transformIntoClass(Value value, Repository repository) {
		if (value instanceof Resource) {
			Resource res = (Resource) value;
			EClassifier classifier = EcoreFactory.eINSTANCE.createEClass();
			// find label
			TupleQueryResult result4label = RDFUtils.executeQuery(repository,
					String.format(
							"SELECT ?label WHERE { <%s> rdfs:label ?label } ",
							res.stringValue()));
			try {
				if (result4label.hasNext()) {
					classifier.setName(EMFUtils.getName(result4label.next().getBinding("label")
							.getValue().stringValue()));
				}
			} catch (QueryEvaluationException e) {
				e.printStackTrace();
			}
			classifier.getEAnnotations().add(EMFUtils.getURIAnnotation(res.stringValue()));
			return classifier;
		}
		return null;
	}

	@Override
	public EPackage transformIntoEPackage(String uri, Repository repository) {
		EPackage result = EcoreFactory.eINSTANCE.createEPackage();
		result.setNsURI(uri);
		result.setNsPrefix(RDFUtils.getPrefix(repository, uri));
		try {
			// get all types
			TupleQueryResult graphResult = RDFUtils.executeQuery(repository,
					"SELECT ?label WHERE {<" + uri + "> rdfs:label ?label}");
			String name = null;
			if (graphResult.hasNext()) {
				name = graphResult.next().getBinding("label").getValue()
						.stringValue();
			}
			if (name != null) {
				result.setName(name);
			} else {
				if (result.getNsPrefix() != null) {
					result.setName(result.getNsPrefix());
				} else {
					result.setName(uri);
				}
			}
		} catch (QueryEvaluationException e) {
		}

		return result;
	}

	@Override
	public void fillDefaultURIs(Map<String, EClassifier> classifierURIs) {
	}

}
