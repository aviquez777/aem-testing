package com.edc.edcweb.core.models.progressiveprofiling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.EbookHelper;
import com.edc.edcweb.core.helpers.FormFieldsUtil;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.helpers.SignInHelper;
import com.edc.edcweb.core.helpers.emailHasEloquaAccount;
import com.edc.edcweb.core.helpers.constants.ConstantsEbook;
import com.edc.edcweb.core.helpers.constants.ConstantsForm;
import com.edc.edcweb.core.helpers.progressiveprofiling.ProgressiveProfilingFormData;
import com.edc.edcweb.core.helpers.progressiveprofiling.ProgressiveProfilingHelper;
import com.edc.edcweb.core.helpers.progressiveprofiling.ProgressiveProfilingPremiumPageHelper;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 *
 *   Model for Progressive Profiling component. Contains the profiling form content.
 *
 */

@Model(adaptables = SlingHttpServletRequest.class)
public class ProgressiveProfiling
{
    private static final Logger log = LoggerFactory.getLogger(ProgressiveProfiling.class);

    //use a map to share values to the htl component
    private Map<String, Object> propertiesMap = new HashMap<>();

    private ProgressiveProfilingFormData formData;
    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private SlingHttpServletResponse response;

    @Inject
    @Optional
    private Page currentPage;
    private String inquiryID;
    private String premiumUrl;
    private String postUrl;
    private String refPremiumURL;
    private String userStatusServiceUrl;
    private Integer accessLevel;
    private String levelFive;
    private Boolean isLevelFive;

    @ScriptVariable
    private ValueMap properties;

    private String docID;

    @Inject
    @Optional
    private String mode;

    // Access level
    private String sneakPeek;
    private String assetTier;

    private boolean needHideComponent = false;

    // Button url values
    private String registerLinkUrl;
    private String loginLinkUrl;

    // 58406 Default texts from policy but might be overridden
    private String heading;
    private String subtitle;
    private String disclaimer;
    private String submitButton;
    private String cancelButton;
    private String headingLoggedIn;
    private String subtitleLoggedIn;
    private String submitButtonLoggedIn;
    private String submitButtonL5;

    // 58406 Default texts from policy If Level5 use level5 text
    private String upsellTitle;
    private String upsellDesc;
    private String upsellCtaText;
    private String upsellOptLinkText;
    private String upsellOptLinkUrl;
    // 58406 Default texts from policy If Level5 use level5 text
    private String upsellTitleL5;
    private String upsellDescL5;
    private String upsellCtaTextL5;
    private String upsellOptLinkTextL5;
    private String premUrl;
    // 61416-data-layer-eventValue1-2 
    private String productTypeValue;
    private String productDescValue;
    /**
     * Populates the model.
     *
     */
    @PostConstruct
    public void initModel() {

        log.debug(" initModel  " );

        this.mode = properties.get("mode", String.class);
        this.sneakPeek = request.getParameter("sneakPeek");
        this.assetTier = properties.get("assetTier", String.class);
        this.levelFive = "";
        this.isLevelFive = false;
        this.userStatusServiceUrl = Constants.Paths.USER_STATUS_SERVICE_URL;
        // US 144669
        boolean isCRG = Constants.Properties.PROGRESSIVEPROFILING_MODE_CRG.equals(mode);

        //Get referral url if exist. This value is passed through url parameter
        this.refPremiumURL = request.getParameter("refPremiumURL");

        //get the mode injected from the dialog or initialize to normal
        if (this.mode == null || this.mode.isEmpty()){
            this.mode = Constants.Properties.PROGRESSIVEPROFILING_MODE_NORMAL;
        }
        if ( this.mode.equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_MEA)){
            this.premiumUrl = "";
            this.docID = Constants.Properties.PROGRESSIVEPROFILING_MODE_MEA_MEAUNLOCKED_DOCID;
        }
        else if(mode.equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_EBOOK_REGULAR) ) {
            String previewOnlyInSession = (String) request.getSession().getAttribute(ConstantsEbook.SessionAttr.PREVIEW_ONLY_ATTR);
            if ( currentPage.getTemplate().getPageTypePath().equals(ConstantsEbook.Paths.EBOOK_PAGE_TYPE) && previewOnlyInSession == null) {
                //When current PP component is in the chapter page, if session don't have 'preview', hide PP component.
                //do not hide for author in edit mode
                WCMMode wcmMode = WCMMode.fromRequest(request);
                if (wcmMode != WCMMode.EDIT)
                {
                    this.needHideComponent = true;
                }

            }

            //docID come from PP component property. Should set same in PP of chapter also
            this.docID = properties.get("docID", "");
            String firstChapterInternalUrl = properties.get(Constants.Properties.PROGRESSIVEPROFILING_PREMIUMURL, EbookHelper.getFirstChapterURLFromCurrentPage(currentPage,null));
            firstChapterInternalUrl =  addHTMLExtentiontoURL(firstChapterInternalUrl);
            this.premiumUrl = firstChapterInternalUrl;

        } else if(mode.equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_EBOOK_PERSONA)) {
            /*
                For persona ebook we don't know the docId and premium until form submission
                In this case, these values depend on the user selection
             */
            this.premiumUrl = "";
            this.docID = "";
            // Get premium asset level from persona button properties
            this.assetTier = EbookHelper.getAttributeFromPersonaButton(currentPage, Constants.Properties.ASSET_TIER);
        } else{
            //Progressive Profiling Mode = Normal
            String internalPremiumUrl = properties.get(Constants.Properties.PROGRESSIVEPROFILING_PREMIUMURL, ProgressiveProfilingPremiumPageHelper.getInternalPremiumUrlFromTeaser(request));
            internalPremiumUrl =  addHTMLExtentiontoURL(internalPremiumUrl);
            this.docID = ProgressiveProfilingPremiumPageHelper.getDocIDFromPremiumUrl(request, internalPremiumUrl);
            this.premiumUrl = internalPremiumUrl;

            log.debug(" getDocIDFromPremiumUrl {}  ", this.docID  );

            //Normal Mode PP in MEA Teaser:Hide the component if session contains MEA-*(not MEA-USA): Country grid is unlocked now.
            checkHiddenRequiredInMEATeaser();

        }

        // Get access level of content in order to show/hide content
        this.accessLevel = emailHasEloquaAccount.getAccessLevel(request, this.mode, this.sneakPeek, this.premiumUrl, this.assetTier);

        // Get product description based on resource path
        Resource res = currentPage.adaptTo(Resource.class);
        String[] prodTypeDesc = ProgressiveProfilingHelper.getProductTypeAndDesc(res, this.docID);
        productTypeValue  = prodTypeDesc[0];
        productDescValue = prodTypeDesc[1];

        // Generate login/sign-up url for buttons with product parameters
        if(refPremiumURL!=null && !refPremiumURL.isEmpty() && !refPremiumURL.equalsIgnoreCase("null")) {
            this.premUrl = refPremiumURL;
        } else {
            this.premUrl = premiumUrl;
        }
        // Use internal premium link for the buttons if none set at this point
        if(StringUtils.isBlank(premUrl)) {
            this.premUrl = properties.get(Constants.Properties.PROGRESSIVEPROFILING_PREMIUMURL, ProgressiveProfilingPremiumPageHelper.getInternalPremiumUrlFromTeaser(request));
        }
        // force full url for login and sign in buttons
        boolean noRedirectBack = Boolean.parseBoolean(properties.get(Constants.Properties.PROGRESSIVEPROFILING_NO_REDIRECT_BACK, "false"));
        this.registerLinkUrl = SignInHelper.buildSignInQueryString(request, this.premUrl, productTypeValue, productDescValue, true, true, 5, noRedirectBack);
        this.loginLinkUrl = SignInHelper.buildSignInQueryString(request, this.premUrl, productTypeValue, productDescValue, false, true, 5, noRedirectBack);
        // US 144669 foce level 5
        if (this.accessLevel == 5 || isCRG) {
            this.isLevelFive = true;
        }
        this.levelFive = Boolean.toString(isLevelFive);


        //get the premiumURL from the dialog or from the related premium page
        log.debug(" PremiumUrl {}  ", this.premiumUrl  );
        log.debug(" mode {}  ", this.mode  );
        this.postUrl = Constants.Paths.PROGRESSIVEPROFILING_POSTSERVLET;
        log.debug(" postUrl {}  ", this.postUrl  );

        ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(this.request, this.currentPage);
        if ( contentPolicy != null )
        {
            log.debug("got contentPolicy");
            populateInputFieldsFromPolicy(contentPolicy);
            populateUIFromPolicy(contentPolicy);
        }
        else
        {
            log.debug("Error: contentPolicy is null!");
        }

        String customInquiryId = properties.get(Constants.Properties.NEWSLETTER_CUSTOMINQUIRYID,  String.class);
        if(customInquiryId != null && customInquiryId.trim().length()>0 ){
            this.inquiryID = customInquiryId;
        }
    }

    /**
     * Populates the form data content based on the content policy values
     *
     */
    private void populateInputFieldsFromPolicy(ContentPolicy contentPolicy) {
        formData  = new ProgressiveProfilingFormData();
        if (contentPolicy != null) {
            log.debug("populateModel InitializeFromPolicyContent");
            formData.initializeInputFieldsFromPolicyContent(request, contentPolicy);
        }
        else
        {
            log.debug("Error: populateModel contentPolicy is null!");
        }

    }

    /**
     * Populates the properties map for the component from policy
     *
     */
    private void populateUIFromPolicy(ContentPolicy contentPolicy )
    {
        ValueMap policy = contentPolicy.getProperties();
        if (properties != null) {
            log.debug("contentPolicy != null ");

            this.inquiryID = properties.get(ConstantsForm.FormProperties.FORM_HIDDEN_INQUIRYID, String.class);

            this.propertiesMap.put(Constants.Properties.PROGRESSIVEPROFILING_NEXTBUTTON, policy.get(Constants.Properties.PROGRESSIVEPROFILING_NEXTBUTTON,String.class));
            this.propertiesMap.put(Constants.Properties.PROGRESSIVEPROFILING_BACKBUTTON, policy.get(Constants.Properties.PROGRESSIVEPROFILING_BACKBUTTON,String.class));
            this.propertiesMap.put(Constants.Properties.PROGRESSIVEPROFILING_DONEBUTTON, policy.get(Constants.Properties.PROGRESSIVEPROFILING_DONEBUTTON,String.class));

            this.propertiesMap.put(Constants.Properties.PROGRESSIVEPROFILING_SUCCESSTITLE, policy.get(Constants.Properties.PROGRESSIVEPROFILING_SUCCESSTITLE,String.class));
            this.propertiesMap.put(Constants.Properties.PROGRESSIVEPROFILING_SUCCESSMSG, policy.get(Constants.Properties.PROGRESSIVEPROFILING_SUCCESSMSG,String.class));

            this.propertiesMap.put(Constants.Properties.PROGRESSIVEPROFILING_HEADINGP2, policy.get(Constants.Properties.PROGRESSIVEPROFILING_HEADINGP2,String.class));
            this.propertiesMap.put(Constants.Properties.PROGRESSIVEPROFILING_SUBTITLEP2, policy.get(Constants.Properties.PROGRESSIVEPROFILING_SUBTITLEP2,String.class));
            this.propertiesMap.put(Constants.Properties.PROGRESSIVEPROFILING_HEADINGP3, policy.get(Constants.Properties.PROGRESSIVEPROFILING_HEADINGP3,String.class));
            this.propertiesMap.put(Constants.Properties.PROGRESSIVEPROFILING_SUBTITLEP3, policy.get(Constants.Properties.PROGRESSIVEPROFILING_SUBTITLEP3,String.class));
            this.propertiesMap.put(Constants.Properties.PROGRESSIVEPROFILING_HEADINGP4, policy.get(Constants.Properties.PROGRESSIVEPROFILING_HEADINGP4,String.class));
            this.propertiesMap.put(Constants.Properties.PROGRESSIVEPROFILING_SUBTITLEP4, policy.get(Constants.Properties.PROGRESSIVEPROFILING_SUBTITLEP4,String.class));
            this.propertiesMap.put(Constants.Properties.PROGRESSIVEPROFILING_HEADINGP5, policy.get(Constants.Properties.PROGRESSIVEPROFILING_HEADINGP5,String.class));
            this.propertiesMap.put(Constants.Properties.PROGRESSIVEPROFILING_SUBTITLEP5, policy.get(Constants.Properties.PROGRESSIVEPROFILING_SUBTITLEP5,String.class));
            this.propertiesMap.put(Constants.Properties.PROGRESSIVEPROFILING_DISCLAIMER, policy.get(Constants.Properties.PROGRESSIVEPROFILING_DISCLAIMER,String.class));
            this.propertiesMap.put(Constants.Properties.PROGRESSIVEPROFILING_MODE, mode );
            // Field
            this.propertiesMap.put(Constants.Properties.PROGRESSIVEPROFILING_FIELD_LABEL, policy.get(Constants.Properties.PROGRESSIVEPROFILING_FIELD_LABEL,String.class));
            this.propertiesMap.put(Constants.Properties.PROGRESSIVEPROFILING_FIELD_HELP, policy.get(Constants.Properties.PROGRESSIVEPROFILING_FIELD_HELP,String.class));

            // 58406 Get Texts from policy
            this.heading = policy.get(Constants.Properties.PROGRESSIVEPROFILING_HEADING, String.class);
            this.subtitle = policy.get(Constants.Properties.PROGRESSIVEPROFILING_SUBTITLE, String.class);
            this.disclaimer = policy.get(Constants.Properties.PROGRESSIVEPROFILING_DISCLAIMER, String.class);
            this.upsellTitle = policy.get(Constants.Properties.PROGRESSIVEPROFILING_UPSELL_TITLE,String.class);
            this.submitButton =  policy.get(Constants.Properties.PROGRESSIVEPROFILING_SUBMITBUTTON, String.class);
            this.cancelButton = policy.get(Constants.Properties.PROGRESSIVEPROFILING_CANCELBUTTON, String.class);

            this.upsellDesc = policy.get(Constants.Properties.PROGRESSIVEPROFILING_UPSELL_DESCRIPTION, String.class);
            this.upsellCtaText = policy.get(Constants.Properties.PROGRESSIVEPROFILING_UPSELL_CTA_TEXT, String.class);
            this.upsellOptLinkText = policy.get(Constants.Properties.PROGRESSIVEPROFILING_UPSELL_OPT_LINK_TEXT, String.class);
            this.upsellOptLinkUrl = policy.get(Constants.Properties.PROGRESSIVEPROFILING_UPSELL_OPT_LINK_URL, String.class);

            this.upsellTitleL5 = policy.get(Constants.Properties.PROGRESSIVEPROFILING_UPSELL_TITLE_L5,String.class);
            this.upsellDescL5 = policy.get(Constants.Properties.PROGRESSIVEPROFILING_UPSELL_DESCRIPTION_L5, String.class);
            this.upsellCtaTextL5 = policy.get(Constants.Properties.PROGRESSIVEPROFILING_UPSELL_CTA_TEXT_L5, String.class);
            this.upsellOptLinkTextL5 = policy.get(Constants.Properties.PROGRESSIVEPROFILING_UPSELL_OPT_LINK_TEXT_L5, String.class);

            this.headingLoggedIn = policy.get(Constants.Properties.PROGRESSIVEPROFILING_HEADING_LOGGED_IN, String.class);
            this.subtitleLoggedIn = policy.get(Constants.Properties.PROGRESSIVEPROFILING_SUBTITLE_LOGGED_IN, String.class);
            this.submitButtonLoggedIn = policy.get(Constants.Properties.PROGRESSIVEPROFILING_SUBMIT_BUTTON_LOGGED_IN,String.class);

            this.submitButtonL5 = policy.get(Constants.Properties.PROGRESSIVEPROFILING_SUBMIT_BUTTON_L5,String.class);
        }
    }

    private void checkHiddenRequiredInMEATeaser() {

        String pageTypePath = currentPage.getTemplate().getPageTypePath();
        if( pageTypePath.equalsIgnoreCase(Constants.Paths.MEA_TEASER_PAGE_TYPE) ) {
            String attr = (String) request.getSession().getAttribute("accessGranted");
            if ( attr != null ){
                String regExp = "MEA-.*?";
                List<String> list = new ArrayList<>(Arrays.asList(attr.split(",")));

                for (int i = 0; i < list.size(); i++) {
                    //change country grid to unlock status only we have MEA-* (but not MEA-USA) in session's accessGranted attribute
                    if (!list.get(i).equalsIgnoreCase("MEA-USA") && list.get(i).matches( regExp ) ){
                        if ( WCMMode.fromRequest(request) != WCMMode.EDIT)
                        {
                            this.needHideComponent = true;
                            return;
                        }
                    }
                }
            }
         }
    }

    private String addHTMLExtentiontoURL(String link) {
    	String result = link;
    	String targetType = (String)properties.get("contenttype");
    	if (targetType != null && (targetType.isEmpty() || targetType.equalsIgnoreCase(".html"))){
    		 result = LinkResolver.addHtmlExtension(link);
    	}

    	return result;

    }

    private String checkOverride (String propertyName, String policyValue) {
        String authorVal = properties.get(propertyName, String.class);
        // If there's an authored value override policy value
        return StringUtils.isNotBlank(authorVal) ? authorVal : policyValue;
    }

    public Map<String, String>  getErrorMsgs() {
        return  FormFieldsUtil.getFormErrorsListMap(request);
    }

    public ProgressiveProfilingFormData getFormData(){
         return this.formData;
    }

    public Map<String, Object>  getPropertiesMap(){
        return this.propertiesMap;
    }

    public String getPremiumUrl(){
        return this.premiumUrl;
    }

    public String getExternalPremiumUrl(){
        String externalPremiumUrl = LinkResolver.addHtmlExtension(this.premiumUrl);
        externalPremiumUrl = LinkResolver.changeInternalURLtoExternal(request, externalPremiumUrl);
        return externalPremiumUrl;
    }

    public String getPostUrl(){
        return this.postUrl;
    }

    public String getInquiryID() {
        return this.inquiryID;
    }

    public String getDocID() {
        return this.docID;
    }

    public String getPremiumUrlFull(){
        return request.getScheme() + "://" + Constants.WHOS_ON_DOMAIN_PROD + this.premiumUrl.replace(Constants.Paths.CONTENT + Constants.Paths.EDC, "");
    }

    public String getMode(){
        return this.mode;
    }

    public Boolean getNeedHideComponent() {
        return needHideComponent;
    }

    public String getRefPremiumURL() {
    		return this.refPremiumURL;
    }

    public String getUserStatusServiceUrl() {
        return this.userStatusServiceUrl;
    }

    public String getLevelFive() {
        return this.levelFive;
    }

    public String getRegisterLinkUrl() {

        return this.registerLinkUrl;
    }

    public String getLoginLinkUrl() {
        // US 144669
//        if(isCRG) {
//            this.loginLinkUrl = StringUtils.remove(loginLinkUrl, Constants.Paths.PREMIUM);
//        }
        return this.loginLinkUrl;
    }

    public String getButtonLinkUrl() {
        return isLevelFive ? loginLinkUrl : registerLinkUrl;
    }

    //Default texts from policy If Level5 use level5 text
    public String getUpsellTitle() {
        return isLevelFive ? upsellTitleL5 : upsellTitle;
    }

    public String getUpsellDesc() {
        return isLevelFive ? upsellDescL5 : upsellDesc;
    }

    public String getUpsellCtaText() {
        return isLevelFive ? upsellCtaTextL5 : upsellCtaText;
    }

    public String getUpsellOptLinkText() {
        return isLevelFive ? upsellOptLinkTextL5 : upsellOptLinkText;
    }

    public String getUpsellOptLinkUrl() {
        return  StringUtils.isNotBlank(upsellOptLinkUrl) ? upsellOptLinkUrl : loginLinkUrl;
    }

    // Default texts from policy but might be overridden
    public String getHeading() {
        return checkOverride(Constants.Properties.PROGRESSIVEPROFILING_HEADING, heading);
    }

    public String getSubtitle() {
        return checkOverride(Constants.Properties.PROGRESSIVEPROFILING_SUBTITLE, subtitle);
    }

    public String getDisclaimer() {
        return checkOverride(Constants.Properties.PROGRESSIVEPROFILING_DISCLAIMER, disclaimer);
    }

    public String getSubmitButton() {
        return checkOverride(Constants.Properties.PROGRESSIVEPROFILING_SUBMITBUTTON, submitButton);
    }

    public String getSubmitButtonL5() {
        return checkOverride(Constants.Properties.PROGRESSIVEPROFILING_SUBMITBUTTON, submitButtonL5);
    }

    public String getCancelButton() {
        return checkOverride(Constants.Properties.PROGRESSIVEPROFILING_CANCELBUTTON, cancelButton);
    }

    public String getHeadingLoggedIn() {
        return checkOverride(Constants.Properties.PROGRESSIVEPROFILING_HEADING_LOGGED_IN, headingLoggedIn);
    }

    public String getSubtitleLoggedIn() {
        return checkOverride(Constants.Properties.PROGRESSIVEPROFILING_SUBTITLE_LOGGED_IN, subtitleLoggedIn);
    }

    public String getSubmitButtonLoggedIn() {
        return checkOverride(Constants.Properties.PROGRESSIVEPROFILING_SUBMIT_BUTTON_LOGGED_IN, submitButtonLoggedIn);
    }

    public String getPremUrl() {
        String premUrlShort = this.premUrl;
        if(StringUtils.isNotBlank(premUrlShort)) {
            premUrlShort = premUrlShort.replace(Constants.Paths.CONTENT_EDC, "");
        }
        return premUrlShort;
    }

    public String getProductTypeValue() {
        return this.productTypeValue;
    }

    public String getProductDescValue() {
        return this.productDescValue;
    }
}
