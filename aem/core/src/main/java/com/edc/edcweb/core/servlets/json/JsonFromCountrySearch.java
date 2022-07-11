package com.edc.edcweb.core.servlets.json;

import java.util.List;

import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.ArticlePageHelper;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.helpers.Request;
import com.edc.edcweb.core.models.CountrySearchData;

public class JsonFromCountrySearch  
{

	/**
	 * Construct a new JsonFromPages object. 
	 *
	 * @param   
	 * @param   
	 */	
	public JsonFromCountrySearch( )
	{
		//this is emptybecause its in develpment...
	}

	/**
	 * Create JSON from a list of country pages 
	 *
	 * @param  request  Originating request ({@see org.apache.sling.api.SlingHttpServletRequest} Request).
	 * @param  List<CountrySearchData>  A collection of countrysearchdata from which JSON will be created.
	 * @return JSON array object.
	 */	
	public JSONArray createJsonFromCountrySearchData(SlingHttpServletRequest request, List<CountrySearchData> countriesdata) throws ServletException
	{
		JSONArray mainJAry = new JSONArray();
		int count = 1;
		try {
			for(CountrySearchData countrydata : countriesdata)
			{

				//-------------------------------------------------------------
				// Do not include the current page in the result set
				//-------------------------------------------------------------
				JSONObject job = new JSONObject(); 

				//---------------------------------------------------------
				//job.put(Constants.Properties.ID, count++);
				job.put("country", countrydata.getCountryName());
				job.put("code", countrydata.getCountryID());
				job.put("regionKey", countrydata.getRegion());
				job.put("positionKey", countrydata.getPosition());
				job.put("url", countrydata.getCountryPageUrl());
				job.put("regionValue", countrydata.getRegionLabel());
				job.put("positionValue", countrydata.getPositionLabel());
				job.put("premium", countrydata.getPremium());
				job.put("isInitial", String.valueOf(countrydata.getIsLetter()));


				//---------------------------------------------------------
				mainJAry.put(job);

			}
			return mainJAry;
		} catch (JSONException e) {
			throw new ServletException(e);
		}
	}
}
