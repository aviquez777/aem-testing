package com.edc.edcweb.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.LinkResolver;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.constants.ConstantsAccessibility;
import java.util.ArrayList;
import java.util.List;

@Model(adaptables = SlingHttpServletRequest.class)
public class MultipleVideoLineUp {
    private static final Logger log = LoggerFactory.getLogger(MultipleVideoLineUp.class);

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Page currentPage;

    @ScriptVariable
    private ValueMap properties;

    // Model
    private String keyline;
    private String title;
    private String subtitle;
    private String relatedPage1;
    private String relatedPage2;
    private String relatedPage3;
    private String viewMoreText;
    private String viewMoreLink;
    private String viewMoreTarget;
    private String policy_videoplaybtn;

    private List<SuccessStoryPage> pages;

    @PostConstruct
    public void initModel() {
        this.populateModel();
        this.populateFromPolicy();
    }

    private List<SuccessStoryPage> setPages(List<String> searchPathsList) {
        List<SuccessStoryPage> pages = new ArrayList<>();

        for (int i = 0; i < searchPathsList.size(); i++) {
            String pagePath = searchPathsList.get(i);
            ResourceResolver resolver = this.request.getResourceResolver();
            if (StringUtils.startsWith(pagePath, "/")) {
                Resource resource = resolver.getResource(pagePath);
                if (resource != null) {
                    Page page = resource.adaptTo(Page.class);
                    if (page != null) {
                        SuccessStoryPage successStoryPage = page.getContentResource().adaptTo(SuccessStoryPage.class);
                        if (successStoryPage != null) {
                            successStoryPage.setPath(LinkResolver.addHtmlExtension(page.getPath()));
                            pages.add(successStoryPage);
                        }
                    }
                }
            }
        }

        return pages;
    }

    private void populateFromPolicy() {
        ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(this.request, this.currentPage);

        if (contentPolicy != null) {
            ValueMap policy_properties = contentPolicy.getProperties();

            // Accessibility policies
            this.policy_videoplaybtn = policy_properties.get(ConstantsAccessibility.CAProperties.ACCESSIBILITY_VIDEOPLAYBTN, String.class);
        }
    }

    private void populateModel() {
        List<String> searchPathsList = new ArrayList<>();

        this.keyline = properties.get(Constants.Properties.KEYLINE, String.class);
        this.title = properties.get(Constants.Properties.TITLE, String.class);
        this.subtitle = properties.get(Constants.Properties.SUBTITLE, String.class);
        this.relatedPage1 = properties.get(Constants.Properties.RELATEDPAGE1, String.class);
        this.relatedPage2 = properties.get(Constants.Properties.RELATEDPAGE2, String.class);
        this.relatedPage3 = properties.get(Constants.Properties.RELATEDPAGE3, String.class);
        this.viewMoreText = properties.get(Constants.Properties.VIEWMORETEXT, String.class);
        this.viewMoreLink = LinkResolver.addHtmlExtension(properties.get(Constants.Properties.VIEWMORELINK, String.class));
        this.viewMoreTarget = properties.get(Constants.Properties.VIEWMORETARGET, String.class);

        // Add paths
        searchPathsList.add(this.relatedPage1);
        searchPathsList.add(this.relatedPage2);
        searchPathsList.add(this.relatedPage3);

        // Set Pages
        this.pages = this.setPages(searchPathsList);
    }

    public String getKeyline() {
        return this.keyline;
    }

    public String getTitle() {
        return this.title;
    }

    public String getSubtitle() {
        return this.subtitle;
    }

    public List<SuccessStoryPage> getPages() {
        return this.pages;
    }

    public String getViewMoreText() {
        return this.viewMoreText;
    }

    public String getViewMoreLink() {
        return this.viewMoreLink;
    }

    public String getViewMoreTarget() {
        return this.viewMoreTarget;
    }

    public String getPolicy_videoplaybtn() {
        return policy_videoplaybtn;
    }
}