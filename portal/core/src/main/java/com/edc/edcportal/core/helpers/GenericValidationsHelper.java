package com.edc.edcportal.core.helpers;

import org.apache.commons.lang3.StringUtils;

import com.edc.edcportal.core.helpers.formvalidation.FormCleaner;

public class GenericValidationsHelper {

    private GenericValidationsHelper() {
        // sonar lint
    }

    /** Data validation **/
    public static boolean checkNotEmpty(String valueToCheck) {
        return StringUtils.isNotBlank(valueToCheck);
    }

    public static boolean checkValidation(String valueToCheck, String validation) {
        /** TODO **/
        return true;
    }

    public static boolean checkValidationRule(String valueToCheck, String validationRule) {
        /** TODO **/
        return true;
    }

    public static boolean checkMinLength(String valueToCheck, String minLength) {
        int minChars = Integer.parseInt(minLength);
        return StringUtils.length(valueToCheck) > minChars;
    }

    public static boolean checkDataNoUrl(String valueToCheck) { // NOSONAR
        return true;
    }

    /** Data Format **/
    public static String enforceMaxLength(String valueToCheck, String maxLength) {
        int maxChars = Integer.parseInt(maxLength);
        return FormCleaner.maxLength(valueToCheck, maxChars);
    }

    public static String enforceCapitalize(String valueToCheck, String capitalize) {
        if (Constants.STRING_TRUE.equals(capitalize)) {
            valueToCheck = StringUtils.capitalize(valueToCheck);
        }
        return valueToCheck;
    }

    public static String enforceDataMask(String valueToCheck, String dataMask) {
        if (Constants.PHONE_DATA_MASK.equals(dataMask)) {
            valueToCheck = valueToCheck.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1)-$2-$3");
        }
        return valueToCheck;
    }

    public static String removeCharsByRegex(String valueToCheck, String regExp) {
        if (StringUtils.isNotBlank(valueToCheck)) {
            valueToCheck = valueToCheck.replaceAll(regExp, "");
        }
        return valueToCheck;
    }

}
