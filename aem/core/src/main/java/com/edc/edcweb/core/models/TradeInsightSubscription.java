package com.edc.edcweb.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 



/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 *
 * @see ContentPolicyUtil
 * @see TextUrlLinks
 *
 *
 * This class provides model support for the TradeInsightSubscription
 *
 */
@Model(adaptables = SlingHttpServletRequest.class) 
public class TradeInsightSubscription 
{
	private static final Logger log= LoggerFactory.getLogger(TradeInsightSubscription.class);

	@Inject
	private SlingHttpServletRequest request;

	@Inject @Source("sling-object")
    private ResourceResolver resourceResolver;
	
	@Inject
	private Page currentPage;
		
// model  
	/*
	 * EDC logo, cookie disclaimer, title, and grey text within the email field must be centrally managed.
(LJL) +cookie disclaimer link +cta text +success text +success title.

	 */
	private String ctaText;
	private String successMessage;
	private String logoImage;
	private String cookieDisclaimer;
	private String cookieLabel;
	private String cookiePath;
	private String title;
	private String emailEmptyLabel;
	
	
	private ValueMap properties;
	 
	 
	 
	/**
	 * This method is responsible for initial assignment of model properties.
	 * 
	 * Initial values are loaded from the policy and mapped onto model properties.
	 *  
	 */
	 
	
	@PostConstruct
	public void initModel() {
		log.debug("TradeInsight Subscription constructor  ");
	 
		
		
		/**
		 * populate the model members
		 */
	 			
		this.properties = currentPage.getProperties();
		
		this.populateFromPolicy();
		this.populateModel();
						
					
	}
	
	private void populateFromPolicy( )
	{
		 
		log.debug("populateFromPolicy ");
		ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(this.request, this.currentPage);
		if (contentPolicy != null) {
			log.debug("contentPolicy != null ");
			ValueMap myproperties = contentPolicy.getProperties();
			 			
			this.ctaText = myproperties.get(Constants.Properties.TISUB_CTATEXT, String.class);
			this.successMessage = myproperties.get(Constants.Properties.TISUB_SUCCESSMESSAGE, String.class);
			this.logoImage = myproperties.get(Constants.Properties.TISUB_LOGOIMAGE, String.class);
			this.cookieDisclaimer = myproperties.get(Constants.Properties.TISUB_COOKIEDISCLAIMER, String.class);
			this.cookieLabel = myproperties.get(Constants.Properties.TISUB_COOKIELABEL, String.class);
			this.cookiePath = myproperties.get(Constants.Properties.TISUB_COOKIEPATH, String.class);
			this.title = myproperties.get(Constants.Properties.TISUB_TITLE, String.class);
			this.emailEmptyLabel = myproperties.get(Constants.Properties.TISUB_EMAILEMPTYLABEL, String.class);
			
		}	
		
		
		 
		 
	}
	
	private void populateModel() {
		
		/**
		 *  - check that this has the property defined or its not in a serie.
		 */
		log.debug("populateModel");
		 
					
	}
	
	 	
	public String getCtaText() {
		log.debug("getCtaText [{}]", this.ctaText); 
		return this.ctaText;
	}
	
	public String getSuccessMessage() {
		log.debug("getsuccessMessage [{}]", this.successMessage); 
		return this.successMessage;
	}
	
	public String getLogoImage() {
		log.debug("getlogoImage [{}]", this.logoImage); 
		return this.logoImage;
	}
	
	public String getCookieDisclaimer() {
		log.debug("getcookieDisclaimer [{}]", this.cookieDisclaimer); 
		return this.cookieDisclaimer;
	}
	
	public String getCookieLabel() {
		log.debug("getcookieLabel [{}]", this.cookieLabel); 
		return this.cookieLabel;
	}
	
	public String getCookiePath() {
		log.debug("getcookiePath [{}]", this.cookiePath); 
		return this.cookiePath;
	}
	
	public String getTitle() {
		log.debug("gettitle [{}]", this.title); 
		return this.title;
	}
	
	public String getEmailEmptyLabel() {
		log.debug("getemailEmptyLabel [{}]", this.emailEmptyLabel); 
		return this.emailEmptyLabel;
	}
	
	 
 
	
	 


}
