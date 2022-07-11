package com.edc.edcweb.core.helpers;

import java.util.List;
import com.edc.edcweb.core.helpers.Constants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;

import com.day.cq.wcm.api.Page;

public class TagResult
{

	public static List<Page> findMatchingPages(SlingHttpServletRequest request, ValueMap properties)
	{
		String startingPage = properties.get(Constants.Properties.ARTICLES_PATH, String.class);
    	String[] tagIds = properties.get(Constants.Properties.TAG_IDS, String[].class);
        boolean matchAny = properties.get(Constants.Properties.TAGS_MATCH, Constants.TAGS_MATCH_ALL).equalsIgnoreCase(Constants.TAGS_MATCH_ANY);
        //---------------------------------------------------------------------
    	return SearchForPages.getPagesByTagIds(request, tagIds, matchAny, startingPage);
	}

}
