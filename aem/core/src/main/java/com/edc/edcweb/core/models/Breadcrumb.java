package com.edc.edcweb.core.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.CountryInfoHelper;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.helpers.TagHelper;
import com.edc.edcweb.core.helpers.Constants;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 * 
 * 
 *        Backing Java code Breadcrumb component.
 * 
 */

@Model(adaptables = SlingHttpServletRequest.class)
public class Breadcrumb {

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Page currentPage;

    private boolean hasPolicy = false;

    private boolean singleLink;

    private List<Map<String, String>> breadcrumbList = new ArrayList<>();

    private String pageType =  null;

    @PostConstruct
    public void initModel() {
        ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(this.request, this.currentPage);
        // Check first for Coutry tag and page
        String countrytag = CountryInfoHelper.getPageRegionTag(currentPage, request);
        String currentPath = currentPage.getPath();
        if (countrytag != null
                && currentPath.contains(Constants.Paths.COUNTRYINFO_COUNTRYROOTPAGE_EN + "/")) {
            TagManager tagMgr = request.getResourceResolver().adaptTo(TagManager.class);
            if (tagMgr != null) {
                addCrumb(LinkResolver.addHtmlExtension(currentPath), TagHelper.getLocalizedTagTitle(tagMgr, currentPage, countrytag));
            }
        } else {
            // Use the path to figure out the bread crumb content
            for (String checkPath : Constants.getBreadcrumbPaths()) {
                if (currentPage.getPath().contains("/" + checkPath + "/") || currentPage.getName().equals(checkPath)) {
                    // Check if page is a "Parent Page"
                    Page parentPage = currentPage;
                    if (!currentPage.getName().equals(checkPath)) {
                        parentPage = getParentPage(currentPage, checkPath);
                    }
                    // if we found the parent page, add it to the breadcrumb
                    if (parentPage != null) {
                        addCrumb(parentPage.getPath(), getNavigationTitle(parentPage));
                    }
                    // Save the current page type for later
                    pageType = checkPath;
                    break;
                }
            }
        }
        // If we have policy, add Info from policy
        if (contentPolicy != null) {
            ValueMap properties = contentPolicy.getProperties();
            // Get the values for "root link"
            String linkUrl = properties.get(Constants.Properties.BREADCRUMB_LINKURL, String.class);
            // Get the value for "single link" checkbox
            this.singleLink = Boolean.parseBoolean(properties.get(Constants.Properties.BREADCRUMB_SINGLELINK, String.class));
            //if no bread crumbs, add current page as fallback if no pages are found
            if(breadcrumbList.isEmpty() && !this.singleLink) {
                addCrumb(currentPage.getPath() + getSelectorsInCurrentPageURL(), getNavigationTitle(currentPage));
            }
            if (linkUrl != null && !linkUrl.isEmpty()) {
                addCrumb(linkUrl, properties.get(Constants.Properties.BREADCRUMB_LINKTEXT, String.class));
            }
            hasPolicy = true;
        } else {
            // no policy defined, if no bread crumbs, add current page as fallback if no pages are found
            if(breadcrumbList.isEmpty()) {
                addCrumb(currentPage.getPath() + getSelectorsInCurrentPageURL(), getNavigationTitle(currentPage));
                Page parentPage = currentPage.getParent();
                //do not display parent if it is the language node
                if (parentPage != null && !parentPage.getName().equalsIgnoreCase(currentPage.getLanguage().getLanguage())) {
                    addCrumb(parentPage.getPath(), getNavigationTitle(parentPage));
                }
            }
        }
    }

    /**
     * getParentPage recursively find the requested page
     * 
     * @param page       Page Object Start with current page
     * @param parentName page we are looking for
     * @return desired page if found, null otherwise
     */
    private Page getParentPage(Page page, String parentName) {
        addCrumb(page.getPath(), getNavigationTitle(page));
        Page parentPage = page.getParent();
        if (parentPage != null) {
            if (parentPage.getName().equals(parentName)) {
                return parentPage;
            } else {
                return getParentPage(parentPage, parentName);
            }
        } else {
            // page not found clear the breadcrumb list
            breadcrumbList.clear();
            return null;
        }
    }

    /**
     * addCrumb add link url and link text to the breadcrumb list
     * 
     * @param url      String path of the page
     * @param linkText text to display on link
     */
    private void addCrumb(String url, String linkText) {
        Map<String, String> breadcrumb = new HashMap<>();
        breadcrumb.put(LinkResolver.addHtmlExtension(url), linkText);
        breadcrumbList.add(breadcrumb);
    }

    /**
     * getNavigationTitle returns the p
     * 
     * @param page page to look the Navigation title
     * @return the Navigation title, page name if not Nav title
     */
    private String getNavigationTitle(Page page) {
        String pageTitle = page.getNavigationTitle();
        if (pageTitle == null) {
            pageTitle = page.getName();
        }
        return pageTitle;
    }
    
    private String getSelectorsInCurrentPageURL() {
    	String result = "";
        String selectors = request.getRequestPathInfo().getSelectorString();
        if (selectors != null && !selectors.isEmpty()) {
        	result =result+"." + selectors;
        }

    	return result;
    }

    /**
     * getBreadcrumbList get the list of link url / link text to display bread crumb
     * path
     * 
     * @return List<Map<String, String>> link url / link text
     */
    public List<Map<String, String>> getBreadcrumbList() {
        // Since we are creating the list from the children up, we must reverse for
        // proper order
        Collections.reverse(breadcrumbList);
        // check if we need to skip second node (Solutions page)
        int listSize = breadcrumbList.size();
        if (listSize > 2) {
            // Handle special cases
            switch (pageType) {
            // Solutions and Country info pages are a special case, should skip one node when necessary.
                case "solutions" :
                case "country-info" :
                    breadcrumbList.remove(1);
                break;
                // For events we just want the first two levels of the bread crumb
                case "events" : 
                    breadcrumbList = breadcrumbList.subList(0, 2);
                    break;

                default:
                    break;
            }
        }
        // Pages under Financial institutions remove second item
        if (hasPolicy && listSize > 1 && StringUtils.equals(pageType,"financial-institutions")) {
            breadcrumbList.remove(1);
        }
        return breadcrumbList;
    }

    /**
     * Returns whether the policy has been set
     * @return true if component has policy, false otherwise
     * 
     */
    public boolean getHasPolicy() {
        return hasPolicy;
    }

    public boolean getSingleLink() {
        return this.singleLink;
    }
}