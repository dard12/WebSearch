package edu.illinois.ugl.eos.dao.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import edu.illinois.ugl.eos.model.search.Model;

public class Dao 
{	
	public List<Model> getModels(String lookfor)
	{
		// Create list
		Hashtable<String, Model> models = new Hashtable<String, Model>();

		// Add data to list
		while (results.hasNext()) 
		{
		    // Convert to string
		    String subject = s.toString();
		    String predicate = p.toString();
		    String object = o.toString();
		    
		    if(subject.contains("http://sif.grainger.uiuc.edu/Nash/"))
		    {
		    	// Get relevant model
		    	Model model = models.get(subject);
		    	if( model == null)
		    	{
		    		model = new Model();
		    		models.put(subject, model);
		    	}
		    
		    	// Add data to model
		    	if(predicate.equalsIgnoreCase("http://purl.org/dc/elements/1.1/title"))
		    	{
		    		if(object.trim().endsWith("/"))
		    			object = object.substring(0, object.length() - 2);
		    		model.setTitle(object);
		    	}
		    	else if(predicate.equalsIgnoreCase("http://purl.org/dc/elements/1.1/creator"))
		    		model.setCreator(object);
			    else if(predicate.equalsIgnoreCase("http://purl.org/dc/elements/1.1/type"))
			    	model.setType(object);
			    else if(predicate.equalsIgnoreCase("http://purl.org/dc/elements/1.1/publisher"))
			    	model.setPublisher(object);
				else if(predicate.equalsIgnoreCase("http://purl.org/dc/elements/1.1/date"))
					model.setDate(object);
				else if(predicate.equalsIgnoreCase("http://purl.org/dc/elements/1.1/language"))
					model.setLanguage(object);
				else if(predicate.equalsIgnoreCase("http://purl.org/dc/elements/1.1/description"))
					model.setDescription(object);
			    else if(predicate.equalsIgnoreCase("http://purl.org/dc/elements/1.1/subject"))
			    	model.setSubject(object);
			    else if(predicate.equalsIgnoreCase("http://purl.org/dc/elements/1.1/coverage"))
			    	model.setCoverage(object);
			    else if(predicate.equalsIgnoreCase("http://purl.org/dc/elements/1.1/relation type=\"original\""))
			    	model.setRelation_original(object);
			    else if(predicate.equalsIgnoreCase("http://purl.org/dc/elements/1.1/relation"))
			    	model.setRelation(object);		    	
				else if(predicate.equalsIgnoreCase("http://purl.org/dc/elements/1.1/identifier"))
					model.setIdentifier(object);
				else if(predicate.equalsIgnoreCase("http://purl.org/dc/elements/1.1/rights"))
					model.setRights(object);
		    }
		}

		Collection<Model> c = models.values();
		List<Model> l = new ArrayList<Model>(c);
		
		for(Model m:l)
			System.out.println(m.getTitle());
		
		return l;
	}
	
	

  
}