package com.edc.edcweb.core.datasources;
 
import java.util.HashMap;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.commons.collections.iterators.TransformIterator; 
import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.Request;
import com.edc.edcweb.core.helpers.TagHelper;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
  
/**
 * @author Peter Crummey
 * @version 0.0.3-SNAPSHOT
 * @since 0.0.3-SNAPSHOT
 * 
 * 
 * Datasource for the Article Hero component used by the EDC web site.
 * 
 * 
 */
public class TagsList extends WCMUsePojo
{

	@Override
	public void activate() throws Exception
	{
		 final ResourceResolver resolver = getResource().getResourceResolver();
		 final TagManager tagMgr = resolver.adaptTo(TagManager.class);
		 //--------------------------------------------------------------------
		 String[] tags = getTagsFromRequest(resolver, getRequest());
		 //--------------------------------------------------------------------
		 Page page = Request.adaptToPage(getRequest());
		 
		 @SuppressWarnings("unchecked")
		 DataSource ds = new SimpleDataSource(new TransformIterator(new ArrayIterator(tags), new Transformer()
		 {
		  
			 @Override
			 public Object transform(Object o)
			 {
				 String tagId = o.toString();
				 
				 ValueMap vm = new ValueMapDecorator(new HashMap<String, Object>());
				 if(tagMgr != null)
				 {
					 vm.put(Constants.VALUE, tagId);
					 Tag tag = tagMgr.resolve(tagId);
					 if(tag != null)
					 {
						 String text = TagHelper.getLocalizedTagTitle(page, tag);
						 vm.put(Constants.TEXT, text);
					 }
				 }				  
				 return new ValueMapResource(resolver, new ResourceMetadata(), Constants.Properties.NT_UNSTRUCTURED, vm);
			 }
		 }));
		 this.getRequest().setAttribute(DataSource.class.getName(), ds);
	 }

	public static String[] getTagsFromRequest(final ResourceResolver resolver, SlingHttpServletRequest request)
	{
		 String pagePath = Request.getPagePath(request);
		 return getTagsFromPagePath(resolver, pagePath);
	}
	
	public static String[] getTagsFromPagePath(final ResourceResolver resolver, String pagePath)
	{
		String[] tags = {};
		if(!pagePath.isEmpty())
		{
			//-----------------------------------------------------------------
			Resource nodeResource = resolver.resolve(pagePath + Constants.Paths.JCR_CONTENT);
			 
			if(nodeResource != null)
			{
				ValueMap valueMap = nodeResource.getValueMap();
				tags = valueMap.get(Constants.Properties.CQ_TAGS, String[].class);
				if(tags == null){
					tags = new String [] {};
				}
			}						 
		}
		return tags;
	}
}