/*
 *  Copyright 2017 EDC
 */

/**
 * This class defines all the custom treatment used by the
 * EDC page component
 *
 * @author Valerie Trenou
 */

package com.edc.edcweb.core.models;

import com.adobe.cq.sightly.SightlyWCMMode;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.datasources.TagsList;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ReplicationStatusHelper;
import com.edc.edcweb.core.service.EDCSystemConfigurationService;
import com.edc.edcweb.core.service.EloquaService;

import com.edc.edcweb.core.helpers.LanguageUtil;

@Model(adaptables = SlingHttpServletRequest.class)
public class Page // extends com.adobe.cq.wcm.core.components.models.impl.v1.PageImpl
{
    private static final Logger log = LoggerFactory.getLogger(Page.class);

    @Inject
    @Source("sling-object")
    private ResourceResolver resourceResolver;

    @Inject
    private EDCSystemConfigurationService edcSystemConfiguration;

    @Inject
    private EloquaService eloquaServiceImpl;

    @Self
    private SlingHttpServletRequest request;

    @ScriptVariable(name = "wcmmode")
    private SightlyWCMMode wcmMode;
    private ValueMap properties;
    private String canonical;

    private List<String> liveChatPaths = Arrays.asList("/solutions/");
    private boolean injectLiveChat;
    private String whosOnDomain;

    // path to present as reference, the page path less the /content/edc part of it.
    private String referenceURL;

    private String metaKeywords;
    private String metaEloqua;

    private String headline;
    private String published;
    private String modified;
    private String description;
    private String image;
    private String logo;
    private String pageType;
    private String pageLang;

    @PostConstruct
    protected void init() {
        if (log.isDebugEnabled()) {
            log.debug("Page PostConstruct ");
        }

        this.properties = this.getProperties();
        this.processCanonicalPath();
        this.setFirstPublished();
        this.setInjectLiveChat();
        this.setReferenceURL();
        this.setWhosOnDomain();

        this.setMetaKeywords();
        this.setMetaEloqua();

        this.setHeadline();
        this.setDescription();
        this.setModified();
        this.setPublished();
        this.setImages();
        this.setPageType();
        this.setPageLang();
    }

    /**
     * processCanonicalPath()
     *
     * Called to process the canonical path.
     */
    private void processCanonicalPath() {
        String scheme = request.getScheme();
        String siteDomain = edcSystemConfiguration.getSiteDomainName();       
        this.canonical = properties.get(Constants.Properties.CANONICAL, String.class);
        if (this.canonical == null) {
            this.canonical = this.request.getRequestURI().toString().toLowerCase();
        }        
        if ((this.canonical != null) && !(this.canonical.startsWith("http://") || this.canonical.startsWith("https://"))) {
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
            if (!this.canonical.contains(".")) {
                this.canonical += Constants.HTML_EXTENTION;
            }
            // ---------------------------------------------------------------------
            // complete url with scheme and domain
            // ---------------------------------------------------------------------
            this.canonical = scheme + "://" + siteDomain + this.canonical;

        }
    }

    /**
     * setFirstPublished()
     *
     * Called to set the first published date for this page. If that value is
     * already set, it will not be set again (only want the first published date).
     */
    private void setFirstPublished() {
        Calendar firstPublished;
        if ((this.wcmMode != null) && (!this.wcmMode.isDisabled()) && (this.request.getResource() != null)
                && (this.request.getResource().getPath().toLowerCase().startsWith(Constants.Paths.CONTENT_EDC))) {
            firstPublished = properties.get(Constants.Properties.FIRST_PUBLISHED, Calendar.class);
            if (firstPublished == null) {
                // -------------------------------------------------------------
                ReplicationStatus repStatus = ReplicationStatusHelper.getReplicationStatus(this.request.getResource());
                // -------------------------------------------------------------
                if ((repStatus != null) && (repStatus.getLastPublished() != null)) {
                    firstPublished = repStatus.getLastPublished();
                    // -----------------------------------------------------
                    this.setNodeValue(Constants.Properties.FIRST_PUBLISHED, firstPublished);
                }
            }
        }
    }

    /**
     * setInjectLiveChat()
     *
     * Called to set the first published date for this page. If that value is
     * already set, it will not be set again (only want the first published date).
     */
    private void setInjectLiveChat() {
        boolean pathMatch = false;

        if (this.request != null) {
            String currentPath = this.request.getRequestURI().toLowerCase();
            for (int i = 0; i < liveChatPaths.size(); i++) {
                if (currentPath.contains(liveChatPaths.get(i).toLowerCase())) {
                    pathMatch = true;
                }
            }
        }
        this.injectLiveChat = pathMatch;
    }

    private void setReferenceURL() {
        if (log.isDebugEnabled()) {
            log.debug("setReferenceURL");
        }

        if (this.request != null) {
            String currentPath = this.request.getRequestURL().toString().toLowerCase();
            this.referenceURL = currentPath.replaceFirst(Constants.Paths.CONTENT_EDC, "");

        }

    }

    /**
     * setNodeValue()
     *
     * Set a node key to given value.
     * 
     * @param key   Key (name) of the value.
     * @param value The value to which the key is to be set.
     * @return boolean true if the value is set successfully.
     */
    private boolean setNodeValue(String key, Calendar value) {
        boolean valueSet = false;
        // ---------------------------------------------------------------------
        Session session = this.resourceResolver.adaptTo(Session.class);
        try {
            Node node = this.request.getResource().adaptTo(Node.class);
            if (node != null) {
                node.setProperty(key, value);
                if (session != null) {
                    session.save();
                }
                valueSet = true;
            }
        } catch (RepositoryException e) {
            // Nothing the user can do about this...
        }
        // ---------------------------------------------------------------------
        return valueSet;
    }

    private void setMetaKeywords() {

        ArrayList<Tag> tags = new ArrayList<>();
        String result = "";
        // ---------------------------------------------------------------------
        // Get all tags assigned to this page
        // ---------------------------------------------------------------------
        if (this.resourceResolver != null && this.request != null) {
            String[] tagArray = TagsList.getTagsFromRequest(this.resourceResolver, this.request);
            final TagManager tagMgr = this.resourceResolver.adaptTo(TagManager.class);
            // ---------------------------------------------------------------------
            if (tagMgr != null) {
                // -----------------------------------------------------------------
                // Iterate all tags adding them to the "tags" list as long as they
                // are not one of the tag types that should not be in the Meta keywords
                // -----------------------------------------------------------------
                for (String tag : tagArray) {
                    Tag tagObject = tagMgr.resolve(tag);
                    if (tagObject != null) {

                        String tagNamespace = tagObject.getNamespace().getName()
                                + Constants.TAG_CLOUD_NAMESPACE_DELIMITER;
                        if (this.isValidMetaKeywordTag(tagObject, tagNamespace,
                                Constants.ArrayValues.META_KEYWORDS_TAGS_TO_IGNORE.toArray(), false)) {
                            tags.add(tagObject);
                        }
                    }
                }

                Iterator<Tag> tagIterator = tags.iterator();
                while (tagIterator.hasNext()) {
                    Tag aTag = tagIterator.next();
                    result += aTag.getLocalizedTitle(new Locale("en"));
                    if (tagIterator.hasNext()) {
                        result += ",";
                    }
                }

            }
        }

        this.metaKeywords = result;
    }

    private void setMetaEloqua() {

        ArrayList<Tag> tags = new ArrayList<>();
        String result = "";
        // ---------------------------------------------------------------------
        // Get all tags assigned to this page
        // ---------------------------------------------------------------------
        if (this.resourceResolver != null && this.request != null) {
            String[] tagArray = TagsList.getTagsFromRequest(this.resourceResolver, this.request);
            final TagManager tagMgr = this.resourceResolver.adaptTo(TagManager.class);
            // ---------------------------------------------------------------------
            if (tagMgr != null) {
                // -----------------------------------------------------------------
                // Iterate all tags adding them to the "tags" list as long as they
                // are not one of the tag types that should not be in the Meta keywords
                // -----------------------------------------------------------------
                for (String tag : tagArray) {
                    Tag tagObject = tagMgr.resolve(tag);
                    if (tagObject != null) {

                        String tagNamespace = tagObject.getNamespace().getName()
                                + Constants.TAG_CLOUD_NAMESPACE_DELIMITER;
                        if (this.isValidMetaKeywordTag(tagObject, tagNamespace,
                                Constants.ArrayValues.META_ELOQUA_TAGS_TO_INCLUDE.toArray(), true)) {
                            tags.add(tagObject);
                        }
                    }
                }

                if (!tags.isEmpty()) {
                    result = builtMetaEloquaValue(tags);
                }

            }
        }

        this.metaEloqua = result;
    }

    private boolean isValidMetaKeywordTag(Tag tagObject, String tagNamespace, String[] tagConstrains,
            boolean isInclude) {

        boolean valid = false;
        if (isInclude) {
            for (int i = 0; i < tagConstrains.length; i++) {
                if (tagObject.getTagID().startsWith(tagNamespace + tagConstrains[i])) {
                    return true;
                }
            }
        } else {
            for (int i = 0; i < tagConstrains.length; i++) {
                if (tagObject.getTagID().startsWith(tagNamespace + tagConstrains[i])) {
                    return false;
                }
            }
            valid = true;
        }
        return valid;
    }

    private String builtMetaEloquaValue(ArrayList<Tag> tags) {
        String result = "";
        if (!tags.isEmpty()) {
            Iterator<Tag> tagIterator = tags.iterator();
            while (tagIterator.hasNext()) {
                Tag aTag = tagIterator.next();
                if (aTag != null) {
                    String tagId = aTag.getTagID();
                    tagId = tagId.replaceFirst(Constants.TAGS_EDC_NAMESPACE, "");
                    String tagMetaValue = tagId.replaceAll("/", ": ");
                    if (result.length() == 0) {
                        result = tagMetaValue;
                    } else {
                        result = result + "," + tagMetaValue;
                    }

                }
            }
        }
        return result;
    }

    private void setHeadline() {
        String pageTitle = properties.get(Constants.Properties.PAGE_TITLE, String.class);
        String navTitle = properties.get(Constants.Properties.NAV_TITLE, String.class);
        String jcrTitle = properties.get(Constants.Properties.JCR_TITLE, String.class);

        this.headline = pageTitle != null ? pageTitle : navTitle != null ? navTitle : jcrTitle;
    }

    private void setDescription() {
        String pageDescription = properties.get(Constants.Properties.JCR_DESCRIPTION, String.class);
        String pageSynopsis = properties.get(Constants.Properties.ARTICLE_SYNOPSIS, String.class);

        this.description = pageDescription != null ? pageDescription : pageSynopsis;
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

    private void setImages() {
        if (this.request != null) {
            String path = null != edcSystemConfiguration.getSiteDomainName()
                    ? edcSystemConfiguration.getSiteDomainName()
                    : "";

            // ---------------------------------------------------------------------
            // Set the image
            // ---------------------------------------------------------------------
            if (properties.get(Constants.Properties.TABLET_FILE_REFERENCE, String.class) != null) {
                this.image = path + properties.get(Constants.Properties.TABLET_FILE_REFERENCE, String.class);
            }

            if (null == this.image && properties.get(Constants.Properties.FILE_REFERENCE, String.class) != null) {
                this.image = path + properties.get(Constants.Properties.FILE_REFERENCE, String.class);
            }

            if (null == this.image && properties.get(Constants.Properties.TEASER_IMAGE_SOURCE, String.class) != null) {
                this.image = path + properties.get(Constants.Properties.TEASER_IMAGE_SOURCE, String.class);
            }

            // ---------------------------------------------------------------------
            // Set the logo
            // ---------------------------------------------------------------------
            this.logo = path + Constants.Paths.BLACK_LOGO;
        }
    }

    private void setPageType() {
        boolean foundContentType = false;
        // ---------------------------------------------------------------------
        // Get all tags assigned to this page
        // ---------------------------------------------------------------------
        if (this.resourceResolver != null && this.request != null) {
            String[] tagArray = TagsList.getTagsFromRequest(this.resourceResolver, this.request);
            final TagManager tagMgr = this.resourceResolver.adaptTo(TagManager.class);

            if (tagMgr != null) {
                for (String tag : tagArray) {
                    Tag tagObject = tagMgr.resolve(tag);
                    if (tagObject != null) {
                        if (tagObject.getPath().startsWith(Constants.Paths.CONTENT_TYPE_TAGS)
                                || tagObject.getPath().startsWith(Constants.Paths.EVENT_TYPE_TAGS)) {
                            if (this.pageType == null) {
                                foundContentType = true;
                                this.pageType = tagObject.getTitle();
                            }
                        }
                    }
                }
                // -----------------------------------------------------------------
                // If content type was not found, empty is the default
                // -----------------------------------------------------------------
                if (!foundContentType) {
                    this.pageType = "WebPage";
                }
            }
        }
    }

    private void setPageLang() {
        try {
            this.pageLang = LanguageUtil.getLanguageAbbreviationFromPath(this.request.getResource().getPath(), "EN");
        } catch (Exception ex) {
            // No action
            this.pageLang = "EN";
        }
    }

    /**
     * getCanonical()
     *
     * @return returns the canonical url generated from page 'canonical' property
     *         Based on business rule
     */
    public String getCanonical() {
        return this.canonical;
    }

    /**
     * getCanonical()
     *
     * @return returns a boolean indicating if the live chat script should be added
     *         to the page
     */
    public boolean getInjectLiveChat() {
        return this.injectLiveChat;
    }

    /**
     * getCWhosOnDomain()
     *
     * @return returns a Strng indicating if the domain the live chat script should
     *         pass during instantiation
     */
    public String getWhosOnDomain() {
        return this.whosOnDomain;
    }

    /**
     * setWhosOnDomain()
     *
     * @return sets the Domain name for the WhosOn Plugin
     */
    public void setWhosOnDomain() {
        this.whosOnDomain = edcSystemConfiguration.getLiveChatDomainName();
    }

    /**
     * getProperties()
     *
     * @return returns a ValueMap containing the properties for this resource
     */
    protected ValueMap getProperties() {
        return this.request.getResource().getValueMap();
    }

    /**
     * getReferenceURL()
     *
     * @return returns the reference path url generated from page path but without
     *         /content/edc... Based on business rule
     */
    public String getReferenceURL() {

        return this.referenceURL;
    }

    public String getMetaKeywords() {

        return this.metaKeywords;
    }

    public String getMetaEloqua() {

        return this.metaEloqua;
    }

    /**
     * @return the headline
     */
    public String getHeadline() {
        return this.headline;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @return the modified date
     */
    public String getModified() {
        return this.modified;
    }

    /**
     * @return the published date
     */
    public String getPublished() {
        return this.published;
    }

    /**
     * @return the EDC logo
     */
    public String getLogo() {
        return this.logo;
    }

    /**
     * @return the image assigned to the page
     */
    public String getImage() {
        return this.image;
    }

    /**
     * @return page type
     */
    public String getPageType() {
        return this.pageType;
    }

    /**
     * Get DTM's Url
     *
     * @return DTM url
     */
    public String getDtmUrl() {
        return edcSystemConfiguration.getDtmLibURL() != null ? edcSystemConfiguration.getDtmLibURL() : "";
    }

    /**
     * Get Eloqua Site Id
     *
     * @return Eloqua Site Id
     */
    public String getEloquaSiteId() {
        return eloquaServiceImpl.getSiteId() != null ? eloquaServiceImpl.getSiteId() : "";
    }

    /**
     * Get Google Site Verification
     *
     * @return Google Site Verification Code
     */
    public String getGoogleSiteVerification() {
        return edcSystemConfiguration.getGoogleSiteVerification() != null
                ? edcSystemConfiguration.getGoogleSiteVerification()
                : "";
    }

    /**
     * Get MS Validate
     *
     * @return MS Validate Code
     */
    public String getMsValidate() {
        return edcSystemConfiguration.getMsValidate() != null ? edcSystemConfiguration.getMsValidate() : "";
    }

    /**
     * Get If tracking scripts should load on author side
     *
     * @return true If tracking scripts should load on author side
     */
    public String getEnableAuthor() {
        return edcSystemConfiguration.getEnableAuthor() != null ? edcSystemConfiguration.getEnableAuthor() : "";
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
     * Get One Trurst's Full Javascript's EN EU URI
     * 
     * @return One Trurst's Full Javascript's URI EN EU if defined, empty if not
     */
    public String getOneTrustScriptUrlEuEn() {
        return edcSystemConfiguration.getOneTrustScriptUrlEuEn() != null
                ? edcSystemConfiguration.getOneTrustScriptUrlEuEn()
                : "";
    }

    /**
     * Get One Trurst's Full Javascript's FR EU URI
     * 
     * @return One Trurst's Full Javascript's URI FR EU if defined, empty if not
     */
    public String getOneTrustScriptUrlEuFr() {
        return edcSystemConfiguration.getOneTrustScriptUrlEuFr() != null
                ? edcSystemConfiguration.getOneTrustScriptUrlEuFr()
                : "";
    }

    /**
     * Get One Trurst's Full Javascript's EN Non EU URI
     * 
     * @return One Trurst's Full Javascript's URI EN Non EU if defined, empty if not
     */
    public String getOneTrustScriptUrlNonEuEn() {
        return edcSystemConfiguration.getOneTrustScriptUrlNonEuEn() != null
                ? edcSystemConfiguration.getOneTrustScriptUrlNonEuEn()
                : "";
    }

    /**
     * Get One Trurst's Full Javascript's FR Non EU URI
     * 
     * @return One Trurst's Full Javascript's URI FR EU if defined, empty if not
     */
    public String getOneTrustScriptUrlNonEuFr() {
        return edcSystemConfiguration.getOneTrustScriptUrlNonEuFr() != null
                ? edcSystemConfiguration.getOneTrustScriptUrlNonEuFr()
                : "";
    }

    /**
     * Get One Trurst's Script Source
     *
     * @return One Trurst's Script Source if defined, empty if not
     */
    public String getOneTrustScripSrc() {
        return edcSystemConfiguration.getOneTrustScriptSrc() != null ? edcSystemConfiguration.getOneTrustScriptSrc()
                : "";
    }

    /**
     * Method to get One Trust's Script SRI Hash
     *
     * @return One Trurst's Script SRI Hash
     */
    public String getOneTrustScriptIntegrityHash() {
        return edcSystemConfiguration.getOneTrustScriptIntegrityHash() != null
                ? edcSystemConfiguration.getOneTrustScriptIntegrityHash()
                : "";
    }

    /**
     * Get One Trurst's Domain Source
     *
     * @return One Trurst's Domain Source if defined, empty if not
     */
    public String getOneTrustDomainSrc() {
        return edcSystemConfiguration.getOneTrustDomainSrc() != null ? edcSystemConfiguration.getOneTrustDomainSrc()
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