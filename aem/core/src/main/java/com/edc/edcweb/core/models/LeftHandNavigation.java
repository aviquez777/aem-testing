package com.edc.edcweb.core.models;

import java.util.ArrayList;
import java.util.List;

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
import com.edc.edcweb.core.search.PageList;
import com.edc.edcweb.core.search.PageSearch;
import com.edc.edcweb.core.search.filters.PageNotIsHideInNavFilter;

/**
 *
 *
 * This class is the model class for the Left-hand navigation component used by
 * the EDC web site.
 *
 *
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class LeftHandNavigation {

    private static final Logger log = LoggerFactory.getLogger(LeftHandNavigation.class);
    @Inject
    @Source("sling-object")
    private ResourceResolver resolver;

    @Inject
    private SlingHttpServletRequest request;
    @Inject
    private Page currentPage;

    private List<PageList> pages = new ArrayList<>();
    private String navTitle;
    private String target;
    private boolean noBottomMargin;

    /**
     * Populates the Model with values from the applicable ContentPolicy (based
     * on current path language).
     *
     */
    @PostConstruct
    public void initModel() {
        String startingPage = "";
        //---------------------------------------------------------------------
        ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(this.request, this.currentPage);
        if (contentPolicy != null) {
            ValueMap properties = contentPolicy.getProperties();
            startingPage = properties.get(Constants.Properties.STARTING_PAGE, "");
            navTitle = properties.get(Constants.Properties.NAV_TITLE, "");
            target = properties.get(Constants.Properties.MENU_TARGET, "");
            noBottomMargin = properties.get(Constants.Properties.NO_BOTTOM_MARGIN, false);
        }

        //---------------------------------------------------------------------
        PageSearch pageSearch = new PageSearch(2, new PageNotIsHideInNavFilter());
        //---------------------------------------------------------------------
        Resource startingPageResource = this.resolver.getResource(startingPage);
        Page page = null;

        if (startingPageResource != null) {
            Resource startingParentPage = startingPageResource.getParent();

            if (startingParentPage != null) {
                page = startingParentPage.adaptTo(Page.class);
            }
        }

        if (page != null) {
            this.pages = pageSearch.searchPages(page);
        }
    }

    /**
     * Get navigation title.
     * @return String
     */
    public String getNavTitle() {
        return navTitle;
    }

    /**
     * Get navigation target.
     * @return String
     */
    public String getTarget() {
        return target;
    }

    /**
     * Get the list of pages in the navigation menu.
     *
     * @return List<PageList>
     */
    public List<PageList> getPages() {
        return this.pages;
    }

    /**
     * Get the no bottom margin checkbox.
     *
     * @return boolean
     */
    public boolean getNoBottomMargin() {
        return this.noBottomMargin;
    }
}
