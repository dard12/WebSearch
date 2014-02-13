package edu.illinois.ugl.eos.resource.search;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


import edu.illinois.ugl.eos.dao.search.Dao;
import edu.illinois.ugl.eos.model.search.Model;

@Path("/Search")
public class Resource 
{	
	Dao dao = new Dao();
	
	@GET 
	@Path("{query}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML }) //, 
	public List<Model> findByName(@PathParam("query") String query) {
		System.out.println("findByName: " + query);
		return dao.getModels(query);
	}
	
	// This method is called if TEXT_PLAIN is request
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String sayPlainTextHello() {
		return "Hello Jersey";
	}

	// This method is called if XML is request
	@GET
	@Produces(MediaType.TEXT_XML)
	public String sayXMLHello() {
		return "<?xml version=\"1.0\"?>" + "<hello> Hello Jersey" + "</hello>";
	}

	// This method is called if HTML is request
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String sayHtmlHello() {
		return "<html> " + "<title>" + "Hello Jersey" + "</title>"
				+ "<body><h1>" + "Hello Jersey" + "</body></h1>" + "</html> ";
	}

} 