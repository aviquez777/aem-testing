package com.edc.edcweb.core.serviceImpl.pojo;

import java.util.HashMap;

public class EloquaDocHistory {

	private HashMap<String, String> properties = new HashMap<>();
    
    public String getAttr( String attrName ) {
    	if ( properties.containsKey(attrName))
    	{
    		return  properties.get(attrName);
    	}
    	else {
    		return "";
    	}
	}
    
    public void  setAttr(String attrName, String value ) {
    	if(value!=null) {
    		properties.put(attrName, value);
    	}
	}
    
	public String getEmailAddressWithDocID() {
		return getAttr("emailAddressWithDocID");
	}
	
	public void setEmailAddressWithDocID(String emailAddressWithDocID) {
		setAttr("emailAddressWithDocID",emailAddressWithDocID);
	}
	
	public String getEmailAddress() {
		return getAttr("emailAddress");
	}
	
	public void setEmailAddress(String emailAddress) {
		setAttr("emailAddress",emailAddress);
	}
	
	public String getDocId() {
		return getAttr("docId");
	}
	
	public void setDocId(String docId) {
		setAttr("docId",docId);
	}
	
	public String getDocID() { //NOSONAR
		return getAttr("docID");
	}
	
	public void setDocID(String docID) { //NOSONAR
		setAttr("docID",docID);
	}
	
	public String getTimesAccessed() {
		return getAttr("timesAccessed");
	}
	
	public void setTimesAccessed(String timesAccessed) {
		setAttr("timesAccessed",timesAccessed);
	}
	
	
}
