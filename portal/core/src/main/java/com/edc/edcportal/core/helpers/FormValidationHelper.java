package com.edc.edcportal.core.helpers;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.edc.edcportal.core.eloqua.EloquaConnectionManagerConstants;
import com.edc.edcportal.core.eloqua.pojo.EloquaUserProfileDO;
import com.edc.edcportal.core.eloqua.services.EloquaDAOService;
import com.edc.edcportal.core.helpers.formvalidation.FormCleaner;
import com.edc.edcportal.core.models.FormFieldDefintion;

public class FormValidationHelper {

    private FormValidationHelper() {
        // Sonar Lint
    }

    /**
     * If country is not USA or Canada Make companyProvince && companyPostal not
     * required
     * 
     * @param fieldName To check
     * @return false if If country is not USA or Canada
     */
    public static boolean checkReqByCountry(String selectedCountry, String fieldName, Boolean fieldIsRequired) {
        if ((StringUtils.isNotBlank(selectedCountry) && !Constants.CAN_COUNTRY_CODE.equals(selectedCountry)
                && !Constants.USA_COUNTRY_CODE.equals(selectedCountry))
                && (fieldName.equals(EloquaConnectionManagerConstants.COMPANY_PROVINCE_FIELD_NAME)
                        || fieldName.equals(EloquaConnectionManagerConstants.COMPANY_POSTAL_FIELD_NAME))) {
            fieldIsRequired = false;
        }
        return fieldIsRequired;
    }

    /**
     * checkPainPoints
     * 
     * @param eloquaUserProfileDO to get the data from
     * @param eloquaDAOService    to get the painPoints field id
     * @return true is PainPoints is not required for this profile
     *         true is PainPoints is required for this profile type and has data
     *         false if is PainPoints required for this profile type and has no data
     */
    public static boolean checkPainPoints(EloquaUserProfileDO eloquaUserProfileDO, EloquaDAOService eloquaDAOService) {
        boolean isOk = true;
        String profileTypeId = eloquaDAOService.getEloquaConfigService().getProfileFieldId();
        String profileType = eloquaUserProfileDO.getFormFieldsValues().get(profileTypeId).getEloquaValue();
        if (Constants.CANADIAN_PROFILE_TYPE.equals(profileType)) {
            String painPointsId = eloquaDAOService.getEloquaConfigService().getPainPointsFieldId();
            String painPoints = eloquaUserProfileDO.getFormFieldsValues().get(painPointsId).getEloquaValue();
            painPoints = FormCleaner.cleanAll(painPoints);
            isOk = GenericValidationsHelper.checkNotEmpty(painPoints);
        }
        return isOk;
    }

    /**
     * checkMarketsOfInterest Check Only if not F.I. Profile type
     * @param profileType the profile type
     * @param marketsOfInterest the marketsOfInterest
     * @return true is MarketsOfInteres is not required for this profile
     *         true is MarketsOfInteres is required for this profile type and has data
     *         false if MarketsOfInteres is required for this profile type and has no data
     */
    public static boolean checkMarketsOfInterest(String profileType, String marketsOfInterest) {
        boolean result = Constants.FI_PROFILE_TYPE.equals(profileType);
        // if profile is not FI, then check for MOI
        if (!result) {
            result = StringUtils.isNotBlank(marketsOfInterest);
        }
        return result;
    }

    /**
     * removeTheCheckAll there's a "clear-all" value present on multiple multi-
     * fields that can be selected if javaScript is disabled. Remove it
     * 
     * @param valueToSave
     * @return
     */
    public static String removeTheCheckAll(String valueToSave) {
        return GenericValidationsHelper.removeCharsByRegex(valueToSave, "([:]?[:]?clear-all[:]?[:]?)");
    }

    /**
     * Enforce Data Validation Rules
     * 
     * @param valueToSave        string to check
     * @param fieldIsRequired    to validate if string is not empty
     * @param formFieldDefintion to get specific field validations
     * @return
     */
    public static Map<String, Boolean> checkRules(String valueToSave, boolean fieldIsRequired,
            FormFieldDefintion formFieldDefintion) {
        Map<String, Boolean> results = new HashMap<>();
        // "Required"
        if (fieldIsRequired) {
            results.put("isRequired", GenericValidationsHelper.checkNotEmpty(valueToSave));
        }
        // "validation attribute"
        String validation = formFieldDefintion.getValidation();
        if (StringUtils.isNotBlank(validation)) {
            results.put("validation", GenericValidationsHelper.checkValidation(valueToSave, validation));
        }
        // "validationRule"
        String validationRule = formFieldDefintion.getValidationRule();
        if (StringUtils.isNotBlank(validationRule)) {
            results.put("validationRule", GenericValidationsHelper.checkValidationRule(valueToSave, validationRule));
        }
        // "dataNoUrl"
        String dataNoUrl = formFieldDefintion.getDataNoUrl();
        if (StringUtils.isNotBlank(dataNoUrl)) {
            results.put("dataNoUrl", GenericValidationsHelper.checkDataNoUrl(valueToSave));
        }
        // "minLength",
        String minLength = formFieldDefintion.getMinLength();
        if (StringUtils.isNotBlank(minLength)) {
            results.put("minLength", GenericValidationsHelper.checkMinLength(valueToSave, minLength));
        }
        return results;
    }

    /**
     * Enforce data format
     * 
     * @param valueToSave String to format
     * @return formatted string id applicable
     */
    public static String formatData(String valueToSave, FormFieldDefintion formFieldDefintion) {
        // "maxLength",
        String maxLength = formFieldDefintion.getMaxLength();
        if (StringUtils.isNotBlank(maxLength)) {
            valueToSave = GenericValidationsHelper.enforceMaxLength(valueToSave, maxLength);
        }
        // "capitalize",
        String capitalize = formFieldDefintion.getCapitalize();
        if (StringUtils.isNotBlank(capitalize)) {
            valueToSave = GenericValidationsHelper.enforceCapitalize(valueToSave, capitalize);
        }
        // "dataMask",
        String dataMask = formFieldDefintion.getDataMask();
        if (StringUtils.isNotBlank(dataMask)) {
            valueToSave = GenericValidationsHelper.enforceDataMask(valueToSave, dataMask);
        }
        return valueToSave;
    }

}
