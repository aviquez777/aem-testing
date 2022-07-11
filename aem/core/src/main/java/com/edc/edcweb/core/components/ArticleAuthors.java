package com.edc.edcweb.core.components;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import com.adobe.cq.sightly.WCMUsePojo;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.models.AuthorBio;

/**
 * @author Scott Ross
 * @version 1.0.0
 * @since 1.0.0
 *
 *
 * Backing Java code for the Author Bios component used by the EDC web site.
 *
 *
 */
public class ArticleAuthors extends WCMUsePojo {

    private String primaryAuthorPath;
    private String primaryShowBio;
    private String primaryShortBio;
    private String secondaryAuthorPath;
    private String secondaryShowBio;
    private String secondaryShortBio;
    private List<AuthorBio> biographies = new ArrayList<>();

    /**
     * Populates the Author Bios based on the resource paths provided for the bio pages
     *
     */
    @Override
    public void activate() throws Exception {
        ValueMap properties = getResource().adaptTo(ValueMap.class);
        if(properties != null) {
            this.primaryAuthorPath = properties.get(Constants.Properties.PRIMARY_AUTHOR_PATH, "");
            this.primaryShowBio = properties.get(Constants.Properties.PRIMARY_SHOW_BIOGRAPHY, "");
            this.primaryShortBio = properties.get(Constants.Properties.PRIMARY_SHORT_BIOGRAPHY, "");

            this.secondaryAuthorPath = properties.get(Constants.Properties.SECONDARY_AUTHOR_PATH, "");
            this.secondaryShowBio = properties.get(Constants.Properties.SECONDARY_SHOW_BIOGRAPHY, "");
            this.secondaryShortBio = properties.get(Constants.Properties.SECONDARY_SHORT_BIOGRAPHY, "");

            // populate the primary author details
            ResourceResolver resolver = getResourceResolver();
            Resource authorResource = resolver.getResource(primaryAuthorPath + Constants.Properties.AUTHOR_PATH_SUFFIX);

            if (authorResource != null) {
                AuthorBio author = authorResource.adaptTo(AuthorBio.class);
                biographies.add(author);
            }

            // and the secondary author details
            authorResource = resolver.getResource(secondaryAuthorPath  + Constants.Properties.AUTHOR_PATH_SUFFIX);

            if (authorResource != null) {
                AuthorBio author = authorResource.adaptTo(AuthorBio.class);
                biographies.add(author);
            }
        }
    }

    public String primaryAuthorPath() {
        return LinkResolver.addHtmlExtension(primaryAuthorPath);
    }

    public String getPrimaryShowBio() {
        return primaryShowBio;
    }

    public String getPrimaryShortBio() {
        return primaryShortBio;
    }

    public String secondaryAuthorPath() {
        return LinkResolver.addHtmlExtension(secondaryAuthorPath);
    }

    public String getSecondaryShowBio() {
        return secondaryShowBio;
    }

    public String getSecondaryShortBio() {
        return secondaryShortBio;
    }

    public List<AuthorBio> getBiographies() {
        return new ArrayList<>(this.biographies);
    }
}