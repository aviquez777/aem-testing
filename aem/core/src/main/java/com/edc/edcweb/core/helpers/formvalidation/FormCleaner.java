package com.edc.edcweb.core.helpers.formvalidation;

import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.helpers.Constants;

public class FormCleaner {

    private static final Logger log = LoggerFactory.getLogger(FormCleaner.class);

    private FormCleaner() {
        // Sonar Lint
    }

    /**
     * Clean the string using cleanString and trimString
     * 
     * @param string
     * @return clean string
     */
    public static String cleanAll(String string) {
        if (string == null) {
            string = "";
        }
        string = cleanString(string);
        string = trimString(string);
        return string;
    }

    /**
     * Clean the string array using cleanString and trimString
     * 
     * @param arrString
     * @return clean string
     */
    public static String cleanAll(String[] arrString) {
        String string = "";
        if (arrString != null && arrString.length == 1) {
            string = arrString[0];
        }
        string = cleanString(string);
        string = trimString(string);
        return string;
    }

    /**
     * Apply all methods as requested
     * 
     * @param string
     * @param removeSpecialChars if true
     * @param length             if provided
     * @return clean string
     */
    public static String cleanAll(String string, Boolean removeSpecialChars, Integer length) {
        string = cleanString(string);
        string = trimString(string);
        if (removeSpecialChars == null) {
            removeSpecialChars = false;
        }
        if (removeSpecialChars) { // NOSONAR removeSpecialChars could be null and primitive does not accept it;
            string = removeSpecialChars(string);
        }
        if (length != null) {
            string = maxLength(string, length);
        }
        return string;
    }

    /**
     * Remove control characters only from string
     * 
     * @param string
     * @return clean string
     */
    public static String cleanString(String string) {
        return string.replaceAll("[\\p{Cntrl}\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}]", "").replaceAll("[\\r\\n\\t]", "");

    }

    /**
     * Remove whitespace from beginning and end of the string
     * 
     * @param string
     * @return string with no whitespace
     */
    public static String trimString(String string) {
        return StringUtils.trim(string);
    }

    /**
     * Remove non printable characters
     * 
     * @param string
     * @return string with non printable characters
     */
    public static String removeSpecialChars(String string) {
        return Normalizer.normalize(string, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    /**
     * Trim the string to the indicated length
     * 
     * @param string
     * @param length
     * @return trimmed string
     */
    public static String maxLength(String string, Integer length) {
        return StringUtils.left(string, length);
    }

    /**
     * Get boolean value from a string array
     * 
     * @param string
     * @return boolean value
     */
    public static boolean getBoolean(String string) {
        string = cleanAll(string);
        return StringUtils.isNotBlank(string);
    }

    /**
     * Get boolean value from a string array
     * 
     * @param arrString
     * @return boolean value
     */
    public static boolean getBoolean(String[] arrString) {
        String string = "";
        if (arrString != null && arrString.length == 1) {
            string = arrString[0];
        }
        string = cleanAll(string);
        return StringUtils.isNotBlank(string);
    }

    /**
     * Get boolean string True" or "False" from a string array
     * 
     * @param string
     * @return Capitalized String "True" or "False"
     */
    public static String getBooleanString(String string) {
        return StringUtils.capitalize(Boolean.toString(getBoolean(string)));
    }

    /**
     * Get boolean string "true" or "false" from a string array
     * 
     * @param arrString
     * @return Capitalized Sting "True" or "False"
     */
    public static String getBooleanString(String[] arrString) {
        String string = "";
        if (arrString != null && arrString.length == 1) {
            string = arrString[0];
        }
        string = cleanString(string);
        string = trimString(string);
        return getBooleanString(string);
    }

    /**
     * Gets a, Integer value from s string
     * 
     * @param string
     * @return 0 if value cannot be converted
     */
    public static Integer getInteger(String string) {
        string = cleanAll(string);
        Integer parsedInt = 0;
        try {
            parsedInt = Integer.parseInt(string);
        } catch (Exception e) {
            log.debug("FormCleaner getInteger BAD Number {}: ", string, e);
        }
        return parsedInt;
    }

    /**
     * Gets a long value from s string
     * 
     * @param string
     * @return 0 if value cannot be converted
     */
    public static Long getLong(String string) {
        string = cleanAll(string);
        Long parsedLong = 0L;
        try {
            parsedLong = Long.parseLong(string);
        } catch (Exception e) {
            log.debug("FormCleaner getLong BAD Number {}: ", string, e);
        }
        return parsedLong;
    }

    /**
     * Clean the array elements using the clean methods
     * 
     * @param inputArray
     * @return array with clean values
     */
    public static String[] cleanArray(String[] inputArray) {
        List<String> theList = new LinkedList<>();
        if (inputArray != null) {
            for (String item : inputArray) {
                String cleanedStr = cleanAll(item);
                if (StringUtils.isNotBlank(cleanedStr)) {
                    theList.add(cleanedStr);
                }
            }
        }
        return theList.stream().toArray(n -> new String[n]);
    }

    /**
     * split the string into an array using the provided delimiter
     * 
     * @param string
     * @param delimiter
     * @return array with the values
     */
    public static String[] getArrayFromString(String string, String delimiter) {
        String[] stringArr = null;
        if (StringUtils.isNotBlank(string)) {
            stringArr = string.split(delimiter);
        }
        return stringArr;
    }

    /**
     * Escapes the characters in a String using XML entities.
     * 
     * @param string
     * @return escaped String
     */
    public static String escapeXml(String string) {
        string = cleanAll(string);
        return StringEscapeUtils.escapeXml(string);
    }

    /**
     * Escapes the characters in a String using HTML entities.
     * 
     * @param string
     * @return escaped String
     */
    public static String escapeHtml(String string) {
        string = cleanAll(string);
        return StringEscapeUtils.escapeHtml(string);
    }

    /**
     * Amount must have 2 decimals separated by period
     * @param textAmount
     * @return formatted String
     */
    public static String checkCCAmount(String textAmount) {
        textAmount = cleanAll(textAmount);
        String withDecimals = null;
        if (StringUtils.isNotBlank(textAmount)) {
            //Replace Comma and remove Minus Sign if any
            String[] searchList = new String[] {Constants.COMMA, Constants.MINUS_SIGN};
            String[] replacementList = new String[] {Constants.PERIOD, ""};
            textAmount = StringUtils.replaceEach(textAmount, searchList, replacementList);
            DecimalFormat moneyFormat = new DecimalFormat("#0.00");
            try {
                double amount = Double.parseDouble(textAmount);
                withDecimals = moneyFormat.format(amount);
            } catch (NumberFormatException e) {
                log.error("FormCleaner checkCents error formatting: {}", textAmount, e);
            }
        }
        return withDecimals;
    }
    
}
