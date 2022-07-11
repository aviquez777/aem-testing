package com.edc.edcweb.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.helpers.LinkResolver;

/**
 * @author Scott Ross
 * @version 1.0.0
 * @since 1.0.0
 * 
 * 
 * Parameterized Model for links.  Properties being text, url and target (e.g., existing window, new window, etc.).
 * 
 * 
 */
@Model(adaptables=SlingHttpServletRequest.class) 
public class ParamTextUrlLinks
{

	private static final Logger log= LoggerFactory.getLogger(ParamTextUrlLinks.class);	
	  @Inject @Default(values="") 
	  private Resource resource; 

	  @Inject @Default(values="") 
	  private String linkText;
	  
	  @Inject @Default(values="")
	  private String linkUrl;
	   
	  @Inject @Default(values="") 
	  private String linkTarget;	  

	  private String resolvedLinkUrl;	  


	@PostConstruct
	public void initModel() {
		//do nothing
	}	
	
	
	
	/**
	 * Get the link text.  
	 *
	 */	
	public String getLinkText() {
		return linkText;
	}

	
	/**
	 * Get the link URL. (Uses LinkResolver to add html extension if appropriate)  
	 *
	 */	
	public String getLinkUrl() {
		log.debug("original link: " + this.linkUrl);
		this.linkUrl = LinkResolver.addHtmlExtension(this.linkUrl);
		log.debug("updated link: " + this.linkUrl);
		return this.linkUrl;
	}
	
	/**
	 * Get the resolved link URL. (Uses LinkResolver to resolve the link.)  
	 *
	 */	
	public String getResolvedLinkUrl() {
		log.debug("original link: " + this.linkUrl);
		this.resolvedLinkUrl = LinkResolver.reverseMapLink(resource.getResourceResolver(), LinkResolver.addHtmlExtension(this.linkUrl));
		log.debug("updated link: " + this.resolvedLinkUrl);
		return this.resolvedLinkUrl;
	}

	/**
	 * Get the link's target (e.g., existing window, new window, etc.).  
	 *
	 */	
	public String getLinkTarget() {
		return linkTarget;
	}
}
