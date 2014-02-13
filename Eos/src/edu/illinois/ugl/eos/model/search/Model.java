package edu.illinois.ugl.eos.model.search;


import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Model 
{
	private String title;
	private String creator;
	private String type;
	private String publisher;
	private String date;
	private String language;
	private String description;
	private String subject;
	private String coverage;
	private String relation_original;
	private String relation;
	private String identifier;
	private String rights;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getCoverage() {
		return coverage;
	}
	public void setCoverage(String coverage) {
		this.coverage = coverage;
	}
	public String getRelation_original() {
		return relation_original;
	}
	public void setRelation_original(String relation_original) {
		this.relation_original = relation_original;
	}
	public String getRelation() {
		return relation;
	}
	public void setRelation(String relation) {
		this.relation = relation;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getRights() {
		return rights;
	}
	public void setRights(String rights) {
		this.rights = rights;
	}
	public Model(String title, String creator, String type, String publisher,
			String date, String language, String description, String subject,
			String coverage, String relation_original, String relation,
			String identifier, String rights) {
		super();
		this.title = title;
		this.creator = creator;
		this.type = type;
		this.publisher = publisher;
		this.date = date;
		this.language = language;
		this.description = description;
		this.subject = subject;
		this.coverage = coverage;
		this.relation_original = relation_original;
		this.relation = relation;
		this.identifier = identifier;
		this.rights = rights;
	}

	public Model() {
		super();
		this.title = "";
		this.creator =  "";
		this.type =  "";
		this.publisher =  "";
		this.date =  "";
		this.language =  "";
		this.description =  "";
		this.subject =  "";
		this.coverage =  "";
		this.relation_original =  "";
		this.relation =  "";
		this.identifier =  "";
		this.rights =  "";
	}	
}