package org.github.rdemf.rdf2ecore.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.github.rdemf.core.Activator;
import org.github.rdemf.core.RDEMF;
import org.github.rdemf.rdf2ecore.IRDF2Ecore;
import org.junit.Test;
import org.openrdf.repository.Repository;

public class RDF2EcoreTest {
	IRDF2Ecore rdf2Ecore = RDEMF.make(IRDF2Ecore.class);
	String fileName = "foaf.rdf";
	String path = "/rdf/" + fileName;
	URL url = getClass().getResource(path);
	
	public static final String FOAF = "http://xmlns.com/foaf/0.1/" ;
	
	
	@Test
	public void testRDF2ECoreInstanceIsAvailable() {
		assertNotNull(rdf2Ecore);
	}
	
	@Test
	public void testFillEClassesInExistingResourceSet(){
		Repository repository = RDFUtils.getRepository();
		RDFUtils.addRDFFile(url, repository);
		
		ResourceSetImpl resourceSetImpl = new ResourceSetImpl();
		int sizeBefore = resourceSetImpl.getPackageRegistry().size();
		rdf2Ecore.transform(repository, resourceSetImpl);
		assertTrue(sizeBefore != resourceSetImpl.getPackageRegistry().size());
		
	}
	
	@Test
	public void testFillEClasses(){
		Repository repository = RDFUtils.getRepository();
		RDFUtils.addRDFFile(url, repository);
		ResourceSet set = rdf2Ecore.transform(repository);
		assertTrue(set.getPackageRegistry().getEPackage(FOAF) != null);
	}
	
	@Test
	public void testTransformEPackage(){
		Repository repository = RDFUtils.getRepository();
		RDFUtils.addRDFFile(url, repository);
		EPackage epackage = rdf2Ecore.transform(repository, FOAF);
		assertTrue(epackage != null);
	}

}
