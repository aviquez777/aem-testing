package com.edc.edcweb.core.models;

import javax.inject.Inject;
import javax.inject.Named;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

@Model(adaptables = Resource.class)
public class SuccessStoryPage {
    @Inject
    @Optional
    @Named("pageTitle")
    private String pageTitle;

    @Inject
    @Optional
    @Named("jcr:description")
    private String pageDescription;

    @Inject
    @Optional
    @Named("tagline")
    private String tagline;

    @Inject
    @Named("tabletFileReference")
    @Optional
    private String tabletFileReference;

    @Inject
    @Named("imagealignment")
    @Optional
    private String imageAlignment;

    @Inject
    @Named("imagealttext")
    @Optional
    private String imagealttext;

    private String path;

    public void setPath(String path) {
        this.path = path;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public String getPageDescription() {
        return pageDescription;
    }

    public String getTagline() {
        return tagline;
    }

    public String getPath() {
        return this.path;
    }

    public String getTabletFileReference() {
        return tabletFileReference;
    }

    public String getImageAlignment() {
        return imageAlignment;
    }

    public String getImagealttext() {
        return imagealttext;
    }
}
