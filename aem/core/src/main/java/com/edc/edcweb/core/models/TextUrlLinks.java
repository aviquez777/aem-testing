package com.edc.edcweb.core.models;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import com.edc.edcweb.core.helpers.LinkResolver;

/**
 * @author Peter Crummey
 * @version 1.0.0
 * @since 1.0.0
 *
 *
 * Model to retrieve information for a link, properties being text, url and target (e.g., existing window, new window, etc.).
 *
 *
 */
@Model(adaptables = Resource.class)
public class TextUrlLinks
{

    @Inject
    @Named("linktext")
    @Optional
    private String linkText;

    @Inject
    @Named("linkurl")
    @Optional
    private String linkUrl;

    @Inject
    @Named("linktarget")
    @Optional
    private String linkTarget;

    @Inject
    @Named("displaytype")
    @Optional
    private String displaytype;

    @Inject
    @Named("target")
    @Optional
    private String target;

    /**
     * Get the link text.
     *
     */
    public String getLinkText() {
        return linkText;
    }

    /**
     * Get the resolved link URL. (Uses LinkResolver to resolve the link.)
     *
     */
    public String getLinkUrl() {
        return LinkResolver.addHtmlExtension(linkUrl);
    }

    /**
     * Get the link's target (e.g., existing window, new window, etc.).
     *
     */
    public String getLinkTarget() {
        return linkTarget;
    }

    /**
     * Get the link's styling.
     *
     */
    public String getDisplaytype() {
        return displaytype;
    }

    /**
     * Get target for on page navigation.
     *
     */
    public String getTarget() {
        return target;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public void setLinkTarget(String linkTarget) {
        this.linkTarget = linkTarget;
    }

    public void setDisplaytype(String displaytype) {
        this.displaytype = displaytype;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
