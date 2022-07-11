package com.edc.edcportal.core.helpers;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;

import com.day.cq.i18n.I18n;

public class TranslationHelper {

    private TranslationHelper() {
        // Sonar lint
    }

    /**
     *  Helper class to get translations from the dictionary on JAVA
     * @param key Dictionary key
     * @param request SlingHttpServletRequest
     * @param pagePath to get the language, will use from referrer if no path provided
     * @return translated string from dictionary, will return the key if no found on dictionary
     */
    public static String getI18nTranslation(String key, SlingHttpServletRequest request, String pagePath) {
        String msg = key;
        // Get the page path from referrer if no path provided
        if (StringUtils.isBlank(pagePath)) {
            pagePath = request.getHeader(Constants.Properties.REFERER);
        }
        // get the page language, default to English
        String pageLanguage = LanguageUtil.getLanguageAbbreviationFromPath(pagePath,
                Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
        // Do the page locale, resource bundle
        Locale pageLocale = new Locale(pageLanguage);
        ResourceBundle resourceBundle = request.getResourceBundle(pageLocale);
        // If resolved, look for the translation
        if (resourceBundle != null) {
            I18n i18n = new I18n(resourceBundle);
            msg = i18n.get(key);
        }
        return msg;
    }
}