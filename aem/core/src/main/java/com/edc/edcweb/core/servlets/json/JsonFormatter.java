package com.edc.edcweb.core.servlets.json;

import java.util.List;

import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.json.JSONArray;
import com.day.cq.wcm.api.Page;

public abstract class JsonFormatter
{
	private boolean includeTags;
	private boolean includeCurrentPage;
	
	/**
	 * Construct a new JsonFormatter object. 
	 *
	 * @param  includeTags  Set to {@code true} to include each page's tags in the output JSON.
	 * @param  includeCurrentPage  Set to {@code true} to include the current page in the output JSON (if applicable).
	 */	
	public JsonFormatter(boolean includeTags, boolean includeCurrentPage)
	{
		this.includeTags = includeTags;
		this.includeCurrentPage = includeCurrentPage;
	}

	public boolean getIncludeTags()
	{
		return this.includeTags;
	}

	public boolean getIncludeCurrentPage()
	{
		return this.includeCurrentPage;
	}
	
	/**
	 * Create JSON from a list of pages 
	 *
	 * @param  request  Originating request ({@see org.apache.sling.api.SlingHttpServletRequest} Request).
	 * @param  pages  A collection of pages from which JSON will be created.
	 * @return JSON array object.
	 */	
	public abstract JSONArray createJsonFromListOfPages(SlingHttpServletRequest request, List<Page> pages) throws ServletException;

}