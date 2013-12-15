package org.github.rdemf.rdf2ecore.tests;

import java.io.IOException;
import java.net.URL;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.openrdf.sail.memory.MemoryStore;

public class RDFUtils {

	public static Repository getRepository() {
		MemoryStore memStore = new MemoryStore();
		memStore.setSyncDelay(1000L);
		Repository repo = new SailRepository(memStore);
		try {
			repo.initialize();
		} catch (RepositoryException e) {
		}
		return repo;
	}

	public static void addRDFFile(URL url, Repository repository) {
		try {
			RepositoryConnection con = repository.getConnection();
			try {
				con.add(url, url.toString(), Rio.getParserFormatForFileName(url.getPath()));
			} catch (RDFParseException e) {
			} catch (IOException e) {
			} finally {
				con.close();
			}
		} catch (RepositoryException e) {
		} finally {

		}
	}

}
