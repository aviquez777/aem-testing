package com.edc.edcportal.core.interceptors;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.eloqua.EloquaConnectionManagerUtil;
import com.edc.edcportal.core.eloqua.pojo.EloquaDataItem;
import com.edc.edcportal.core.eloqua.pojo.EloquaUserProfileDO;
import com.edc.edcportal.core.eloqua.services.EloquaDAOService;
import com.edc.edcportal.core.eloqua.services.EloquaDAOUtils;
import com.edc.edcportal.core.exception.EDCEloquaSystemException;
import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.FormFieldDefinitionsUtil;
import com.edc.edcportal.core.helpers.FormValidationHelper;
import com.edc.edcportal.core.helpers.LoginRequestHeadersUtil;
import com.edc.edcportal.core.models.FormFieldDefintion;
import com.edc.edcportal.core.services.FieldMappingConfigService;

/**
 *
 * Separated Eloqua logic to make the code more readable and compact
 *
 */
public class EloquaActions {

    protected static final Logger log = LoggerFactory.getLogger(EloquaActions.class);

    private EloquaActions() {
        // Sonar Lint
    }

    /**
     * checkEloquacheck if record exists, create it otherwise.
     *
     * @param externalId
     * @param headers
     * @param productType
     * @param productDesc
     * @param eloquaDAOService with the data (empty if new)
     * @return
     */
    public static EloquaUserProfileDO checkEloqua(String externalId, Map<String, String> headers, String productType,
            String productDesc, EloquaDAOService eloquaDAOService) throws EDCEloquaSystemException {
        EloquaUserProfileDO eloquaUserProfileDO = eloquaDAOService.getUserProfileByExternalId(externalId);
        // get headers for eloqua
        Map<String, String> headersForEloqua = LoginRequestHeadersUtil.getHeadersForEloqua(headers,
                eloquaDAOService.getEloquaConfigService());
        if (StringUtils.isBlank(eloquaUserProfileDO.getCdoRecordId())) {
            // add new record
            eloquaUserProfileDO = eloquaDAOService.createUserProfile(eloquaUserProfileDO, headersForEloqua);
            log.debug("AEMProfileInterceptor created account for {} ", externalId);
        }
        // Check if we have values on eloquaUserProfileDO for productType and
        // productDesc, if both empty on CDO, update
        if (StringUtils
                .isBlank(eloquaUserProfileDO.getFormFieldsValues()
                        .get(eloquaDAOService.getEloquaConfigService().getProductFieldId()).getEloquaValue())
                && StringUtils.isBlank(eloquaUserProfileDO.getFormFieldsValues()
                        .get(eloquaDAOService.getEloquaConfigService().getProductDescriptionFieldId())
                        .getEloquaValue())) {
            // add the profile type, alongside to the values to update
            headersForEloqua.put(eloquaDAOService.getEloquaConfigService().getProductFieldId(), productType);
            headersForEloqua.put(eloquaDAOService.getEloquaConfigService().getProductDescriptionFieldId(), productDesc);
            // add the headers as the values to update
            eloquaUserProfileDO = EloquaDAOUtils.updateProfileDOWithMap(eloquaUserProfileDO, headersForEloqua);
            // send it to REST
            if (StringUtils.isNotBlank(eloquaUserProfileDO.getCdoRecordId())) {
                eloquaDAOService.updateUserProfile(eloquaUserProfileDO);
                log.debug("AEMProfileInterceptor update productType and productDescritption {} ", externalId);
            }
        }
        return eloquaUserProfileDO;
    }

    /**
     * checkDataShareConsent adds partner to list of data share consent for a user if it's not already set
     *
     * @param externalId
     * @param headers
     * @param eloquaDAOService
     * @param dataShareConsent
     */
    public static void checkDataShareConsent(String externalId, Map<String, String> headers, EloquaDAOService eloquaDAOService, String dataShareConsent) throws EDCEloquaSystemException {
        EloquaUserProfileDO eloquaUserProfileDO = eloquaDAOService.getUserProfileByExternalId(externalId);
        // get headers for eloqua
        Map<String, String> headersForEloqua = LoginRequestHeadersUtil.getHeadersForEloqua(headers,
                eloquaDAOService.getEloquaConfigService());

        // Check if we have a value on eloquaUserProfileDO for dataShareConsent
        String dataShareConsentEloqua = eloquaUserProfileDO.getFormFieldsValues().get(eloquaDAOService.getEloquaConfigService().getDataShareConsent()).getEloquaValue();

        // add the new partner only if it's not already there
        if (StringUtils.isBlank(dataShareConsentEloqua) || !dataShareConsentEloqua.contains(dataShareConsent)) {
            // Task 22143 squid:S864
            String newConsent = StringUtils.isBlank(dataShareConsentEloqua) ? dataShareConsent : dataShareConsentEloqua.concat(Constants.Properties.ELOQUA_DELIMITER).concat(dataShareConsent);
            headersForEloqua.put(eloquaDAOService.getEloquaConfigService().getDataShareConsent(), newConsent);
            // add the headers as the values to update
            eloquaUserProfileDO = EloquaDAOUtils.updateProfileDOWithMap(eloquaUserProfileDO, headersForEloqua);
            // send it to REST
            eloquaDAOService.updateUserProfile(eloquaUserProfileDO);
            log.debug("AEMProfileInterceptor update data share consent {} ", externalId);
        }
    }

    /**
     * registrationComplete check all the prefile's required fields and break if
     * there's at least one empty.
     *
     * @param eloquaUserProfileDO
     * @param eloquaDAOService
     * @param fieldMappingConfigService
     * @param request
     * @return true if all the prefile's required fields have data, false otherwise
     */
    public static Boolean registrationComplete(EloquaUserProfileDO eloquaUserProfileDO,
            EloquaDAOService eloquaDAOService, FieldMappingConfigService fieldMappingConfigService,
            SlingHttpServletRequest request) {
        Boolean regIsComplete = true;
        // get profile type
        String profileType = EloquaConnectionManagerUtil.getELoquaValueFromDO(eloquaUserProfileDO,
                eloquaDAOService.getEloquaConfigService().getProfileFieldId());
        // get the fields mapping, definitions and values for the profile type
        Map<String, String> fieldMap = FormFieldDefinitionsUtil.getFieldMapping(request, profileType,
                fieldMappingConfigService);
        Map<String, FormFieldDefintion> fieldDefinitions = FormFieldDefinitionsUtil.getDefinitions(request,
                fieldMappingConfigService);
        Map<String, EloquaDataItem> formFieldValues = eloquaUserProfileDO.getFormFieldsValues();
        // convert Map<String, String, for easy of use
        String selectedCountry = null;
        String key;
        String value;
        for (Map.Entry<String, String> item : fieldMap.entrySet()) {
            key = item.getKey();
            value = item.getValue();
            if (formFieldValues.containsKey(key) && fieldDefinitions.containsKey(value)) {
                // get field value and eloquaId
                String fieldValue = formFieldValues.get(key).getEloquaValue();
                String fieldEloquaID = fieldDefinitions.get(value).getEloquaid();
                String fieldName = fieldDefinitions.get(value).getFieldName();
                // get field definition that matches key(eloquaId) from fieldMap
                if (fieldEloquaID.equals(key)) {
                    Boolean fieldIsRequired = fieldDefinitions.get(value).getIsRequired();
                    if (Constants.Properties.COUNTRY_FIELD.equals(fieldName)) {
                        selectedCountry = fieldValue;
                    }
                    if (StringUtils.isNotBlank(selectedCountry)) {
                        fieldIsRequired = FormValidationHelper.checkReqByCountry(selectedCountry, fieldName,
                                fieldIsRequired);
                    }
                    // redirect to register if profile is not complete for this profile type
                    if (StringUtils.isBlank(fieldValue) && (fieldIsRequired != null && fieldIsRequired)) {
                        regIsComplete = false;
                        break;
                    }
                }
            }
        }
        return regIsComplete;
    }
}
