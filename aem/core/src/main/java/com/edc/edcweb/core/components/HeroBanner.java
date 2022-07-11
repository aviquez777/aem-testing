package com.edc.edcweb.core.components;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.helpers.constants.ConstantsAccessibility;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

/**
 * @author Peter Crummey
 * @version 1.0.0
 * @since 1.0.0
 * 
 * 
 * Backing Java code for the Hero Banner component used by the EDC web site.
 * 
 * 
 */
public class HeroBanner extends WCMUsePojo {

	private ValueMap policy_properties;
	
	private String policy_heading = "";
	private String policy_text = "";
	private String policy_tagline = "";
	private String policy_alignment = "";
	private String policy_linktarget = "";
	private String policy_linktext = "";
	private String policy_linkurl = "";
	private String policy_fileReference = "";
	private String policy_tabletFileReference = "";
	private String policy_phoneFileReference = "";
	private String policy_alt = "";
	private String policy_lazyload = "";
	private String policy_bgicon = "";
	
	private String resolvedBtnLink = "";
	private String resolvedPolicyBtnLink = "";
	private Calendar pageLastPublished;

	/**
	 * Populates the Hero Banner images for phone, tablet and desktop breakpoints.  
	 *
	 */	
    @Override
    public void activate() throws Exception {
        //---------------------------------------------------------------------
        ValueMap properties = getResource().adaptTo(ValueMap.class);
        if(properties != null)
        {
			resolvedBtnLink = LinkResolver.reverseMapLink(getResourceResolver(), properties.get(Constants.Properties.LINK_URL, ""));
			// cq:lastreplicated is not carried over to publish by design, use jcr:created. This property gets updated every time a page is published.
			pageLastPublished = getCurrentPage().getProperties().get(Constants.Properties.JCR_CREATED, Calendar.class);
        }
        
        //policy properties
        ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(getRequest(), getCurrentPage());
        if (contentPolicy != null)
        {
                policy_properties = contentPolicy.getProperties();

                this.policy_heading = policy_properties.get(Constants.Properties.HEROBANNER_HEADING, String.class);
                this.policy_text = policy_properties.get(Constants.Properties.HEROBANNER_TEXT, String.class);
                this.policy_alignment = policy_properties.get(Constants.Properties.HEROBANNER_ALIGNMENT, String.class);
                this.policy_tagline = policy_properties.get(Constants.Properties.HEROBANNER_TAGLINE, String.class);
                this.policy_linktarget = policy_properties.get(Constants.Properties.HEROBANNER_LINKTARGET, String.class);
                this.policy_linktext = policy_properties.get(Constants.Properties.HEROBANNER_LINKTEXT, String.class);
                this.policy_fileReference = policy_properties.get(Constants.Properties.HEROBANNER_FILEREFERENCE, String.class);
                this.policy_tabletFileReference = policy_properties.get(Constants.Properties.HEROBANNER_TABLETFILEREFERENCE, String.class);
                this.policy_phoneFileReference = policy_properties.get(Constants.Properties.HEROBANNER_PHONEFILEREFERENCE, String.class);
                this.policy_alt = policy_properties.get(Constants.Properties.HEROBANNER_ALT, String.class);
                this.policy_lazyload = policy_properties.get(Constants.Properties.HEROBANNER_LAZYLOAD, String.class);
                this.resolvedPolicyBtnLink = LinkResolver.reverseMapLink(getResourceResolver(), policy_properties.get(Constants.Properties.HEROBANNER_LINKURL, String.class));
                this.policy_bgicon = policy_properties.get(Constants.Properties.HEROBANNER_BACKGROUNDICON, String.class);

        }

    }

	/**
	 * Get the Hero Banner's resolved button (CTA) link.  
	 *
	 * @return  String  Resolved button link. 
	 */	
	public String getResolvedButtonLink()
	{
		return resolvedBtnLink;
	}
	
	public String getResolvedPolicyButtonLink()
	{
		return resolvedPolicyBtnLink;
	}

	public String getPolicy_heading() {
		return policy_heading;
	}

	public String getPolicy_text() {
		return policy_text;
	}

	public String getPolicy_alignment() {
		return policy_alignment;
	}

	public String getPolicy_linktarget() {
		return policy_linktarget;
	}

	public String getPolicy_linktext() {
		return policy_linktext;
	}

	public String getPolicy_resolvedButtonLink() {
		return policy_linkurl;
	}

	public String getPolicy_fileReference() {
		return policy_fileReference;
	}

	public String getPolicy_tabletFileReference() {
		return policy_tabletFileReference;
	}

	public String getPolicy_phoneFileReference() {
		return policy_phoneFileReference;
	}

	public String getPolicy_alt() {
		return policy_alt;
	}

	public String getPolicy_lazyload() {
		return policy_lazyload;
	}

	public String getPolicy_tagline() {
		return policy_tagline;
	}

	public String getPolicy_bgicon() {
		return policy_bgicon;
	}

	public Calendar getPageLastPublished() {
		return pageLastPublished;
	}
}