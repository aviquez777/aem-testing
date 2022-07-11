package com.edc.edcportal.core.models;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.edc.edcportal.core.eloqua.EloquaConnectionManagerConstants;
import com.edc.edcportal.core.eloqua.EloquaConnectionManagerUtil;
import com.edc.edcportal.core.eloqua.pojo.EloquaDataItem;
import com.edc.edcportal.core.eloqua.pojo.EloquaUserProfileDO;
import com.edc.edcportal.core.eloqua.services.EloquaDAOService;
import com.edc.edcportal.core.eloqua.services.EloquaDAOUtils;
import com.edc.edcportal.core.exception.EDCEloquaSystemException;
import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.FormFieldDefinitionsUtil;
import com.edc.edcportal.core.helpers.FormValidationHelper;
import com.edc.edcportal.core.helpers.LanguageUtil;
import com.edc.edcportal.core.helpers.LoginRequestHeadersUtil;
import com.edc.edcportal.core.helpers.RedirectHelper;
import com.edc.edcportal.core.services.FieldMappingConfigService;
import com.edc.edcportal.core.services.MyEDCConfigSystemService;

@Model(adaptables = SlingHttpServletRequest.class)

/**
 * Model to prepare the data needed for the presentation leyar on Manage Profile
 * / Create account form
 * 
 *
 */

public class ManageProfile {

    private static final Logger logger = LoggerFactory.getLogger(ManageProfile.class);

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private SlingHttpServletResponse response;

    @Inject
    private EloquaDAOService eloquaDAOService;

    @Inject
    private FieldMappingConfigService fieldMappingConfigService;

    @Inject
    private Page currentPage;

    @Inject
    private MyEDCConfigSystemService myEDCConfigSystemService;

    ProfileTypeDefinition profileTypeDefinition = new ProfileTypeDefinition();
    EloquaUserProfileDO eloquaUserProfileDO;
    String profileType = null;
    private String profilePageLink;
    private String editBasicInformationLink;
    private String editDetailedInformationLink;

    Map<String, String> formFields = new LinkedHashMap<>();
    Map<String, Map<String, Boolean>> formFieldErrors = new LinkedHashMap<>();
    Map<String, String> headers = new LinkedHashMap<>();
    Map<String, FormFieldDefintion> formFieldDefinitions;
    String enLowerCaseAbb = Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation().toLowerCase();
    String pageLanguage = enLowerCaseAbb;

    @PostConstruct
    public void initModel() { // NOSONAR
        // Get page language
        pageLanguage = LanguageUtil.getLanguageAbbreviationFromPath(currentPage.getPath(),
                Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
        // Get external system error page path
        String externalErrorPath = pageLanguage.equals(enLowerCaseAbb) ? Constants.Paths.MYACCOUNT_SYSTEM_ERROR_PATH_EN
                : Constants.Paths.MYACCOUNT_SYSTEM_ERROR_PATH_FR;
        String redirectTo = null;
        boolean showErrors = (request.getSession().getAttribute(Constants.Properties.PROFILE_HAS_ERRORS) != null);
        try {
            try { // NOSONAR
                headers = LoginRequestHeadersUtil.getHeaders(request);
                String externalId = headers.get(Constants.Properties.HEADER_EXTERNAL_ID);
                eloquaUserProfileDO = eloquaDAOService.getUserProfileByExternalId(externalId);
                profileType = EloquaConnectionManagerUtil.getELoquaValueFromDO(eloquaUserProfileDO,
                        eloquaDAOService.getEloquaConfigService().getProfileFieldId());
                profileTypeDefinition = FormFieldDefinitionsUtil.getProfileTypeDefinition(request, profileType);
                // get the fields mapping for the profile type
                Map<String, String> fieldMap = FormFieldDefinitionsUtil.getFieldMapping(request,
                        profileTypeDefinition.getFormFields(), fieldMappingConfigService);
                eloquaUserProfileDO = EloquaDAOUtils.updateProfileDOWithFieldData(eloquaUserProfileDO, fieldMap);
                Map<String, EloquaDataItem> formFieldValues = eloquaUserProfileDO.getFormFieldsValues();
                formFieldDefinitions = FormFieldDefinitionsUtil.getDefinitions(request, fieldMappingConfigService);
                String selectedCountry = null;
                // convert Map<String, String, for easy of use
                for (Map.Entry<String, String> item : fieldMap.entrySet()) {
                    String key = item.getKey();
                    if (formFieldValues.containsKey(key)) {
                        String fieldName = formFieldValues.get(key).getFormFieldName();
                        String fieldValue = formFieldValues.get(key).getEloquaValue();
                        // use it if is not one of the fields from the headers
                        if (StringUtils.isNotBlank(fieldName) && !headers.containsKey(fieldName)) {
                            formFields.put(fieldName, fieldValue);
                            // add to the errors
                            if (showErrors) {
                                FormFieldDefintion formFieldDefinition = formFieldDefinitions.get(fieldName);
                                boolean fieldIsRequired = formFieldDefinition.getIsRequired();
                                if (Constants.Properties.COUNTRY_FIELD.equals(fieldName)) {
                                    selectedCountry = fieldValue;
                                }
                                if (StringUtils.isNotBlank(selectedCountry)) {
                                    fieldIsRequired = FormValidationHelper.checkReqByCountry(selectedCountry, fieldName,
                                            fieldIsRequired);
                                }
                                Map<String, Boolean> validationsResults = FormValidationHelper.checkRules(fieldValue,
                                        fieldIsRequired, formFieldDefinition);
                                formFieldErrors.put(fieldName, validationsResults);
                            }
                        }
                    } else {
                        // Special case for Province and State
                        String fieldName = item.getValue();
                        String fieldValue = "";
                        String provinceKey = eloquaDAOService.getEloquaConfigService().getProvinceIdFieldId();
                        // Set the state, if not state it will not get selected
                        if (fieldName.equals(EloquaConnectionManagerConstants.STATE_FIELD_NAME)) {
                            fieldValue = formFieldValues.get(provinceKey).getEloquaValue();
                        }
                        // Set the state, if not state it will not get selected
                        if (fieldName.equals(EloquaConnectionManagerConstants.PROVINCE_INPUT_FIELD_NAME)) {
                            fieldValue = formFieldValues.get(provinceKey).getEloquaValue();
                        }
                        formFields.put(fieldName, fieldValue);
                    }
                }
                // marketsInt is the field that all the forms should have full, so if the field
                // has data, MyEDC profile should be full
                // if so, use the data already on the eloquaUserProfileDO which comes from MyEDC
                // CDO
                // Otherwise get the data from Progressive Profile CDO Using email
                // Task 314271
                String marketsInt = eloquaDAOService.getEloquaConfigService().getMarketsIntFieldId();
                String marketsOfInterest = eloquaUserProfileDO.getFormFieldsValues().get(marketsInt).getEloquaValue();
                // End Task 314271
                if (FormValidationHelper.checkMarketsOfInterest(profileType, marketsOfInterest)) {
                    String email = headers.get(Constants.Properties.HEADER_EMAIL_ID);
                    formFields = eloquaDAOService.prePopulatePPDataByEMailId(formFields, email,
                            fieldMappingConfigService);
                }
                if (getLanguage().equals("en")) {
                    this.editBasicInformationLink = Constants.Paths.ACCOUNT_BASIC_EDIT_EN;
                    this.editDetailedInformationLink = "/en" + Constants.Paths.MYACCOUNT + Constants.Paths.EDIT_PROFILE
                            + Constants.HTML_EXTENSION;
                    this.profilePageLink = "/en" + Constants.Paths.MYACCOUNT + Constants.Paths.MYEDC_HOME_PAGE
                            + Constants.HTML_EXTENSION;
                } else {
                    this.editBasicInformationLink = Constants.Paths.ACCOUNT_BASIC_EDIT_FR;
                    this.editDetailedInformationLink = "/fr" + Constants.Paths.MYACCOUNT_FR
                            + Constants.Paths.EDIT_PROFILE_FR + Constants.HTML_EXTENSION;
                    this.profilePageLink = "/fr" + Constants.Paths.MYACCOUNT_FR + Constants.Paths.MYEDC_HOME_PAGE_FR
                            + Constants.HTML_EXTENSION;
                }
            } catch (EDCEloquaSystemException e) {
                logger.error(e.toString());
                redirectTo = externalErrorPath;
                RedirectHelper.redirectTo(response, redirectTo);
            }
            // Catch exception from RedirectHelper.redirectTo()
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public Map<String, String> getFormFields() {
        return formFields;
    }

    public Map<String, Map<String, Boolean>> getFormFieldErrors() {
        return formFieldErrors;
    }

    public Map<String, FormFieldDefintion> getFieldDefinitions() {
        return formFieldDefinitions;
    }

    public String getLanguage() {
        return LanguageUtil.getLanguage(request);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getProfileType() {
        return profileType;
    }

    public String getPostUrl() {
        return Constants.Paths.FRONT_CONTROLLER_SERVLET_URL;
    }

    public String getEditBasicInformationLink() {
        return this.editBasicInformationLink;
    }

    public String getEditDetailedInformationLink() {
        return this.editDetailedInformationLink;
    }

    public boolean showTC() {
        return currentPage.getPath().contains(Constants.Paths.REGISTER);
    }

    public String getProfilePageLink() {
        return profilePageLink;
    }

    public String getAddressCompleteCSSUrl() {
        return myEDCConfigSystemService.getAddressCompleteCSSUrl();
    }

    public String getAddressCompleteJSUrl() {
        return myEDCConfigSystemService.getAddressCompleteJSUrl();
    }

    public String getAddressCompleteKey() {
        return myEDCConfigSystemService.getAddressCompleteKey();
    }

    public String getAddressCompleteService() {
        return myEDCConfigSystemService.getAddressCompleteService();
    }

    public boolean isRenewal() {
        return currentPage.getPath().contains(Constants.Paths.RENEWAL);
    }

    public String getFormTitle() {
        return pageLanguage.equalsIgnoreCase(enLowerCaseAbb) ? profileTypeDefinition.getEnTitle()
                : profileTypeDefinition.getFrTitle();
    }

    public String getFormSubtitle() {
        return pageLanguage.equalsIgnoreCase(enLowerCaseAbb) ? profileTypeDefinition.getEnSubtitle()
                : profileTypeDefinition.getFrSubtitle();
    }

    public boolean getShowAutoComplete() {
        return profileTypeDefinition.getShowAutoComplete();
    }

}
