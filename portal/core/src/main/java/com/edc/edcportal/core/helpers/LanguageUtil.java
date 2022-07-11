package com.edc.edcportal.core.helpers;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.SlingHttpServletRequest;

import com.edc.edcportal.core.helpers.Constants.SupportedLanguages;

/**
 * @author Roberto Ramos
 * @version 1.0.0
 * @since 1.0.0
 *        <p>
 *        This class is used by to get the Language of the EDC Portal web site.
 */
public class LanguageUtil {
    private LanguageUtil() {
    }

    /**
     * Given <code>path</code>, return its language abbreviation.
     *
     * @param path            Path whose language abbreviation is to be returned.
     * @param defaultLanguage If the path does not contain a language abbreviation,
     *                        return this as the default language.
     * @return String Abbreviation of the <code>path</code>'s language (e.g., "EN"
     *         for English, "FR" for French).
     */
    public static String getLanguageAbbreviationFromPath(String path, String defaultLanguage) {
        String language = searchLanguageAbbreviation(path);
        // ---------------------------------------------------------------------
        // if no language was found in the path, return the default
        // ---------------------------------------------------------------------
        if (language.isEmpty()) {
            language = defaultLanguage;
        }

        return language;
    }

    private static String searchLanguageAbbreviation(String path) {
        int lastSlashIndex = 0;
        String language = "";
        // ---------------------------------------------------------------------
        // While there is more string to search...
        // ---------------------------------------------------------------------
        while ((path != null) && (lastSlashIndex < path.length())) {
            // -----------------------------------------------------------------
            // Find next "/"
            // -----------------------------------------------------------------
            int indexSlash = path.indexOf('/', lastSlashIndex);

            if (indexSlash != -1) {
                // -------------------------------------------------------------
                // Found a "/", check if third character away is either a "/"
                // or a "." --OR-- there are only two characters remaining in
                // the path. If so, we (may) have found our language. If not,
                // we need to update our lastSlashIndex and search for the next
                // "/".
                // -------------------------------------------------------------
                if ((((indexSlash + 3) < path.length())
                        && ((path.charAt(indexSlash + 3) == '.') || (path.charAt(indexSlash + 3) == '/')))
                        || ((indexSlash + 3) == path.length())) {
                    // ---------------------------------------------------------
                    // Grab chars between indexSlash + 1 (don't want to include
                    // starting "/") and indexSlash + 3 (the ending index here
                    // is not included in sub-string).
                    // ---------------------------------------------------------
                    language = path.substring(indexSlash + 1, indexSlash + 3);
                    // ---------------------------------------------------------
                    // Language code must be only letters.
                    // ---------------------------------------------------------
                    if (Character.isLetter(language.charAt(0)) && Character.isLetter(language.charAt(1))) {
                        break;
                    }
                }
                // -------------------------------------------------------------
                // Not language code yet but more string to search. Update our
                // lastSlashIndex for the start of our next search.
                // -------------------------------------------------------------
                lastSlashIndex = indexSlash + 1;
                language = "";
            } else {
                // -------------------------------------------------------------
                // No more "/". Set lastSlashIndex to stop iterations.
                // -------------------------------------------------------------
                lastSlashIndex = path.length();
            }
        }

        return language;
    }
    
    
    /**
     *  Get the language abbr to use in the HTL
     * @return lower case language
     */
    public static String getLanguage(SlingHttpServletRequest request) {
        String langAbbr = "";
        String url = request.getRequestURL().toString();
        langAbbr = LanguageUtil.getLanguageAbbreviationFromPath(url,
                Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
        return langAbbr.toLowerCase();
    }

    /**
     *  Get the full language name to use
     * @return Language of supported languages
     */
    public static String getLanguageText(SlingHttpServletRequest request) {
        String langAbbr = getLanguage(request);
        SupportedLanguages lang = Constants.SupportedLanguages.getLanguageFromAbbreviation(langAbbr);
        return  lang.getLanguageText();
    }

    /**
     *  Get the English language name to use
     * @return  language name in English, abbreviation if not found;
     */
    public static String getLanguageEnglishText(SlingHttpServletRequest request) {
        Map<String, String> langMap = new HashMap<>();
        langMap.put("en", "English");
        langMap.put("fr", "French");
        String langAbbr = getLanguage(request);
        String enLang = langAbbr;
        if(langMap.containsKey(langAbbr)) {
            enLang = langMap.get(langAbbr);
        }
        return  enLang;
    }
}