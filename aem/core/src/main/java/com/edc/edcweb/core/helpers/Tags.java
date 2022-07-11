package com.edc.edcweb.core.helpers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;

public class Tags
{
	private static final Logger log = LoggerFactory.getLogger(Tags.class);

	/**
	 * Retrieves all tags assigned to the given <code>request</code>. Tag titles are returned in the same language as the request (if available).
	 *
	 * @param  request  Sling HTTP servlet request whose tags will be retrieved.
	 * @param  removeNamespace  If true, the EDC namespace (e.g., "edc:") will be removed from the start of each tag id in the resulting collection.
	 * @return Collection of tag ids to localized (language-appropriate) tag title.
	 */	
	public static Map<String, String> listAllTags(SlingHttpServletRequest request, boolean removeNamespace)
	{
		String tempTagId;
		Map<String, String> allTags = new HashMap<>();
	    Resource tagResource = request.getResourceResolver().getResource(Constants.Paths.EDC_NAMESPACE_TAGS);
	    if (tagResource!= null)
	    {
	        final Iterable<Resource> children = tagResource.getChildren();
	        final Iterator<Resource> itr = children.iterator();
	        TagManager tagManager = request.getResourceResolver().adaptTo(TagManager.class);
	        Page page = Request.adaptToPage(request);
	        while (itr.hasNext())
	        {
	            final Resource item = itr.next();
	            final Tag tag = item.adaptTo(Tag.class);
	            if (tag != null)
	            {
	            	if(removeNamespace)
	            	{
	            		tempTagId = tag.getTagID().replace(Constants.TAGS_EDC_NAMESPACE, "");
	            	} else {
	            		tempTagId = tag.getTagID();
	            	}
					allTags.put(tempTagId, TagHelper.getLocalizedTagTitle(tagManager, page, tag.getTagID()));
					Iterator<Tag> childitr = tag.listAllSubTags();
					while (childitr.hasNext())
					{
						final Tag childitem = childitr.next();
		            	if(removeNamespace)
		            	{
		            		tempTagId = childitem.getTagID().replace(Constants.TAGS_EDC_NAMESPACE, "");
		            	} else {
		            		tempTagId = childitem.getTagID();
		            	}
						allTags.put(tempTagId, TagHelper.getLocalizedTagTitle(tagManager, page, childitem.getTagID()));
					}
	            }

	        }
	    }
	    if(log.isDebugEnabled())
	    {
		    log.debug("Found tags:");
		    log.debug("---------------------------------------------------");
		    for(Map.Entry<String, String> aTag : allTags.entrySet())
		    {
		    	log.debug(aTag.getKey() + "=" + aTag.getValue());
		    }
	    }
		return allTags;
	}
}