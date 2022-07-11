package com.edc.edcportal.core.helpers.formvalidation;

import java.text.Normalizer;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormCleaner {

    private static final Logger log = LoggerFactory.getLogger(FormCleaner.class);

    private FormCleaner() {
        // Sonar Lint
    }

    /**
     * cleanAll Apply all the rules on one shot
     * 
     * @param string
     * @return clean String
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
     * cleanAll(different signature) Apply all the rules on one shot, trimming the
     * string
     * 
     * @param string
     * @param removeSpecialChars
     * @param length
     * @return clean, trimmed String
     */
    public static String cleanAll(String string, boolean removeSpecialChars, Integer length) {
        string = cleanString(string);
        string = trimString(string);
        if (removeSpecialChars) {
            string = removeSpecialChars(string);
        }
        if (length != null) {
            string = maxLength(string, length);
        }
        return string;
    }

    /**
     * cleanString remove unwanted "unsafe" characters
     * @param string
     * @return safe string
     */
    public static String cleanString(String string) {
        // just remove control characters only
        return string.replaceAll("[\\p{Cntrl}\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}]", "").replaceAll("[\\r\\n\\t]", "");
    }

    public static String trimString(String string) {
        return StringUtils.trim(string);
    }

    public static String removeSpecialChars(String string) {
        return Normalizer.normalize(string, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    public static String maxLength(String string, Integer length) {
        return StringUtils.left(string, length);
    }

    public static Boolean getBoolean(String string) {
        string = cleanAll(string);
        return StringUtils.isNotBlank(string);
    }

    public static String getBooleanString(String string) {
        return StringUtils.capitalize(getBoolean(string).toString());
    }

    public static Integer getInteger(String string) {
        string = cleanAll(string);
        Integer parsedInt = 0;
        try {
            parsedInt = Integer.parseInt(string);
        } catch (NumberFormatException e) {
            log.debug("FormCleaner getInteger BAD Number {}: ", string, e);
        }
        return parsedInt;
    }

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
}
