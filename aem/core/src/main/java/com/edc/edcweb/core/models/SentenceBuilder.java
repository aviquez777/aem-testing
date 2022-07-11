package com.edc.edcweb.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.Request;
import com.edc.edcweb.core.helpers.TagHelper;

/**
 * @author Scott Ross
 * @version 1.0.0
 * @since 1.0.0
 * 
 * 
 * This model is a JSON representation of the tags relevant to the sentence builder.  Only tags currently in use will be included.
 * 
 * 
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class SentenceBuilder
{
	private static final Logger log=LoggerFactory.getLogger(SentenceBuilder.class);

	@Self
	private SlingHttpServletRequest request;
	

	private String categoryJson = "";
	private List<FilterTag> regions = new ArrayList<>();		
	
	
	@PostConstruct
	public void initModel() {
		Page page = Request.adaptToPage(this.request);
		ResourceResolver resolver = request.getResource().getResourceResolver();
		
		this.regions = populateTagList (resolver.getResource(Constants.Paths.REGION_TAGS), page);	
		
		try {
			this.categoryJson = populateCategoryJson(resolver.getResource(Constants.Paths.CATEGORY_TAGS), page);
		} catch (JSONException e) {
			log.error("Error: JSONException: {}",e.getMessage()); 
		}
		
		log.info("resultant category json: " + categoryJson);
		
	}	
	

	/**
	 * Returns a List of FilterTag objects
	 * <p>
	 * @param  resource  a JCR resource corresponding with tags needed.
	 * @param  page  a page object - used to resolve the tag titles based on current locale. 
	 * @return List<FilterTag> a list of FilterTag
	 * @see FilterTag
	 */	
	private List<FilterTag> populateTagList(Resource resource, Page page) {
		List<FilterTag> listOfTags = new ArrayList<>();
		if (resource != null) {							
			Tag root = resource.adaptTo(Tag.class);
			
			if (root != null) {
				
			Iterator<Tag> childTags = root.listChildren();
				while (childTags.hasNext()) {	
					Tag item = childTags.next();	
					
					if (item.find().hasNext()) { // only include tags that have been applied to at least one content item								
						FilterTag tag = new FilterTag( item.getTagID().replace("edc:",  ""), TagHelper.getLocalizedTagTitle(page, item));
						listOfTags.add(tag);
					}
				}
			}
		}
		return listOfTags;
	}	
	
	
	/**
	 * Returns a JSON string of all parent and child tags the provided tag path
	 * <p>
	 * @param  resource  a JCR resource corresponding with tags needed.
	 * @param  page  a page object - used to resolve the tag titles based on current locale. 
	 * @return JSON String
	 */	
	private String populateCategoryJson (Resource resource, Page page) throws JSONException {
		
		JSONObject sentenceBuilderJson = new JSONObject();
		JSONObject categoryListJson = new JSONObject();	
	
		if (resource != null) {
			Tag categoryTags = resource.adaptTo(Tag.class);
			if (categoryTags != null) {
				Iterator<Tag> categoryTagsIterator = categoryTags.listChildren();
				while (categoryTagsIterator.hasNext()) {	
					Tag category = categoryTagsIterator.next();
					if (category.find().hasNext()) { // only include tags that have been applied to at least one content item				
						log.debug("processing category: " + TagHelper.getLocalizedTagTitle(page, category));
						
						JSONObject categoryJson = new JSONObject();
						JSONObject childTagJson = new JSONObject();
						
						categoryJson.put("tagLabel", TagHelper.getLocalizedTagTitle(page, category));
	
						// now process category child tags			
						Iterator<Tag> childTagsIterator = category.listChildren();
						while (childTagsIterator.hasNext()) {
							Tag child = childTagsIterator.next();	
							if (child.find().hasNext()) {
								childTagJson.put(child.getTagID().replaceAll("edc:", ""), TagHelper.getLocalizedTagTitle(page, child));
							} else {
								log.debug("filtered out sub-category tag: " + child.getTitle() + " as it has not been assigned to a content item.");
							}	
						}	
						// add the categories to categoryJson, and when done add the category to sentenceBuilderJson	
						categoryJson.put("childTags", childTagJson);
						categoryListJson.put(category.getTagID().replaceAll("edc:",  ""), categoryJson);
					} else {
						log.debug("filtered out tag: " + category.getTitle() + " as it has not been assigned to a content item.");
					}
				}
				sentenceBuilderJson.put("", categoryListJson);
			}
				
		}		
//		return sentenceBuilderJson.toString();
		return categoryListJson.toString();
	}	
			
	// access methods	
	
	public String getCategoryJson() {
		return categoryJson;
	}

	public List<FilterTag> getRegions() {
		return new ArrayList<>(regions);
	}	
}
