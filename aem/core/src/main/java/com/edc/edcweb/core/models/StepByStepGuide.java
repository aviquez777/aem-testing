package com.edc.edcweb.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.CountryInfoHelper;
import com.edc.edcweb.core.helpers.LinkResolver;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 * 
 * 
 * Backing Java code Step by Step Exporting Guide component.
 * Specific position is defined in property position in country resource under /data/countryinfo.
 * 
 */

@Model(adaptables = SlingHttpServletRequest.class)
public class StepByStepGuide {
	
	private static final Logger log = LoggerFactory.getLogger(Header.class);
	
	@Inject
	@Source("sling-object")
	private ResourceResolver resolver;

	@Inject
	private SlingHttpServletRequest request;
	
	@Inject
	private Page currentPage;

	private String title;
	private String subtitle1;
	private String subtitle2;
	private String subtitle3;
	private String subtitle4;

	private String text1;
	private String text2;
	private String text3;
	private String text4;
	
	private String disclaimer;
	private String linkUrl;
	private String linkText;
	private String linkTarget;
	private String edcPosition;
	
	
	@PostConstruct
	public void initModel()
	{
		ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(this.request, this.currentPage);
        if (contentPolicy != null) {

        	ValueMap properties = contentPolicy.getProperties();
        	
        	this.title = properties.get(Constants.Properties.STEPBYSTEPGUIDE_TITLE, String.class);
        	this.subtitle1 = properties.get(Constants.Properties.STEPBYSTEPGUIDE_SUBTITLE1, String.class);
        	this.subtitle2 = properties.get(Constants.Properties.STEPBYSTEPGUIDE_SUBTITLE2, String.class);
        	this.subtitle3 = properties.get(Constants.Properties.STEPBYSTEPGUIDE_SUBTITLE3, String.class);
        	this.subtitle4 = properties.get(Constants.Properties.STEPBYSTEPGUIDE_SUBTITLE4, String.class);
        	
        	this.text1 = properties.get(Constants.Properties.STEPBYSTEPGUIDE_TEXT1, String.class);
        	this.text2 = properties.get(Constants.Properties.STEPBYSTEPGUIDE_TEXT2, String.class);
        	this.text3 = properties.get(Constants.Properties.STEPBYSTEPGUIDE_TEXT3, String.class);
        	this.text4 = properties.get(Constants.Properties.STEPBYSTEPGUIDE_TEXT4, String.class);
        	
        	this.disclaimer  = properties.get(Constants.Properties.STEPBYSTEPGUIDE_DISCLAIMER, String.class);
        	this.linkUrl  = LinkResolver.addHtmlExtension(properties.get(Constants.Properties.STEPBYSTEPGUIDE_LINKURL, ""));
        	this.linkText = properties.get(Constants.Properties.STEPBYSTEPGUIDE_LINKTEXT, String.class);
        	this.linkTarget = properties.get(Constants.Properties.STEPBYSTEPGUIDE_LINKTARGET, String.class);
        	
        	String regionTag = CountryInfoHelper.getPageRegionTag(currentPage, this.request);
 
			String countryID = CountryInfoHelper.fetchCountryID(regionTag, request ); 
        	this.edcPosition = CountryInfoHelper.getPosition(countryID, request);
 
			
        }
	}
	
		
	// access methods
	
	public String getTitle() {
		return this.title;
	}
	
	public String getSubtitle1() {
		return this.subtitle1;
	}

	public String getSubtitle2() {
		return this.subtitle2;
	}

	public String getSubtitle3() {
		return this.subtitle3;
	}

	public String getSubtitle4() {
		return this.subtitle4;
	}

	public String getText1() {
		return this.text1;
	}

	public String getText2() {
		return this.text2;
	}

	public String getText3() {
		return this.text3;
	}

	public String getText4() {
		return this.text4;
	}

	public String getDisclaimer() {
		return this.disclaimer;
	}
	
	public String getLinkUrl() {
		return linkUrl;
	}
	
	public String getLinkText() {
		return linkText;
	}
	
	public String getLinkTarget() {
		return linkTarget;
	}
	
	public String getEdcPosition() {
		return edcPosition;
	}
	
	public String getPositionValid() {
		if(!edcPosition.equalsIgnoreCase(Constants.Properties.STEPBYSTEPGUIDE_EDCPOSITION_CLOSED)) {
			return edcPosition;
		}
		else {
			return null;
		}
	}
}