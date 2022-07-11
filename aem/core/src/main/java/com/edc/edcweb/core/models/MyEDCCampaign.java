package com.edc.edcweb.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;

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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.service.EloquaService;
import java.util.ArrayList;
/**
 * @author Dennis Bonilla
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * 
 * Backing Java code for the MyEDC Campaign 
 * component used by the EDC web site.
 * 
 */

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class MyEDCCampaign
{
    private static final Logger log = LoggerFactory.getLogger(MyEDCCampaign.class);

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

    private String topTitle;
    private String title;
    private String subtitle;
    private String logo;
    private String logoHighRes;
    private String logoAlt;
    private String placeholder;
    private String inquiryID;
    private String docID;
    private String disclaimer;
    private String successTitle;
    private String successText;
    private String pageLanguage;
    private String eloquaFormName;
    private String eloquaSiteId;
    private String eloquaFormSubmitURL;
    private String cancelBtnText;
    private String submitBtnText;
    private String cancelBtnEnglishText;
    private String submitBtnEnglishText;

    @PostConstruct
    public void initModel() {

        this.populateModel();
        this.setupEloqua();

    }

    /**
     * Populates the Model with values from 
     * the applicable ContentPolicy 
     * (based on current path language).
     */
    private void populateModel () {

        ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(this.request, this.currentPage);
        String pagePath = currentPage.getPath();
        String languageAbbreviation = LanguageUtil.getLanguageAbbreviationFromPath(pagePath, Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());

        if (contentPolicy != null) {

            ValueMap policyProperties = contentPolicy.getProperties();
            this.topTitle               = policyProperties.get(Constants.Properties.TOPTITLE, String.class);
            this.title                  = policyProperties.get(Constants.Properties.WCTITLE, String.class);
            this.subtitle               = policyProperties.get(Constants.Properties.WCSUBTITLE, String.class);
            this.logo                   = policyProperties.get(Constants.Properties.WCLOGO, String.class);
            this.logoHighRes            = policyProperties.get(Constants.Properties.NEWSLETTER_WCLOGOHIGRES, String.class);
            this.logoAlt                = policyProperties.get(Constants.Properties.NEWSLETTER_WCLOGOALT, String.class);
            this.placeholder            = policyProperties.get(Constants.Properties.NEWSLETTER_WCPLACEHOLDER, String.class);
            this.inquiryID              = policyProperties.get(Constants.Properties.NEWSLETTER_WCINQUIRYID, String.class);
            this.docID                  = policyProperties.get(Constants.Properties.NEWSLETTER_WCDOCID, String.class);
            this.disclaimer             = policyProperties.get(Constants.Properties.NEWSLETTER_WCDISCLAIMER, String.class);
            this.successTitle           = policyProperties.get(Constants.Properties.NEWSLETTER_WCSUCCESSTITLE, String.class);
            this.successText            = policyProperties.get(Constants.Properties.NEWSLETTER_WCSUCCESSTEXT, String.class);
            this.cancelBtnText          = policyProperties.get(Constants.Properties.PROGRESSIVEPROFILING_CANCELBUTTON, String.class);
            this.submitBtnText          = policyProperties.get(Constants.Properties.PROGRESSIVEPROFILING_SUBMITBUTTON, String.class);
            this.cancelBtnEnglishText   = policyProperties.get(Constants.Properties.PROGRESSIVEPROFILING_CANCELBUTTONEN, String.class);
            this.submitBtnEnglishText   = policyProperties.get(Constants.Properties.PROGRESSIVEPROFILING_SUBMITBUTTONEN, String.class);
        }

        // Special String format required by eloqua form
        if(languageAbbreviation.equalsIgnoreCase("en")) {
            this.pageLanguage = "English";
        }
        else if(languageAbbreviation.equalsIgnoreCase("fr")) {
            this.pageLanguage = "French";
        }
    }

    // Setup of the eloqua service.
    private void setupEloqua() {

        this.eloquaFormName = eloquaService.getNewsletterFormName()==null?"":eloquaService.getNewsletterFormName();
        this.eloquaSiteId = eloquaService.getSiteId()==null?"":eloquaService.getSiteId();
        this.eloquaFormSubmitURL = eloquaService.getFormSubmitURL()==null?"":eloquaService.getFormSubmitURL();

        if(StringUtils.isEmpty(this.eloquaFormName)) {
            this.eloquaFormName = "no eloqua form name";
        }

        if(StringUtils.isEmpty(this.eloquaSiteId)) {
            this.eloquaSiteId = "no eloqua site id";
        }

        if(StringUtils.isEmpty(this.eloquaFormSubmitURL)) {
            this.eloquaFormSubmitURL = "eloqua form submit url";
        }
    }

    public String getTopTitle() { 
        String tempValue = properties.get("topTitle", String.class);
        return StringUtils.isNotBlank(tempValue) ?  tempValue  : this.topTitle;
    }

    public String getTitle() {
        String tempValue = properties.get("title", String.class);
        return StringUtils.isNotBlank(tempValue) ?  tempValue  : this.title;
    }

    public String getSubtitle() {
        String tempValue = properties.get("subtitle", String.class);
        return StringUtils.isNotBlank(tempValue) ?  tempValue  : this.subtitle;
    }

    public String getLogo() {
        String tempValue = properties.get("logo", String.class);
        return StringUtils.isNotBlank(tempValue) ?  tempValue  : this.logo;
    }

    public String getLogoHighRes() {
        String tempValue = properties.get("logoHighRes", String.class);
        return StringUtils.isNotBlank(tempValue) ?  tempValue  : this.logoHighRes;
    }

    public String getLogoAlt() {
        String tempValue = properties.get("logoAlt", String.class);
        return StringUtils.isNotBlank(tempValue) ?  tempValue  : this.logoAlt;
    }

    public String getPlaceholder() {
        String tempValue = properties.get("placeholder", String.class);
        return StringUtils.isNotBlank(tempValue) ?  tempValue  : this.placeholder;
    }

    public String getInquiryID() {
        String tempValue = properties.get("inquiryID", String.class);
        return StringUtils.isNotBlank(tempValue) ?  tempValue  : this.inquiryID;
    }

    public String getDocID() {
        String tempValue = properties.get("docID", String.class);
        return StringUtils.isNotBlank(tempValue) ?  tempValue  : this.docID;
    }

    public String getDisclaimer() {
        String tempValue = properties.get("disclaimer", String.class);
        return StringUtils.isNotBlank(tempValue) ?  tempValue  : this.disclaimer;
    }

    public String getSuccessTitle() {
        String tempValue = properties.get("successTitle", String.class);
        return StringUtils.isNotBlank(tempValue) ?  tempValue  : this.successTitle;
    }

    public String getSuccessText() {
        String tempValue = properties.get("successText", String.class);
        return StringUtils.isNotBlank(tempValue) ?  tempValue  : this.successText;
    }

    public String getPageLanguage() {
        return this.pageLanguage;
    }

    public String getEloquaFormName(){
       return  this.eloquaFormName;
    }


    public String getEloquaSiteId() {
        return this.eloquaSiteId;
    }

    public String getEloquaFormSubmitURL(){
        return this.eloquaFormSubmitURL;
    }

    public String getCancelBtnText() {
        String tempValue = properties.get("cancelBtnText", String.class);
        return StringUtils.isNotBlank(tempValue) ?  tempValue  : this.cancelBtnText;
    }

    public String getSubmitBtnText() {
        String tempValue = properties.get("submitBtnText", String.class);
        return StringUtils.isNotBlank(tempValue) ?  tempValue  : this.submitBtnText;
    }
	
    public String getCancelBtnEnglishText() {
        String tempValue = properties.get(Constants.Properties.PROGRESSIVEPROFILING_CANCELBUTTONEN, String.class);
        return StringUtils.isNotBlank(tempValue) ?  tempValue  : this.cancelBtnEnglishText;
    }

    public String getSubmitBtnEnglishText() {
        String tempValue = properties.get(Constants.Properties.PROGRESSIVEPROFILING_SUBMITBUTTONEN, String.class);
        return StringUtils.isNotBlank(tempValue) ?  tempValue  : this.submitBtnEnglishText;
    }
}
