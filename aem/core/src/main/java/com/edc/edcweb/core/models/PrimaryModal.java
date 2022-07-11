package com.edc.edcweb.core.models;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.LinkResolver;

/**
 * @author Peter Crummey
 * @version 0.0.3-SNAPSHOT
 * @since 0.0.3-SNAPSHOT
 * 
 * 
 * Backing Java code for the Primary Modal component used by the EDC web site.
 * 
 * 
 */
@Model(adaptables = Resource.class)
public class PrimaryModal
{
	@Self
	private Resource resource;
	
	private String linkUrl1;
	private String linkUrl2;
	
	/**
	 * Get <em>Call to Action</em> link # 1 (resolved).  
	 *
	 * @return  String  Link for CTA button # 1.
	 */	
	public String getLinkUrl1() {
		return this.linkUrl1;
	}

	/**
	 * Get <em>Call to Action</em> link # 2 (resolved).  
	 *
	 * @return  String  Link for CTA button # 2.
	 */	
	public String getLinkUrl2() {
		return this.linkUrl2;
	}


	@PostConstruct
	public void initModel()
	{
		ValueMap properties = this.resource.adaptTo(ValueMap.class);
		if(properties != null)
		{
			this.linkUrl1 = LinkResolver.addHtmlExtension(properties.get(Constants.Properties.LINK_URL_1, ""));
			this.linkUrl2 = LinkResolver.addHtmlExtension(properties.get(Constants.Properties.LINK_URL_2, ""));
		}
	}

}
