package com.edc.edcweb.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.constants.ConstantsForm;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.FormFieldsUtil;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.service.EloquaService;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;


@Model(adaptables = SlingHttpServletRequest.class)
public class ScheduleACall
{
    private static final Logger log = LoggerFactory.getLogger(ScheduleACall.class);

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
    private String inquiryID;
    private String firstname;
    private String companynamelabel;
    private String companynameplaceholder;
    private String businessemail;
    private String businessphone;
    private String preferreddate;
    private String preferredtime;
    private String preferredtimeplaceholder;
    private String painpointlabel;
    private String painpointplaceholder;
    private String painpointconfirm;
    private String painpointclear;
    private String painpointsingular;
    private String painpointplural;
    private String caslconsent;
    private String ctaText;
    private String successTitle;
    private String successPrimaryText;
    private String successSecondaryText;
    private String pageLanguage;
    private String phoneplaceholder;
    private String languageAbbreviation;

    private String eloquaFormName;
    private String eloquaSiteId;
    private String eloquaFormSubmitURL;
    private String eloquaFormId;

    private List<TimeOptions> timeitems;
    private Map<String,String> painpointitems = new HashMap<>();

    /**
     * Populates the Model with values from the applicable ContentPolicy (based on current path language).
     *
     */
    @PostConstruct
    public void initModel() {
        ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(this.request, this.currentPage);
        if (contentPolicy != null) {
            ValueMap policy_properties = contentPolicy.getProperties();
            String pagePath = currentPage.getPath();

            this.languageAbbreviation        = LanguageUtil.getLanguageAbbreviationFromPath(pagePath, ConstantsForm.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
            this.title                       = policy_properties.get(ConstantsForm.FormProperties.TITLE, String.class);
            this.inquiryID                   = policy_properties.get(ConstantsForm.FormProperties.FORM_HIDDEN_INQUIRYID, String.class);
            this.firstname                   = policy_properties.get(ConstantsForm.FormProperties.FORM_HTML_FIRSTNAME, String.class);
            this.companynamelabel            = policy_properties.get(ConstantsForm.FormProperties.FORM_HTML_COMPANYNAMELABEL, String.class);
            this.companynameplaceholder      = policy_properties.get(ConstantsForm.FormProperties.FORM_HTML_COMPANYNAMEPLACEHOLDER, String.class);
            this.businessemail               = policy_properties.get(ConstantsForm.FormProperties.FORM_HTML_BUSINESSEMAIL, String.class);
            this.businessphone               = policy_properties.get(ConstantsForm.FormProperties.FORM_HTML_BUSINESSPHONE, String.class);
            this.preferreddate               = policy_properties.get(ConstantsForm.FormProperties.FORM_HTML_PREFERREDDATE, String.class);
            this.preferredtime               = policy_properties.get(ConstantsForm.FormProperties.FORM_HTML_PREFERREDTIME, String.class);
            this.preferredtimeplaceholder    = policy_properties.get(ConstantsForm.FormProperties.FORM_HTML_PREFERREDTIMEPLACEHOLDER, String.class);
            this.painpointlabel              = policy_properties.get(ConstantsForm.FormProperties.FORM_HTML_PAINPOINTLABEL, String.class);
            this.painpointplaceholder        = policy_properties.get(ConstantsForm.FormProperties.FORM_HTML_PAINPOINTPLACEHOLDER, String.class);
            this.painpointconfirm            = policy_properties.get(ConstantsForm.FormProperties.FORM_HTML_PAINPOINTCONFIRM, String.class);
            this.painpointclear              = policy_properties.get(ConstantsForm.FormProperties.FORM_HTML_PAINPOINTCLEAR, String.class);
            this.painpointsingular           = policy_properties.get(ConstantsForm.FormProperties.FORM_HTML_PAINPOINTSINGULAR, String.class);
            this.painpointplural             = policy_properties.get(ConstantsForm.FormProperties.FORM_HTML_PAINPOINTPLURAL, String.class);
            this.caslconsent                 = policy_properties.get(ConstantsForm.FormProperties.FORM_HIDDEN_CASLCONSENT, String.class);
            this.ctaText                     = policy_properties.get(ConstantsForm.FormProperties.FORM_HTML_CTATEXT, String.class);
            this.successTitle                = policy_properties.get(ConstantsForm.FormProperties.FORM_HTML_SUCCESSTITLE, String.class);
            this.successSecondaryText        = policy_properties.get(ConstantsForm.FormProperties.FORM_HTML_SUCCESSSECONDARYTEXT, String.class);
            this.phoneplaceholder            = policy_properties.get(ConstantsForm.FormProperties.FORM_HTML_PHONEPLACEHOLDER, String.class);
            this.successPrimaryText          = formatSuccessMessage(policy_properties.get(ConstantsForm.FormProperties.FORM_HTML_SUCCESSPRIMARYTEXT, String.class));

            // Special String format required by eloqua form
            if(this.languageAbbreviation.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_EN)) {
                this.pageLanguage = ConstantsForm.FormProperties.FORM_HIDDEN_ENGLISH;
            }
            else if(this.languageAbbreviation.equalsIgnoreCase(ConstantsForm.FormProperties.FORM_FR)) {
                this.pageLanguage = ConstantsForm.FormProperties.FORM_HIDDEN_FRENCH;
            }

            // Configs for Eloqua form
            this.eloquaFormName              = eloquaService.getScheduleCallFormName()==null?"":eloquaService.getScheduleCallFormName();
            this.eloquaSiteId                = eloquaService.getSiteId()==null?"":eloquaService.getSiteId();
            this.eloquaFormSubmitURL         = eloquaService.getFormSubmitURL()==null?"":eloquaService.getFormSubmitURL();
            this.eloquaFormId                = eloquaService.getScheduleCallFormId()==null?"":eloquaService.getScheduleCallFormId();

            Resource policyResource = contentPolicy.adaptTo(Resource.class);
            // Get the options for preferred time
            Resource timeOptionsResource = policyResource.getChild(ConstantsForm.FormProperties.FORM_HTML_TIMEITEMS);
            this.timeitems = populateTimeList(timeOptionsResource);
            // Get pain point options from Category Tags
            this.painpointitems = FormFieldsUtil.getPainPointListMap(this.request);
        }

        String customInquiryId = properties.get(ConstantsForm.FormProperties.NEWSLETTER_CUSTOMINQUIRYID,  String.class);
        if(customInquiryId != null && customInquiryId.trim().length()>0 ){
            this.inquiryID = customInquiryId;
        }
    }

    /**
     *
     * @param message the text to be formatted
     * @return Formatted text for form success primary message
     */
    private String formatSuccessMessage(String message) {
        return message.replace(ConstantsForm.FormProperties.FORM_HTML_DATEPLACEHOLDER, ConstantsForm.FormProperties.FORM_HTML_DATEHTMLTAG).replace(ConstantsForm.FormProperties.FORM_HTML_TIMEPLACEHOLDER, ConstantsForm.FormProperties.FORM_HTML_TIMEHTMLTAG);
    }

    /**
     *
     * @param resource
     * @return list of options for preferred time
     */
    private List<TimeOptions> populateTimeList(Resource resource) {
        List<TimeOptions> listOfOptions = new ArrayList<>();
        if (resource != null) {
            Iterator<Resource> resources = resource.listChildren();
            while (resources.hasNext()) {
                TimeOptions item = resources.next().adaptTo(TimeOptions.class);
                listOfOptions.add(item);
            }
        }
        return listOfOptions;
    }

    /**
     *
     * @return the form title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     *
     * @return the inquiry Id
     */
    public String getInquiryID() {
        return this.inquiryID;
    }

    /**
     *
     * @return the label for first name input
     */
    public String getFirstname() {
        return this.firstname;
    }

    /**
     *
     * @return the label for the company name input
     */
    public String getCompanynamelabel() {
        return this.companynamelabel;
    }

    /**
     *
     * @return the placeholder for the company name input
     */
    public String getCompanynameplaceholder() {
        return this.companynameplaceholder;
    }

    /**
     *
     * @return the label for the business email address
     */
    public String getBusinessemail() {
        return this.businessemail;
    }

    /**
     *
     * @return the label for the business phone number
     */
    public String getBusinessphone() {
        return this.businessphone;
    }

    /**
     *
     * @return the placholder for the business phone number
     */
    public String getPhoneplaceholder() {
        return this.phoneplaceholder;
    }

    /**
     *
     * @return the label for the preferred date
     */
    public String getPreferreddate() {
        return this.preferreddate;
    }

    /**
     *
     * @return the label for the preferred time
     */
    public String getPreferredtime() {
        return this.preferredtime;
    }

    /**
     *
     * @return the placeholder for the preferred time
     */
    public String getPreferredtimeplaceholder() {
        return this.preferredtimeplaceholder;
    }

    /**
     *
     * @return the list of options for the preferred time
     */
    public List<TimeOptions> getTimeitems() {
        return this.timeitems;
    }

    /**
     *
     * @return the label for the painpoint question
     */
    public String getPainpointlabel() {
        return this.painpointlabel;
    }

    /**
     *
     * @return the placeholder for the painpoint question
     */
    public String getPainpointplaceholder() {
        return this.painpointplaceholder;
    }

    /**
     *
     * @return the list of options for the painpoint question
     */
    public Map<String,String> getPainpointitems() {
        return this.painpointitems;
    }

    /**
     *
     * @return the confirm message for painpoint dropdown
     */
    public String getPainpointconfirm() {
        return painpointconfirm;
    }

    /**
     *
     * @return the clear all message for painpoint dropdown
     */
    public String getPainpointclear() {
        return painpointclear;
    }

    /**
     *
     * @return the singular selected message for painpoint dropdown
     */
    public String getPainpointsingular() {
        return painpointsingular;
    }

    /**
     *
     * @return the plural selected message for painpoint dropdown
     */
    public String getPainpointplural() {
        return painpointplural;
    }

    /**
     *
     * @return the text for the caslconsent
     */
    public String getCaslconsent() {
        return this.caslconsent;
    }

    /**
     *
     * @return the text for the CTA button
     */
    public String getCtaText() {
        return this.ctaText;
    }

    /**
     *
     * @return the title for the success message
     */
    public String getSuccessTitle() {
        return this.successTitle;
    }

    /**
     *
     * @return the primary text for the success message
     */
    public String getSuccessPrimaryText() {
        return this.successPrimaryText;
    }

    /**
     *
     * @return the secondary text for the success message
     */
    public String getSuccessSecondaryText() {
        return this.successSecondaryText;
    }

    /**
     *
     * @return the page language
     */
    public String getPageLanguage() {
        return this.pageLanguage;
    }

    /**
     *
     * @return the Eloqua form name
     */
    public String getEloquaFormName(){
        return this.eloquaFormName;
    }

    /**
     *
     * @return the value for elqSiteId
     */
    public String getEloquaSiteId() {
        return this.eloquaSiteId;
    }

    /**
     *
     * @return the Eloqua submit url
     */
    public String getEloquaFormSubmitURL(){
        return this.eloquaFormSubmitURL;
    }

    /**
     *
     * @return the Eloqua form Id
     */
    public String getEloquaFormId() {
        return this.eloquaFormId;
    }

    /**
     *
     * @return the language abbreviation
     */
    public String getLanguageAbbreviation() {
        return this.languageAbbreviation;
    }
}
