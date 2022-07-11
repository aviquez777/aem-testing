package com.edc.edcportal.core.models;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.eloqua.EloquaConnectionManagerConstants;
import com.edc.edcportal.core.eloqua.EloquaConnectionManagerUtil;
import com.edc.edcportal.core.eloqua.pojo.EloquaDataItem;
import com.edc.edcportal.core.eloqua.pojo.EloquaUserProfileDO;
import com.edc.edcportal.core.eloqua.services.EloquaDAOService;
import com.edc.edcportal.core.eloqua.services.EloquaDAOUtils;
import com.edc.edcportal.core.exception.EDCEloquaSystemException;
import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.FormFieldDefinitionsUtil;
import com.edc.edcportal.core.helpers.FormFieldsUtil;
import com.edc.edcportal.core.helpers.LanguageUtil;
import com.edc.edcportal.core.helpers.LoginRequestHeadersUtil;
import com.edc.edcportal.core.lovapi.service.LovApiDAOService;
import com.edc.edcportal.core.services.FieldMappingConfigService;

@Model(adaptables = SlingHttpServletRequest.class)
public class ManageMyAccount {
    private static final Logger log = LoggerFactory.getLogger(ManageMyAccount.class);

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private EloquaDAOService eloquaDAOService;

    @Inject
    private FieldMappingConfigService fieldMappingConfigService;

    @Inject
    private LovApiDAOService lovApiDAOService;

    EloquaUserProfileDO eloquaUserProfileDO;
    String profileType = null;
    Map<String, String> formFields = new LinkedHashMap<>();
    Map<String, String> headers = new LinkedHashMap<>();
    Map<String, FormFieldDefintion> formFieldDefinitions;

    private String editBasicInformationLink;
    // Task 21435 squid:S2068
    private String changePLink;
    private String editDetailedInformationLink;
    private String language;

    @PostConstruct
    public void initModel() {
        language = LanguageUtil.getLanguage(request);
        headers = LoginRequestHeadersUtil.getHeaders(request);
        String externalId = headers.get(Constants.Properties.HEADER_EXTERNAL_ID);
        try {
            eloquaUserProfileDO = eloquaDAOService.getUserProfileByExternalId(externalId);
        } catch (EDCEloquaSystemException e) {
            log.error(e.toString());
        }
        profileType = EloquaConnectionManagerUtil.getELoquaValueFromDO(eloquaUserProfileDO,
                eloquaDAOService.getEloquaConfigService().getProfileFieldId());
        ProfileTypeDefinition profileTypeDefinition = FormFieldDefinitionsUtil.getProfileTypeDefinition(request,
                profileType);
        // get the fields mapping for the profile type
        Map<String, String> fieldMap = FormFieldDefinitionsUtil.getFieldMapping(request,
                profileTypeDefinition.getFormFields(), fieldMappingConfigService);
        eloquaUserProfileDO = EloquaDAOUtils.updateProfileDOWithFieldData(eloquaUserProfileDO, fieldMap);
        Map<String, EloquaDataItem> formFieldValues = eloquaUserProfileDO.getFormFieldsValues();
        formFieldDefinitions = FormFieldDefinitionsUtil.getDefinitions(request, fieldMappingConfigService);
        String selectedCountry = "";
        // convert Map<String, String, for easy of use
        for (Map.Entry<String, String> item : fieldMap.entrySet()) {
            String key = item.getKey();
            if (formFieldValues.containsKey(key)) {
                String fieldName = formFieldValues.get(key).getFormFieldName();
                String fieldValue = formFieldValues.get(key).getEloquaValue();
                String fieldType = formFieldDefinitions.get(fieldName).getFieldType();
                String lovPath = formFieldDefinitions.get(fieldName).getLovPath();
                String lovApiName = formFieldDefinitions.get(fieldName).getLovApiName();
                //U S 321052 Financial Institution Profile Updates
                if (Constants.FI_PROFILE_TYPE.equals(profileType) && Constants.FI_PROFILE_TYPE_INSTITUTION_FIELD.equals(fieldName)) {
                    fieldValue = getValueFromLOV(Constants.FI_PROFILE_TYPE_INSTITUTION_FIELD_LOV, fieldValue, language);
                }
                //End US 321052 Financial Institution Profile Updates
                if ("companyCountry".equals(fieldName)) {
                    selectedCountry = fieldValue;
                }
                // Select
                if (Constants.FIELD_TYPE_SELECT.equals(fieldType)
                        && (!fieldName.equals(EloquaConnectionManagerConstants.STATE_FIELD_NAME)
                                || !fieldName.equals(EloquaConnectionManagerConstants.PROVINCE_INPUT_FIELD_NAME))) {
                    if (StringUtils.isNotBlank(lovApiName)) {
                        fieldValue = getValueFromLOV(lovApiName, fieldValue, language);
                    } else {
                        fieldValue = getSelectedLabels(fieldName, fieldValue, lovPath);
                    }
                }
                // MultiSelect
                if (Constants.FIELD_TYPE_MULTI_SELECT.equals(fieldType) && fieldValue != null) {
                    fieldValue = getMultiSelectLabels(fieldValue, lovPath, language);
                }
                // use it if is not one of the fields from the headers or is not hidden
                if (StringUtils.isNotBlank(fieldName) && !headers.containsKey(fieldName)
                        && !fieldType.equals(Constants.FIELD_TYPE_HIDDEN)) {
                    formFields.put(fieldName, fieldValue);
                }
            } else {
                // Special case for USA State, and other countries
                String fieldName = item.getValue();
                String fieldValue = "";
                String provinceKey = eloquaDAOService.getEloquaConfigService().getProvinceIdFieldId();
                // Set the state for USA
                if (fieldName.equals(EloquaConnectionManagerConstants.STATE_FIELD_NAME)
                        && "USA".equals(selectedCountry)) {
                    fieldValue = formFieldValues.get(provinceKey).getEloquaValue();
                    String lovPath = formFieldDefinitions.get(fieldName).getLovPath();
                    fieldValue = getSelectedLabels(fieldName, fieldValue, lovPath);
                }
                // Select the input field if is not usa or Canada
                if (fieldName.equals(EloquaConnectionManagerConstants.PROVINCE_INPUT_FIELD_NAME)
                        && !"CAN".equals(selectedCountry) && !"USA".equals(selectedCountry)) {
                    fieldValue = formFieldValues.get(provinceKey).getEloquaValue();
                }
                formFields.put(fieldName, fieldValue);
            }
        }

        if (getLanguage().equals("en")) {
            this.editBasicInformationLink = Constants.Paths.ACCOUNT_BASIC_EDIT_EN;
            this.changePLink = Constants.Paths.CHANGE_P_EN;
            this.editDetailedInformationLink = "/en" + Constants.Paths.MYACCOUNT + Constants.Paths.EDIT_PROFILE
                    + ".html";
        } else {
            this.editBasicInformationLink = Constants.Paths.ACCOUNT_BASIC_EDIT_FR;
            this.changePLink = Constants.Paths.CHANGE_P_FR;
            this.editDetailedInformationLink = "/fr" + Constants.Paths.MYACCOUNT_FR + Constants.Paths.EDIT_PROFILE_FR
                    + ".html";
        }
    }

    public Map<String, String> getFormFields() {
        return formFields;
    }

    public Map<String, FormFieldDefintion> getFieldDefinitions() {
        return formFieldDefinitions;
    }

    public String getLanguage() {
        return this.language;
    }

    public Map<String, String> getHeaders() {
        // remove "special" fields, so they wont show up
        headers.remove(Constants.Properties.HEADER_EXTERNAL_ID);
        headers.remove(Constants.Properties.HEADER_CREATED_DATE_TIME);
        return headers;
    }

    public String getProfileType() {
        return profileType;
    }

    public String getPostUrl() {
        return Constants.Paths.FRONT_CONTROLLER_SERVLET_URL;
    }

    private String getSelectedLabels(String fieldName, String fieldValue, String lovPath) {
        if (StringUtils.isBlank(lovPath)) {
            lovPath = Constants.DataPaths.MYEDCDATA + fieldName;
        }
        String[] values = fieldValue.split(EloquaConnectionManagerConstants.MULTI_VALUE_SEPARATOR);
        Map<String, String> options = FormFieldsUtil.getFormFieldOptionsMap(request, lovPath, false, null, null, null);
        fieldValue = null;
        for (String value : values) {
            if (fieldValue == null) {
                fieldValue = options.get(value);
            } else {
                String optionText = options.get(value);
                if (StringUtils.isNotBlank(optionText)) {
                    fieldValue = fieldValue.concat(", ").concat(optionText);
                }
            }
        }
        return fieldValue;
    }

    public String getEditBasicInformationLink() {
        return this.editBasicInformationLink;
    }

    public String getChangePLink() {
        return this.changePLink;
    }

    public String getEditDetailedInformationLink() {
        return this.editDetailedInformationLink;
    }

    private String getMultiSelectLabels(String fieldValue, String lovPath, String lang) {

        Map<String, Map<String, String>> listOfValues = FormFieldsUtil.getMutiSelectOptions(request, lovPath, lang);
        String[] selectedValues = fieldValue.split(EloquaConnectionManagerConstants.MULTI_VALUE_SEPARATOR);
        fieldValue = null;
        for (Entry<String, Map<String, String>> region : listOfValues.entrySet()) {
            Map<String, String> countryMap = region.getValue();
            for (String selCountry : selectedValues) {
                if (countryMap.containsKey(selCountry)) {
                    String optionText = countryMap.get(selCountry);
                    if (fieldValue == null) {
                        fieldValue = optionText;
                    } else {
                        fieldValue = fieldValue.concat(", ").concat(optionText);
                    }
                }
            }
        }
        return fieldValue;
    }

    private String getValueFromLOV(String codeTableName, String desc, String lang) {
        return lovApiDAOService.postLovSearchRequest(3, null, codeTableName, null, desc , lang);
    }

}