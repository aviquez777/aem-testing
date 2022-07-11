package com.edc.edcweb.core.components;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.edc.edcweb.core.datasources.TagsList;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.day.cq.wcm.api.policies.ContentPolicy;
import org.apache.sling.api.resource.ValueMap;
/**
 * @author Peter Crummey
 * @version 0.0.5
 * @since 0.0.5
 * 
 * 
 * Backing Java code for the Tag Cloud component used by the EDC web site.
 * 
 * 
 */
public class TagCloud extends WCMUsePojo
{
	private List<Tag> tags;
	private String title="";
	
	@Override
	public void activate() throws Exception
	{
		this.tags = new ArrayList<>();
		
		this.title = populateTitleFromPolicy( );
		//---------------------------------------------------------------------
		// Get all tags assigned to this page
		//---------------------------------------------------------------------
		String[] tagArray = TagsList.getTagsFromRequest(getResourceResolver(), getRequest());
		final TagManager tagMgr = getResourceResolver().adaptTo(TagManager.class);
		//---------------------------------------------------------------------
		if(tagMgr != null){
			//-----------------------------------------------------------------
			// Iterate all tags adding them to the "tags" list as long as they
			// are not one of the tag types that should not be in the tag cloud
			//-----------------------------------------------------------------
			for(String tag: tagArray)
			{
				Tag tagObject = tagMgr.resolve(tag);
				if(tagObject != null){
					
					String tagNamespace = tagObject.getNamespace().getName() + Constants.TAG_CLOUD_NAMESPACE_DELIMITER;
					 
					
					if(this.isValidTag(tagObject, tagNamespace)){
						this.tags.add(tagObject);
					}
				}				
			}
		}
	}
	
	private boolean isValidTag(Tag tagObject, String tagNamespace){
		boolean valid = true;
		
		for(int i = 0; i < Constants.ArrayValues.TAG_CLOUD_TAGS_TO_IGNORE.toArray().length; i++)
		{
			if(tagObject.getTagID().startsWith(tagNamespace + Constants.ArrayValues.TAG_CLOUD_TAGS_TO_IGNORE.toArray()[i]))
			{
				valid = false;
			}
			
		}
		
		return valid;
		
	}

	public List<Tag> getTags() {
		return new ArrayList<>(this.tags);
	}
	
	public String getTitle() {
		 
		return this.title;
	}
	
	public String populateTitleFromPolicy( )
	{
		String titlePolicy = "";
		
		ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(getRequest(), getCurrentPage());
		if (contentPolicy != null) {
			
			ValueMap properties = contentPolicy.getProperties();
			titlePolicy = properties.get(Constants.Properties.TITLE, String.class);	
	 
		}		
		 
		 	
		return titlePolicy;
	}

}
