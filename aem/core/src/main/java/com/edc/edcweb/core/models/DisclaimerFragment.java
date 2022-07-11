package com.edc.edcweb.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.TagHelper;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 *
 * @see ContentPolicyUtil
 *
 * This class provides model support for the Disclaimer Fragment
 *
 */
@Model(adaptables = SlingHttpServletRequest.class) 
public class DisclaimerFragment 
{
	private static final Logger log= LoggerFactory.getLogger(DisclaimerFragment.class);

	@Inject
	private SlingHttpServletRequest request;

	@Inject @Source("sling-object")
    private ResourceResolver resourceResolver;
	
	@Inject
	private Page currentPage;
		
// model  
	private boolean isValid;
	private String text; 
	   
	 /**
	 * This method is responsible for initial assignment of model properties.
	 * 
	 * Initial values are loaded from the policy and mapped onto model properties.
	 *  
	 */
	@PostConstruct
	public void initModel() {
		log.debug("Disclaimer Fragment constructor  ");
		this.isValid=false;
		 		
		/**
		 * populate the model members
		 */
		
		log.info("Disclaimer model initialization for page  [{}]", currentPage.getPath() );
		this.populateFromPolicy();
		this.validate();
		log.info("this.isValid after initialization  [{}]", this.isValid );				
					
	}
	
	private void validate() {
	  		
		if ( TagHelper.isPageTagged(this.currentPage, Constants.Paths.CONTENT_TYPE_WEEKLYCOMMENTARY ) || TagHelper.isPageTagged(this.currentPage, Constants.Paths.CONTENT_TYPE_WEBINAR) || TagHelper.isPageTagged(this.currentPage, Constants.Paths.EVENT_TYPE_TAGS_WEBINAR))
		{
			this.isValid= true;
		}
		else
		{
			this.isValid= false;
		}
			 
	}

	private void populateFromPolicy( )
	{		 
		log.debug("PopulateFromPolicy ");
		ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(this.request, this.currentPage);
		if (contentPolicy != null) {
			log.debug("contentPolicy != null ");
			ValueMap myproperties = contentPolicy.getProperties();
		 
			this.text = myproperties.get(Constants.Properties.TEXT, String.class);		
		}		
		 		 
	}
	
	public boolean getIsValid() {
		log.debug("getIsValid {}",  this.isValid );
		return isValid;
	}
	   
	public String getText() {
		log.debug("getText [{}]", this.text); 
		return this.text;
	}
	  

}
