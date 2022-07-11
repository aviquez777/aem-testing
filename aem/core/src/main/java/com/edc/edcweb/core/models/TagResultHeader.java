package com.edc.edcweb.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import com.day.cq.wcm.api.Page;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 * 
 * @see ContentPolicyUtil
 * 
 * 
 * This class provides model support for the AEM Tag Result Header component.  The model is populated from English or French content policies.
 * 
 * The ContentPolicyUtil class is used to load the correct policy based upon the language of the current request.
 * 
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class TagResultHeader {

	@Inject
	private SlingHttpServletRequest request;

	@Inject
	private Page currentPage;
		
 	
	private String title;
	 
 	
	/**
	 * This method is responsible for initial assignment of model properties.
	 * 
	 * Initial values are loaded from the policy and mapped onto model properties.
	 *  
	 */
	@PostConstruct
	public void initModel() {
		ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(request, currentPage);
		if (contentPolicy != null) {
			
			ValueMap properties = contentPolicy.getProperties();
			title = properties.get(Constants.Properties.TITLE, String.class);
						
			 
		}		
	}
	 

// getter methods	
	
	public String getTitle() {
		return title;
	}

	 
}
