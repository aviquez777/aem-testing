
package com.edc.edcweb.core.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.Request;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.models.OnPageNavigationLinks;


/**
 * Links Collection to be used for retrieving /apps/edc/content/onpagenavigation component properties
 * Uses Sling Model com.edc.edcweb.core.models.OnPageNavigationLinks
 */

public class OnPageNavigation extends WCMUsePojo {

	private List<OnPageNavigationLinks> links;
	private String linksSectionTitle;
	private String introText;

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void activate() throws Exception {
		/* get the 'items' nodes from the content */
		Resource resource = getResource().getChild(Constants.Properties.ITEMS);
		Page currentPage = Request.adaptToPage(getRequest());
        ValueMap properties = getProperties();

		this.links = populateModel(resource);
		this.introText = properties.get(Constants.Properties.INTRO_TEXT, String.class);

		populateFromPolicy(getRequest(), currentPage);
	}

	public List<OnPageNavigationLinks> populateModel(Resource resource)
	{
		List<OnPageNavigationLinks> itemLinks = new ArrayList<>();
		if (resource != null)
		{
			log.debug("Resource is " + resource.getPath());

			//get all the child elements of 'items' i.e. items0, items1, items2
			Iterator<Resource> linkResources = resource.listChildren();

			while (linkResources.hasNext())
			{
				OnPageNavigationLinks link = linkResources.next().adaptTo(OnPageNavigationLinks.class);
				itemLinks.add(link);
			}
		}
		return itemLinks;
	}

	private void populateFromPolicy(SlingHttpServletRequest request, Page currentPage)
	{
		log.debug("PopulateFromPolicy for Page Navigation");
		ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(request, currentPage);

		if (contentPolicy != null) {
			log.debug("contentPolicy != null");
			ValueMap policies = contentPolicy.getProperties();
			this.linksSectionTitle = policies.get(Constants.Properties.LINKS_SECCION_TITLE, String.class);
		}
	}

    public String getIntroText() {
        return this.introText;
    }

    public String getLinksSectionTitle() {
        return this.linksSectionTitle;
    }

	public List<OnPageNavigationLinks> getLinks()
	{
		return this.links;
	}
}