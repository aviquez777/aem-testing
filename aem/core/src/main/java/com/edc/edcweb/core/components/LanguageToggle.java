package com.edc.edcweb.core.components;

import com.adobe.cq.sightly.WCMUsePojo;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.Constants.Paths;
import com.edc.edcweb.core.helpers.Language;
import com.edc.edcweb.core.helpers.LanguageToggleUtil;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.helpers.constants.ConstantsMyEdc;

/**
 * @author Peter Crummey
 * @version 1.0.0
 * @since 1.0.0
 *
 *
 *        Backing Java code for the Language Toggle component used by the EDC
 *        web site.
 *
 *
 */
public class LanguageToggle extends WCMUsePojo {

    private String path = "";
    private String language = "";
    private String abbreviation = "";
    private String toggledPath = "";
    private String toggledLanguage = "";
    private String toggleAbbreviation = "";

    /**
     * Populates the language toggle path and textual description when this object
     * is activated by AEM.
     *
     */
    @Override
    public void activate() throws Exception {
        String currPath = getCurrentPage().getPath();
        // ---------------------------------------------------------------------
        // If currPath has "/errors/", current page is an error page. Don't
        // want to show the translated version of the error page. Want to point
        // the user to the translated home page. Simply take the string up to
        // "/errors/" and append ".html".
        // ---------------------------------------------------------------------
        if (currPath.indexOf(Paths.ERRORS) != -1) {
            currPath = currPath.substring(0, currPath.indexOf(Paths.ERRORS)) + Constants.HTML_EXTENTION;
        }
        // ---------------------------------------------------------------------
        LanguageToggleUtil langUtil = LanguageToggleUtil.getInstance();
        Language edcLang = langUtil.findLanguage(currPath);
        // ---------------------------------------------------------------------
        toggledLanguage = edcLang.getToggledLanguage().getLanguageText();
        toggleAbbreviation = edcLang.getToggledLanguage().getLanguageAbbreviation();
        toggledPath = LinkResolver.addHtmlExtension(edcLang.getToggledPath(currPath));

        language = edcLang.getCurrentLanguage().getLanguageText();
        abbreviation = edcLang.getCurrentLanguage().getLanguageAbbreviation();
        path = LinkResolver.addHtmlExtension(currPath);

        // ---------------------------------------------------------------------
        // If there is a query string on the request, append to toggled path
        // ---------------------------------------------------------------------
        String queryString = getRequest().getQueryString();
        if (queryString != null) {
            toggledPath = toggledPath + "?" + queryString;
        }

        String selectors = getRequest().getRequestPathInfo().getSelectorString();
        if (selectors != null) {
            toggledPath = toggledPath.replace(Constants.HTML_EXTENTION, "." + selectors + Constants.HTML_EXTENTION);
        }

        if (toggledPath.contains(ConstantsMyEdc.Paths.MYEDC_CCONTENT_EN)
                || toggledPath.contains(ConstantsMyEdc.Paths.MYEDC_CCONTENT_FR)) {
            toggledPath = toggledPath.replace(ConstantsMyEdc.Paths.MYEDC_CCONTENT, "");
            if (toggledPath.lastIndexOf("fr") > 0) {
                toggledPath = myedcEntoFr(toggledPath);
            }

            if (toggledPath.indexOf(Constants.HTML_EXTENTION) == -1) {
                toggledPath = toggledPath + ".html";
            }
        }
    }
    /**
     * Get the URL.
     *
     * @return String The language URL
     */
    public String getUrl() {
        return path;
    }

    /**
     * Get the language's textual description.
     *
     * @return String The language's textual description
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Get the Abbreviation.
     *
     * @return String The language's abbreviation
     */
    public String getAbbreviation() {
        return abbreviation.toLowerCase();
    }

    /**
     * Get the toggled URL (e.g., if the current page's path is
     * "/en/about-exporting/...", the toggled path will be "/fr/exporter/...").
     *
     * @return String The language-toggled URL
     */
    public String getToggleUrl() {
        return toggledPath;
    }

    /**
     * Get the toggled language's textual description (e.g., if the current page's
     * is English, "Français" is returned).
     *
     * @return String The toggled language's textual description
     */
    public String getToggleLanguage() {
        return toggledLanguage;
    }

    /**
     * Get the toggled Abbreviation (e.g., if the current page's abbreviation is en,
     * fr is returned").
     *
     * @return String The toggled language's abbreviation
     */
    public String getToggleAbbreviation() {
        return toggleAbbreviation.toLowerCase();
    }

    private String myedcEntoFr(String path) {
        String[] paths = path.split("\\/", -1);
        String newPath = "";

        for (String p : paths) {
            if (!p.isEmpty()) {
                if (ConstantsMyEdc.PATHSMAP.get(p) != null) {
                    newPath += "/" + ConstantsMyEdc.PATHSMAP.get(p);
                } else {
                    newPath += "/" + p;
                }
            }
        }
        return newPath;
    }
}