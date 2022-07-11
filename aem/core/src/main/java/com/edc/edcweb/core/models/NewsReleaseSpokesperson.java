package com.edc.edcweb.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.ResourceResolverHelper;

/**
 * NewsReleaseSpokesperson Gets the title from profile for display. Looks for
 * the first article author component and gets the author's profile for display.
 */
@Model(adaptables = SlingHttpServletRequest.class)

public class NewsReleaseSpokesperson {

    private static final String ARTICLE_AUTHOR_CONTENT_TYPE = "edc/components/content/article/articleauthors";

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private ResourceResolver resourceResolver;

    @Inject
    private Page currentPage;

    private String title;

    private AuthorBio spokeperson;

    /**
     * Gets the title from profile for display. Looks for the first article author
     * component and gets the author's profile for display.
     */
    @PostConstruct
    public void initModel() {
        ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(request, currentPage);
        if (contentPolicy != null) {
            ValueMap properties = contentPolicy.getProperties();
            title = properties.get(Constants.Properties.TITLE, String.class);
        }

        Resource getAuthorRes = ResourceResolverHelper.getResourceByType(currentPage, ARTICLE_AUTHOR_CONTENT_TYPE);
        if (getAuthorRes != null) {
            ValueMap properties = getAuthorRes.getValueMap();
            String primaryAuthorPath = properties.get(Constants.Properties.PRIMARY_AUTHOR_PATH, String.class);

            if (primaryAuthorPath != null) {
                // populate the primary author details
                Resource authorResource = resourceResolver
                        .getResource(primaryAuthorPath + Constants.Properties.AUTHOR_PATH_SUFFIX);

                if (authorResource != null) {
                    spokeperson = authorResource.adaptTo(AuthorBio.class);
                }
            }
        }
    }

    /**
     * getTitle Pulls title from component's policy.
     * 
     * @return string the title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * getSpokeperson Gets first article author component and gets the author's
     * profile for display.
     * 
     * @return AuthorBio the author's profile for display.
     */
    public AuthorBio getSpokeperson() {
        return spokeperson;
    }

}
