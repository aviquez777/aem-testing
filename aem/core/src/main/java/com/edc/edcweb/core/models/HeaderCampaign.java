package com.edc.edcweb.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.search.PageSearch;
import com.edc.edcweb.core.search.filters.PageNotIsHideInNavFilter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author ACN
 * @version 1.1.0
 * @since 1.0.0
 * <p>
 * <p>
 * This class is the model class for the Header component used by the EDC web site.
 */
@Model(adaptables = SlingHttpServletRequest.class, resourceType = "edc/components/structure/campaign/headerCampaign")
public class HeaderCampaign extends Header {
    /**
     * Populates the Model with values first from the cqdialog if null then
     * from the applicable ContentPolicy (based on current path language).
     */
    @PostConstruct
    @Override
    public void initModel() {
        Resource resource = request.getResource();
        String startingPage;
        Long maxLevels;
        Resource tempResource;
        ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(this.request, this.currentPage);

        if(contentPolicy != null) {
            ValueMap properties = contentPolicy.getProperties();

            startingPage = properties.get(Constants.Properties.STARTING_PAGE, "");
            maxLevels = properties.get(Constants.Properties.MAX_LEVELS, -1L);
            this.logoUrl = LinkResolver.addHtmlExtension(getStringFromResource(Constants.Properties.LOGO_URL, properties));
            this.logoImage = getStringFromResource(Constants.Properties.LOGO_IMAGE, properties);
            this.logoAltText = getStringFromResource(Constants.Properties.LOGO_ALT_TEXT, properties);
            this.logoTarget = getStringFromResource(Constants.Properties.LOGO_TARGET, properties);
            this.phoneNumber = getStringFromResource(Constants.Properties.PHONE_NUMBER, properties);
            this.menuTarget = getStringFromResource(Constants.Properties.MENU_TARGET, properties);
            this.skipText = getStringFromResource(Constants.Properties.MENU_SKIP, properties);

            setPages(maxLevels, startingPage);

            tempResource = resource.getChild(Constants.Properties.LINKS);

            if (tempResource == null) {
                Resource policyResource = contentPolicy.adaptTo(Resource.class);
                if (policyResource != null) {
                    tempResource = policyResource.getChild(Constants.Properties.LINKS);
                }
            }
            links = populateLinksModel(tempResource);
        }
    }

    private void setPages(Long maxLevels, String startingPage) {
        //---------------------------------------------------------------------
        PageSearch pageSearch = new PageSearch(maxLevels, new PageNotIsHideInNavFilter());
        //---------------------------------------------------------------------
        Resource startingPageResource = this.resolver.getResource(startingPage);
        Page page = null;
        if (startingPageResource != null) {
            page = startingPageResource.adaptTo(Page.class);
        }
        this.pages = pageSearch.searchPages(page);
    }

    /**
     * get resource from cq dialog if not found get it from policy
     */
    private String getStringFromResource(String attribute, ValueMap propertiesFromPolicy) {
        Resource tempResource = null;
        String myAttribute;

        tempResource = request.getResource().getChild(attribute);
        if (tempResource != null) {
            myAttribute = tempResource.adaptTo(String.class);
        } else {
            myAttribute = propertiesFromPolicy.get(attribute, String.class);
        }

        return myAttribute;
    }

    @Override
    protected List<TextUrlLinks> populateLinksModel(Resource resource) {
        List<TextUrlLinks> listOfLinks = new ArrayList<>();
        if (resource != null) {
            Iterator<Resource> resources = resource.listChildren();
            while (resources.hasNext()) {
                TextUrlLinks item = resources.next().adaptTo(TextUrlLinks.class);

                //add # only if Link is in the same page
                if(!item.getLinkUrl().contains("/")){
                    //adding target for data layer
                    item.setLinkTarget(item.getLinkUrl());
                    item.setLinkUrl("#" + item.getLinkUrl());
                }else{
                    item.setLinkTarget(item.getLinkUrl());
                }
                listOfLinks.add(item);
            }
        }
        return listOfLinks;
    }
}
