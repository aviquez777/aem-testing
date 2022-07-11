package com.edc.edcportal.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import com.edc.edcportal.core.helpers.LinkResolver;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Roberto Ramos
 * @version 1.0.0
 * @since 1.0.0
 * <p>
 * Model to retrieve information for a link, properties being text, url and target (e.g., existing window, new window, etc.).
 */
@Model(adaptables = Resource.class)
public class TextUrlLinks {

    @Inject
    @Named("linkText")
    @Optional
    private String linkText;

    @Inject
    @Named("linkUrl")
    @Optional
    private String linkUrl;

    @Inject
    @Named("linkTarget")
    @Optional
    private String linkTarget;

    /**
     * Get the link text.
     *
     * @return Link Text
     */
    public String getLinkText() {
        return this.linkText;
    }

    /**
     * Get the resolved link URL. (Uses LinkResolver to resolve the link.)
     *
     * @return Link Url
     */
    public String getLinkUrl(String baseContent) {
        return LinkResolver.addHtmlExtension(this.linkUrl, baseContent);
    }

    /**
     * Get the link's target (e.g., existing window, new window, etc.).
     *
     * @return Link target
     */
    public String getLinkTarget() {
        return this.linkTarget;
    }
}
