package com.edc.edcweb.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.edc.edcweb.core.datasources.TagsList;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.service.EloquaService;
import java.util.ArrayList;

@Model(adaptables = SlingHttpServletRequest.class)
public class NewsletterSubscription
{
    private static final Logger log = LoggerFactory.getLogger(NewsletterSubscription.class);

    @Inject @Source("sling-object")
    private ResourceResolver resourceResolver;

    @Self
    private SlingHttpServletRequest request;

    @Inject
    @Optional
    private Page currentPage;

    @ScriptVariable
    private ValueMap properties;

    @Inject
    @Optional
    private EloquaService eloquaService;

    private String title;
    private String subtitle;
    private String logo;
    private String logoHighRes;
    private String logoAlt;
    private String placeholder;
    private String inquiryID;
    private String docID;
    private String disclaimer;
    private String ctaText;
    private String successTitle;
    private String successText;
    private String pageLanguage;
    private String eloquaFormName;
    private String eloquaSiteId;
    private String eloquaFormSubmitURL;
    private String displaytype;
    private String pageType;
    private String eventName;

    /**
     * Populates the Model with values from the applicable ContentPolicy (based on current path language).
     *
     */
    @PostConstruct
    public void initModel() {
        // Set pageType
        setPageType();

        ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(this.request, this.currentPage);
        if (contentPolicy != null) {
            ValueMap policy_properties = contentPolicy.getProperties();
            String pagePath = currentPage.getPath();
            String languageAbbreviation = LanguageUtil.getLanguageAbbreviationFromPath(pagePath, Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());

            if (this.pageType.toLowerCase().equals("weekly commentary")) {
                this.title              = policy_properties.get(Constants.Properties.WCTITLE, String.class);
                this.subtitle           = policy_properties.get(Constants.Properties.WCSUBTITLE, String.class);
                this.logo               = policy_properties.get(Constants.Properties.WCLOGO, String.class);
                this.logoHighRes        = policy_properties.get(Constants.Properties.NEWSLETTER_WCLOGOHIGRES, String.class);
                this.logoAlt            = policy_properties.get(Constants.Properties.NEWSLETTER_WCLOGOALT, String.class);
                this.placeholder        = policy_properties.get(Constants.Properties.NEWSLETTER_WCPLACEHOLDER, String.class);
                this.inquiryID          = policy_properties.get(Constants.Properties.NEWSLETTER_WCINQUIRYID, String.class);
                this.docID              = policy_properties.get(Constants.Properties.NEWSLETTER_WCDOCID, String.class);
                this.disclaimer         = policy_properties.get(Constants.Properties.NEWSLETTER_WCDISCLAIMER, String.class);
                this.ctaText            = policy_properties.get(Constants.Properties.NEWSLETTER_WCCTATEXT, String.class);
                this.successTitle       = policy_properties.get(Constants.Properties.NEWSLETTER_WCSUCCESSTITLE, String.class);
                this.successText        = policy_properties.get(Constants.Properties.NEWSLETTER_WCSUCCESSTEXT, String.class);
            } else {
                this.title              = policy_properties.get(Constants.Properties.TITLE, String.class);
                this.subtitle           = policy_properties.get(Constants.Properties.SUBTITLE, String.class);
                this.logo               = policy_properties.get(Constants.Properties.LOGO, String.class);
                this.logoHighRes        = policy_properties.get(Constants.Properties.NEWSLETTER_LOGOHIGRES, String.class);
                this.logoAlt            = policy_properties.get(Constants.Properties.NEWSLETTER_LOGOALT, String.class);
                this.placeholder        = policy_properties.get(Constants.Properties.NEWSLETTER_PLACEHOLDER, String.class);
                this.inquiryID          = policy_properties.get(Constants.Properties.NEWSLETTER_INQUIRYID, String.class);
                this.docID              = policy_properties.get(Constants.Properties.NEWSLETTER_DOCID, String.class);
                this.disclaimer         = policy_properties.get(Constants.Properties.NEWSLETTER_DISCLAIMER, String.class);
                this.ctaText            = policy_properties.get(Constants.Properties.NEWSLETTER_CTATEXT, String.class);
                this.successTitle       = policy_properties.get(Constants.Properties.NEWSLETTER_SUCCESSTITLE, String.class);
                this.successText        = policy_properties.get(Constants.Properties.NEWSLETTER_SUCCESSTEXT, String.class);
                this.eventName          = policy_properties.get(Constants.Properties.NEWSLETTER_EVENTNAME, String.class);
            }

            this.displaytype        = properties.get(Constants.Properties.NEWSLETTER_DISPLAYTYPE, String.class);

            // Special String format required by eloqua form
            if(languageAbbreviation.equalsIgnoreCase("en")) {
                this.pageLanguage = "English";
            }
            else if(languageAbbreviation.equalsIgnoreCase("fr")) {
                this.pageLanguage = "French";
            }

            this.eloquaFormName = eloquaService.getNewsletterFormName()==null?"":eloquaService.getNewsletterFormName();
            this.eloquaSiteId = eloquaService.getSiteId()==null?"":eloquaService.getSiteId();
            this.eloquaFormSubmitURL = eloquaService.getFormSubmitURL()==null?"":eloquaService.getFormSubmitURL();
        }

        String dialogTitle = properties.get(Constants.Properties.TITLE, String.class);
        if (dialogTitle != null && !dialogTitle.isEmpty()) {
            this.title = dialogTitle;
        }

        String dialogSubtitle = properties.get(Constants.Properties.SUBTITLE, String.class);
        if (dialogSubtitle != null && !dialogSubtitle.isEmpty()) {
            this.subtitle = dialogSubtitle;
        }

        String customInquiryId = properties.get(Constants.Properties.NEWSLETTER_CUSTOMINQUIRYID, String.class);
        if(customInquiryId != null && customInquiryId.trim().length()>0 ){
            this.inquiryID = customInquiryId;
        }

        String customDocId = properties.get(Constants.Properties.NEWSLETTER_CUSTOMDOCID, String.class);
        if(customDocId != null && customDocId.trim().length()>0 ){
            this.docID = customDocId;
        }
    }

    private void setPageType() {
        boolean foundContentType=false;
        ArrayList<Tag> tags = new ArrayList<>();
        //---------------------------------------------------------------------
        // Get all tags assigned to this page
        //---------------------------------------------------------------------
        if (this.resourceResolver != null && this.request != null) {
            String[] tagArray = TagsList.getTagsFromRequest(this.resourceResolver, this.request);
            final TagManager tagMgr = this.resourceResolver.adaptTo(TagManager.class);

            if (tagMgr != null){
                for(String tag: tagArray) {
                    Tag tagObject = tagMgr.resolve(tag);
                    if(tagObject != null) {
                        if (tagObject.getPath().startsWith(Constants.Paths.CONTENT_TYPE_TAGS)) {
                            if ((this.pageType == null) && (tagObject.getTagID().equals(Constants.ArticleContentType.WEEKLYCOMMENTARY.getTagId()))) {
                                foundContentType        = true;
                                this.pageType = tagObject.getTitle();
                            }
                        }
                    }
                }
                //-----------------------------------------------------------------
                // If content type was not found, empty is the default
                //-----------------------------------------------------------------
                if(!foundContentType) {
                    this.pageType = "WebPage";
                }
            }
        }
    }

    public String getTitle() {
        return this.title;
    }

    public String getSubtitle() {
        return this.subtitle;
    }

    public String getLogo() {
        return this.logo;
    }

    public String getLogoHighRes() {
        return this.logoHighRes;
    }

    public String getLogoAlt() {
        return this.logoAlt;
    }

    public String getPlaceholder() {
        return this.placeholder;
    }

    public String getInquiryID() {
        return this.inquiryID;
    }

    public String getDocID() {
        return this.docID;
    }

    public String getDisclaimer() {
        return this.disclaimer;
    }

    public String getCtaText() {
        return this.ctaText;
    }

    public String getSuccessTitle() {
        return this.successTitle;
    }

    public String getSuccessText() {
        return this.successText;
    }

    public String getPageLanguage() {
        return this.pageLanguage;
    }

    public String getEloquaFormName(){
       return  this.eloquaFormName;
    }

    /**
     *
     * @return the value for elqSiteId
     */
    public String getEloquaSiteId() {
        return this.eloquaSiteId;
    }

    public String getEloquaFormSubmitURL(){
        return this.eloquaFormSubmitURL;
    }

    public String getDisplaytype() {
        return this.displaytype;
    }

    public String getEventName() {
        return eventName;
    }
}