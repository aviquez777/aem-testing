package com.edc.edcweb.core.models;

import java.util.*;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.LinkResolver;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Reference;

/**
 * <h1>StickyNav</h1> Sling model to provide support for the StickyNav
 * component. initModel() @PostConstruct with component's main logic.
 *
 **/

@Model(adaptables = SlingHttpServletRequest.class)
public class StickyNav {

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Page currentPage;

    @Inject
    private Resource resource;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String text;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String text2;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String cta1type;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String linktext;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String linkurl;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String linktarget;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String modaltext1;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String modalname1;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String sectiontext1;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String sectiontarget1;

    private List<TextUrlLinks> linksList;

    private String navVersion = Constants.Properties.DEFAULT_NAV_VERSION;

    private List<TextUrlLinks> fiLinksList; 

    @Reference
    @Inject
    private SlingSettingsService slingSettingsService;

    private boolean isPublish;

    private ResourceResolver resourceResolver;

    private String pageUrl;

    @PostConstruct
    public void initModel() {
        // Get page url or alias to set links active
        resourceResolver = request.getResourceResolver(); 
        String pathWithHtml = LinkResolver.addHtmlExtension(currentPage.getPath());
        this.pageUrl = resourceResolver.map(request, pathWithHtml);
        // Get author/publish environment
        this.isPublish = false;
        Set<String> runModesList = this.slingSettingsService.getRunModes();
        if (runModesList != null && runModesList.contains(Constants.RUN_MODE_PUBLISH)) {
            this.isPublish = true;
        }
        // resource is injected would never be null
        this.linksList = populateLinkList(resource.getChild(Constants.Properties.ITEMS));
        ValueMap properties = resource.adaptTo(ValueMap.class);
        if (properties != null) {
            this.navVersion = properties.get(Constants.Properties.NAV_VERSION, Constants.Properties.DEFAULT_NAV_VERSION);
        }
        // Task #156263 Get links from policy for Financial Institutions navigation
        ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(this.request, this.currentPage);
        Resource policyResource = null;
        if (contentPolicy != null) {
            policyResource = contentPolicy.adaptTo(Resource.class);
        }
        Resource childResourceFiLinksList = null;
        if (policyResource != null) {
            childResourceFiLinksList = policyResource.getChild(Constants.Properties.ITEMS);
        }
        this.fiLinksList = populateLinkList(childResourceFiLinksList);
    }

    /**
     * Returns a List of Links
     *
     * @param  resource  a JCR resource.
     * @return List<TextUrlLinks> a list of Links
     * @see TextUrlLinks
     *
     */
    private List<TextUrlLinks> populateLinkList(Resource resource) {
        List<TextUrlLinks> links = new ArrayList<>();
        if (resource != null) {
            Iterator<Resource> resources = resource.listChildren();
            while (resources.hasNext()) {
                TextUrlLinks item = resources.next().adaptTo(TextUrlLinks.class);
                // Look for alias if url not blank
                if (StringUtils.isNotBlank(item.getLinkUrl())) {
                    String url = resourceResolver.map(request, item.getLinkUrl());
                    item.setLinkUrl(url);
                }
                links.add(item);
            }
        }
        return links;
    }

    /**
     * This method is used to return the List of links to display.
     *
     * @return List<TextUrlLinks> List of Links;
     */
    public List<TextUrlLinks> getLinks() {
        return linksList;
    }

    /**
     * This method is used to return the List of FI links to display.
     *
     * @return List<TextUrlLinks> List of Links;
     */
    public List<TextUrlLinks> getFiLinks() {
        return fiLinksList;
    }

    /**
     * This method is used to return the navigation version.
     *
     * @return String
     *
     */
    public String getNavVersion() {
        return navVersion;
    }

    /**
     * Get Text
     *
     * @return String
     *
     */
    public String getText() {
        return text;
    }

    /**
     * Get Text2
     *
     * @return String
     *
     */
    public String getText2() {
        return text2;
    }

    /**
     * Get Text
     *
     * @return String
     *
     */

    public String getCta1type() {
        return cta1type;
    }

    /**
     * Get Link Text
     *
     * @return String
     *
     */
    public String getLinktext() {
        return linktext;
    }

    /**
     * Get Link Url
     *
     * @return String
     *
     */
    public String getLinkurl() {
        return LinkResolver.addHtmlExtension(linkurl);
    }

    /**
     * Get Link Target
     *
     * @return String
     *
     */
    public String getLinktarget() {
        return linktarget;
    }

    /**
     * Get Modal Text
     *
     * @return String
     *
     */

    public String getModaltext1() {
        return modaltext1;
    }

    /**
     * Get Modal Text
     *
     * @return String
     *
     */
    public String getModalname1() {
        return modalname1;
    }

    /**
     * Get Section Text
     *
     * @return String
     *
     */

    public String getSectiontext1() {
        return sectiontext1;
    }

    /**
     * Get Section Target
     *
     * @return String
     *
     */
    public String getSectiontarget1() {
        return sectiontarget1;
    }

    /**
     * Get true if Publish mode
     *
     * @return String
     *
     */
    public boolean getIsPublish() {
        return isPublish;
    }

    public String getPageUrl() {
        return pageUrl;
    }
}