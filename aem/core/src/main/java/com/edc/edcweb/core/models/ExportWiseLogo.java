package com.edc.edcweb.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import com.day.cq.wcm.api.Page;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.LinkResolver;

/**
 * @author Scott Ross
 * @version 1.0.0
 * @since 1.0.0
 * 
 * @see ContentPolicyUtil
 *  
 * This class provides model support for the AEM ExportWise Image component.  The model is populated from English or French content policies.
 * 
 * The ContentPolicyUtil class is used to load the correct policy based upon the language of the current request.
 * 
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class ExportWiseLogo {
	private static final Logger log=LoggerFactory.getLogger(ExportWiseLogo.class);

	@Inject
	private SlingHttpServletRequest request;

	@Inject
	private Page currentPage;
	
// logo and link properties	
	
	private String logoReference;
	private String logoAlt;
	private String linkUrl;
	private String linkText;	
	private String linkTarget;
		
// paths
	
	private List<String> paths = new ArrayList<>();

// 	show / hide properties
	
	private Boolean showOnPage;		
	
	/**
	 * This method is responsible for initial assignment of model properties.
	 * 
	 * Initial values are loaded from the policy and mapped onto model properties.
	 *  
	 */
	@PostConstruct
	public void initModel() {
		
		log.debug("ExportWiseLogo :: initModel");
		
		ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(request, currentPage);
		if (contentPolicy != null) {
			
			ValueMap properties = contentPolicy.getProperties();

			logoReference = properties.get(Constants.Properties.LOGO, String.class);
			logoAlt = properties.get(Constants.Properties.ALT_TEXT, String.class);
			linkText = properties.get(Constants.Properties.LINK_TEXT, String.class);
			linkUrl = LinkResolver.addHtmlExtension(properties.get(Constants.Properties.LINK_URL, String.class));	
			linkTarget = properties.get(Constants.Properties.LINK_TARGET, String.class);
			showOnPage = false;
			paths = new ArrayList<>();			
			
			Resource resource = contentPolicy.adaptTo(Resource.class);
			if(resource != null)
			{
				paths = populatePaths ( resource.getChild("rules") );
			}
			
			log.debug("ExportWise Logo - paths found: " + paths.size());
					
			// set showOnPage based on the current page and provided paths
			String pagePath = currentPage.getPath().toLowerCase();
			
			log.debug("Looking for path matches for path: " + pagePath);
			
			Iterator<String> pathIterator = paths.iterator();
			while (pathIterator.hasNext()) {			
				String pathToTest = pathIterator.next();
				log.debug("inspecting path: " + pathToTest);
				if ( pagePath.indexOf(pathToTest) > -1 ) {
					this.showOnPage = true;
					log.debug("found matching path at: " + pathToTest);
					break;
				}		
			}
		} else {
			log.debug("ExportWiseLogo - No ExportWise Logo policy found.");	
		}		
	}

	
	/**
	 * Returns a List of Paths 
	 * <p>
	 * This method translates a series of navigation related nodes defined in the content policy to a collection of
	 * links which can more readily be written out to the page. 
	 *
	 * @param  resource  a JCR resource corresponding with the content policy used to satisfy the current request.
	 * @return List<String> a list of path Strings
	 */	
	public List<String> populatePaths(Resource resource) {
		List<String> listOfPaths = new ArrayList<>();
		if (resource != null) {							
			Iterator<Resource> resources = resource.listChildren();
			while (resources.hasNext()) {			
				Path item = resources.next().adaptTo(Path.class);
				if (item != null) {
					listOfPaths.add(item.getPath().toLowerCase());
					log.error("added path " + item.getPath());
				}
			}
		}
		return listOfPaths;
	}

	
// getter methods	
	

	public String getLogoReference() {
		return logoReference;
	}


	public String getLogoAlt() {
		return logoAlt;
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


	public Boolean getShowOnPage() {
		return showOnPage;
	}
}
