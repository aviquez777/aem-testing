package com.edc.edcweb.core.helpers.ehh;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 * 
 * 
 * Bean object for EHH question.
 * 
 */
public class EHHQuestion {
	
	private String id= "";
	private String enQuestion = "";
	private String frQuestion = "";
	private String enAnswer = "";
	private String frAnswer = "";
	private String author = "";
	private String question = "";
	private String url = "";
	
	//keep original values for checking
	private String authorSource="";
	private ArrayList<String> categoriesSource = new ArrayList<String>();
	private ArrayList<String> countriesSource = new ArrayList<String>();
	
	private ArrayList<String> categories = new ArrayList<String>();
	private ArrayList<String> countries = new ArrayList<String>();
		
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getEnQuestion() {
		return enQuestion;
	}
	
	public void setEnQuestion(String enQuestion) {
		this.enQuestion = enQuestion;
	}
	
	public String getFrQuestion() {
		return frQuestion;
	}
	
	public void setFrQuestion(String frQuestion) {
		this.frQuestion = frQuestion;
	}
	
	public String getQuestion() {
		return question;
	}
	
	public void setQuestion(String question) {
		this.question = question;
	}
	
	public String getEnAnswer() {
		return enAnswer;
	}
	
	public void setEnAnswer(String enAnswer) {
		this.enAnswer = enAnswer;
	}
	
	public String getFrAnswer() {
		return frAnswer;
	}
	
	public void setFrAnswer(String frAnswer) {
		this.frAnswer = frAnswer;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public void setAuthor(String authorName) {
		this.author = authorName;
	}
	
	
	public List<String> getCategories() {
		return categories;
	}
	
		public void setCategories(List<String> categories) {
			this.categories = (ArrayList<String>) categories;
	}
	
	public List<String> getCountries() {
		return countries;
	}
	
	public void setCountries(List<String> countries) {
		this.countries = (ArrayList<String>) countries;
	}
	
	public String getAuthorSource() {
		return authorSource;
	}

	public void setAuthorSource(String authorSource) {
		this.authorSource = authorSource;
	}

	public List<String> getCategoriesSource() {
		return categoriesSource;
	}

	public void setCategoriesSource(List<String> categoriesSource) {
		this.categoriesSource = (ArrayList<String>) categoriesSource;
	}

	public List<String> getCountriesSource() {
		return countriesSource;
	}

	public void setCountriesSource( List<String> countriesSource) {
		this.countriesSource = (ArrayList<String>) countriesSource;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
