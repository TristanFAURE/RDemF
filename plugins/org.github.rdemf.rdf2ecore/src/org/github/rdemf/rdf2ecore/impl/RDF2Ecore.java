package org.github.rdemf.rdf2ecore.impl;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.github.rdemf.rdf2ecore.EMFUtils;
import org.github.rdemf.rdf2ecore.IRDF2Ecore;
import org.github.rdemf.rdf2ecore.RDFUtils;
import org.github.rdemf.rdf2ecore.transform.IRDFTransform;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;

public class RDF2Ecore implements IRDF2Ecore {

	@Inject
	IRDFTransform transformer;

	@Override
	public void transform(Repository repository, ResourceSet resourceSet) {
		Map<String, EClassifier> classifierURIs = new HashMap<String, EClassifier>();
		transformer.fillDefaultURIs (classifierURIs);
		try {
			// get all types
			TupleQueryResult graphResult = RDFUtils
					.executeQuery(repository,
							"SELECT ?s ?epackage WHERE {?s a rdfs:Class. ?s rdfs:isDefinedBy ?epackage }");
			while (graphResult.hasNext()) {
				BindingSet s = graphResult.next();
				// get epackage
				String id = s.getValue("epackage").stringValue();
				EPackage epackage = resourceSet.getPackageRegistry()
						.getEPackage(id);
				if (epackage == null) {
					epackage = transformer
							.transformIntoEPackage(id, repository);
					resourceSet.getPackageRegistry().put(id, epackage);
				}
				if (epackage == null) {
					return;
				}
				// create classifier
				Value classURI = s.getValue("s");
				EClassifier classifier = transformer.transformIntoClass(
						classURI, repository);
				classifierURIs.put(classURI.stringValue(), classifier);
				if (classifier != null) {
					epackage.getEClassifiers().add(classifier);
				}
			}
			
			// deal with subClass
			TupleQueryResult subclassResult  = RDFUtils
					.executeQuery(repository,
							"SELECT ?source  ?target WHERE {?source rdfs:subClassOf ?target. }");
			while (subclassResult.hasNext()) {
				BindingSet s = subclassResult.next();
				EClassifier source = classifierURIs.get(s.getBinding("source").getValue().stringValue());
				EClassifier target  = classifierURIs.get(s.getBinding("target").getValue().stringValue());
				if (source instanceof EClass && target instanceof EClass){
					if (source instanceof EClass) {
						EClass eclass = (EClass) source;
						eclass.getESuperTypes().add((EClass) target);
					}
				}
			}
			
			// deal with relation ships
			TupleQueryResult propertyResult  = RDFUtils
					.executeQuery(repository,
							"SELECT ?p ?label ?domain ?range  WHERE {?p a rdf:Property . ?p rdfs:label ?label . ?p rdfs:domain ?domain . ?p rdfs:range ?range }");
			while (propertyResult.hasNext()) {
				BindingSet s = propertyResult.next();
				EClassifier source = classifierURIs.get(s.getBinding("domain").getValue().stringValue());
				if (source instanceof EClass){
					EClassifier target = classifierURIs.get(s.getBinding("range").getValue().stringValue());
					String name = EMFUtils.getName(s.getBinding("label").getValue().stringValue());
					if (target != null){
						EReference ref = EcoreFactory.eINSTANCE.createEReference();
						ref.setName(name);
						((EClass)source).getEStructuralFeatures().add(ref);
						ref.setEType(target);
					}
					else {
						EAttribute att = EcoreFactory.eINSTANCE.createEAttribute();
						att.setName(name);
						// TODO deal with datatypes
						((EClass)source).getEStructuralFeatures().add(att);
					}
				}
				
			}
			
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}

	}

	@Override
	public ResourceSet transform(Repository repository) {
		ResourceSet set = new ResourceSetImpl();
		transform(repository, set);
		return set;
	}

	@Override
	public EPackage transform(Repository repository, String uri) {
		ResourceSet set = transform(repository);
		return set.getPackageRegistry().getEPackage(uri);
	}

}
