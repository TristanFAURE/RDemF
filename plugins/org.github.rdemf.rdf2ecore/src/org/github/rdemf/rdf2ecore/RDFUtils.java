package org.github.rdemf.rdf2ecore;

import java.util.List;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.model.Namespace;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

public class RDFUtils {
	public static String getPrefix (Repository repo, final String uri){
		try {
			RepositoryConnection connec = repo.getConnection();
			RepositoryResult<Namespace> namespaces = connec.getNamespaces();
			while (namespaces.hasNext()) {
				Namespace ns = namespaces.next();
				if (uri != null && uri.equals(ns.getName())){
					return ns.getPrefix();
				}
			}
		} catch (RepositoryException e) {
		}
		return null;
	}
	
	public static TupleQueryResult executeQuery (Repository repo, String query){
		try {
			RepositoryConnection connec = repo.getConnection();
			return connec.prepareTupleQuery(QueryLanguage.SPARQL, query).evaluate();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}
		return new TupleQueryResult() {
			@Override
			public void remove() throws QueryEvaluationException {
			}
			
			@Override
			public BindingSet next() throws QueryEvaluationException {
				return null;
			}
			
			@Override
			public boolean hasNext() throws QueryEvaluationException {
				return false;
			}
			
			@Override
			public void close() throws QueryEvaluationException {
			}
			
			@Override
			public List<String> getBindingNames() throws QueryEvaluationException {
				return null;
			}
		};
	}
}
