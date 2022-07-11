package com.edc.edcweb.core.servlets.json;

import java.util.Map;

import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.edc.edcweb.core.models.CountrySearchData;

public class JsonFromCollections
{
	/**
	 * Create JSON from a {@codeMap&lt;String, String&gt;}
	 *
	 * @param  request  Originating request ({@see org.apache.sling.api.SlingHttpServletRequest} Request).
	 * @param  collection  A collection from which JSON will be created.
	 * @return JSON in the format of a string.
	 */	
	public static JSONObject createJson(SlingHttpServletRequest request, Map<String, String> collection) throws ServletException
	{
    	JSONObject job = new JSONObject();
    	try {
	        for(Map.Entry<String, String> element : collection.entrySet())
	        {
	            //---------------------------------------------------------
				job.put(element.getKey(), element.getValue());
	            //---------------------------------------------------------
        	}
	    	return job;
		} catch (JSONException e) {
			throw new ServletException(e);
		}
	}
	
	public static JSONArray createJsonArray(SlingHttpServletRequest request, Map<String, String> collection, String keyName, String valueName) throws ServletException
	{
		
		JSONArray mainJAry = new JSONArray();
        int count = 1;
    	try {
	        for(Map.Entry<String, String> element : collection.entrySet())
	        {
	        	
	            //-------------------------------------------------------------
	        	// Do not include the current page in the result set
	            //-------------------------------------------------------------
	        		JSONObject job = new JSONObject(); 
	        		job.put(keyName, element.getKey());
	        		job.put(valueName, element.getValue()); 
		           	            	 
		            //---------------------------------------------------------
		        	mainJAry.put(job);
	        	 
	        }
	    	return mainJAry;
		} catch (JSONException e) {
			throw new ServletException(e);
		}
    	 
	}

}
