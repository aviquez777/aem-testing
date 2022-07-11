package com.edc.edcportal.core.models;

import com.day.cq.commons.LanguageUtil;
import com.edc.edcportal.core.helpers.Constants;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.edc.edcportal.core.services.MyEDCConfigSystemService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Roberto Ramos
 * @version 1.0.0
 * @since 1.0.0
 *
 *        This class extends
 *        com.adobe.cq.wcm.core.components.models.impl.v1.PageImpl.
 *
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class Page {
    private static final Logger log = LoggerFactory.getLogger(Page.class);

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private MyEDCConfigSystemService myEDCConfigSystemService;

    private ValueMap properties;
    private String canonical;

    // path to present as reference, the page path less the /content/edcportal part
    // of it.
    private String referenceURL;
    private String headline;
    private String published;
    private String modified;
    private String description;
    private String pageLang;

    @PostConstruct
    protected void init() {
        if (log.isDebugEnabled()) {
            log.debug("Page PostConstruct ");
        }

        this.properties = this.getProperties();
        this.processCanonicalPath();
        this.setReferenceURL();
        this.setHeadline();
        this.setDescription();
        this.setModified();
        this.setPublished();
        this.setPageLang();
    }

    /**
     * processCanonicalPath()
     *
     * Called to process the canonical path.
     */
    private void processCanonicalPath() {
        this.canonical = properties.get(Constants.Properties.CANONICAL, String.class);

        if (this.canonical == null) {
            this.canonical = "";
        }

        // ---------------------------------------------------------------------
        // remove "/content" from url
        // ---------------------------------------------------------------------
        if (this.canonical.startsWith(Constants.Paths.CONTENT)) {
            this.canonical = this.canonical.replaceFirst(Constants.Paths.CONTENT, "");
            int i = this.canonical.indexOf('/', 1);

            if (i > 0) {
                this.canonical = this.canonical.substring(i);
            }
        }

        // ---------------------------------------------------------------------
        // add extension to url
        // ---------------------------------------------------------------------
        if (!this.canonical.equals("") && !this.canonical.contains(".")) {
            this.canonical += Constants.HTML_EXTENSION;
        }
    }

    private void setReferenceURL() {
        if (log.isDebugEnabled()) {
            log.debug("setReferenceURL");
        }

        if (this.request != null) {
            String currentPath = this.request.getRequestURL().toString().toLowerCase();
            this.referenceURL = currentPath.replaceFirst(Constants.Paths.CONTENT_EDCPORTAL, "");
        }
    }

    private void setHeadline() {
        String pageTitle = properties.get(Constants.Properties.PAGE_TITLE, String.class);
        String navTitle = properties.get(Constants.Properties.NAV_TITLE, String.class);
        String jcrTitle = properties.get(Constants.Properties.JCR_TITLE, String.class);

        if (pageTitle != null) {
            this.headline = pageTitle;
        } else if (navTitle != null) {
            this.headline = navTitle;
        } else {
            this.headline = jcrTitle;
        }
    }

    private void setDescription() {
        String pageDescription = properties.get(Constants.Properties.JCR_DESCRIPTION, String.class);

        this.description = pageDescription != null ? pageDescription : "";
    }

    private void setPublished() {
        this.published = properties.get(Constants.Properties.LAST_REPLICATED, String.class);

        if (null == this.published) {
            this.published = this.modified;
        }
    }

    private void setModified() {
        this.modified = properties.get(Constants.Properties.LAST_MODIFIED, String.class);
    }

    private void setPageLang() {
        this.pageLang = LanguageUtil.getLanguageRoot(this.request.getResource().getPath());
        // Task 22143 squid:S2221 Will return null no error
        if (this.pageLang == null) {
            this.pageLang = "EN";
        }
    }

    /**
     * Get Canonical Url
     *
     * @return returns the canonical url generated from page 'canonical' property
     *         Based on business rule
     *
     */
    public String getCanonical() {
        return this.canonical;
    }

    /**
     * Get Page Properties
     *
     * @return returns a ValueMap containing the properties for this resource
     *
     */
    protected ValueMap getProperties() {
        return this.request.getResource().getValueMap();
    }

    /**
     * Get Reference Url
     *
     * @return returns the reference path url generated from page path but without
     *         /content/edcportal/... Based on business rule
     *
     */
    public String getReferenceURL() {
        return this.referenceURL;
    }

    /**
     * Get Page Headline
     *
     * @return the headline
     */
    public String getHeadline() {
        return this.headline;
    }

    /**
     * Get Page Description
     *
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Get Modified Date
     *
     * @return the modified date
     */
    public String getModified() {
        return this.modified;
    }

    /**
     * Get Published Date
     *
     * @return the published date
     */
    public String getPublished() {
        return this.published;
    }

    /**
     * Get Page Language
     * 
     * @return Page Language
     */
    public String getPageLang() {
        return this.pageLang;
    }

    /**
     * Get DTM's Url
     *
     * @return DTM url
     */
    public String getDtmUrl() {
        return myEDCConfigSystemService.getDtmLibURL() != null ? myEDCConfigSystemService.getDtmLibURL() : "";
    }

    /**
     * Get One Trurst's Full Javascript's EN EU URI
     *
     * @return One Trurst's Full Javascript's URI EN EU if defined, empty if not
     */
    public String getOneTrustScriptUrlEuEn() {
        return myEDCConfigSystemService.getOneTrustScriptUrlEuEn() != null
                ? myEDCConfigSystemService.getOneTrustScriptUrlEuEn()
                : "";
    }

    /**
     * Get One Trurst's Full Javascript's FR EU URI
     *
     * @return One Trurst's Full Javascript's URI FR EU if defined, empty if not
     */
    public String getOneTrustScriptUrlEuFr() {
        return myEDCConfigSystemService.getOneTrustScriptUrlEuFr() != null
                ? myEDCConfigSystemService.getOneTrustScriptUrlEuFr()
                : "";
    }

    /**
     * Get One Trurst's Full Javascript's EN Non EU URI
     *
     * @return One Trurst's Full Javascript's URI EN Non EU if defined, empty if not
     */
    public String getOneTrustScriptUrlNonEuEn() {
        return myEDCConfigSystemService.getOneTrustScriptUrlNonEuEn() != null
                ? myEDCConfigSystemService.getOneTrustScriptUrlNonEuEn()
                : "";
    }

    /**
     * Get One Trurst's Full Javascript's FR Non EU URI
     *
     * @return One Trurst's Full Javascript's URI FR EU if defined, empty if not
     */
    public String getOneTrustScriptUrlNonEuFr() {
        return myEDCConfigSystemService.getOneTrustScriptUrlNonEuFr() != null
                ? myEDCConfigSystemService.getOneTrustScriptUrlNonEuFr()
                : "";
    }

    /**
     * Get One Trurst's Script Source
     *
     * @return One Trurst's Script Source if defined, empty if not
     */
    public String getOneTrustScripSrc() {
        return myEDCConfigSystemService.getOneTrustScriptSrc() != null ? myEDCConfigSystemService.getOneTrustScriptSrc()
                : "";
    }

    /**
     * Method to get One Trust's Script SRI Hash
     *
     * @return One Trurst's Script SRI Hash
     */
    public String getOneTrustScriptIntegrityHash() {
        return myEDCConfigSystemService.getOneTrustScriptIntegrityHash() != null
                ? myEDCConfigSystemService.getOneTrustScriptIntegrityHash()
                : "";
    }

    /**
     * Get One Trurst's Domain Source
     *
     * @return One Trurst's Domain Source if defined, empty if not
     */
    public String getOneTrustDomainSrc() {
        return myEDCConfigSystemService.getOneTrustDomainSrc() != null ? myEDCConfigSystemService.getOneTrustDomainSrc()
                : "";
    }

    /**
     * Check if all the One Trurst's URls are defined
     *
     * @return Boolean true is all URLS are defined, false if not
     */
    public Boolean isOneTrustSet() {
        return (!getOneTrustScriptUrlEuEn().isEmpty() && !getOneTrustScriptUrlEuFr().isEmpty()
                && !getOneTrustScriptUrlNonEuEn().isEmpty() && !getOneTrustScriptUrlNonEuFr().isEmpty());
    }

    /**
     * Check if all the One Trurst's URls are defined
     *
     * @return Boolean true is all URLS are defined, false if not
     */
    public Boolean isOneTrustUpgradeSet() {
        return (!getOneTrustDomainSrc().isEmpty() && !getOneTrustScripSrc().isEmpty());
    }

}