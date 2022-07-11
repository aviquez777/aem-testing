package com.edc.edcweb.core.myedcgating.helpers;

import org.apache.commons.lang3.StringUtils;

import com.edc.edcweb.core.helpers.Constants;

public class PagePathsHelper {

    private PagePathsHelper() {
        // Sonar Lint
    }

    /**
     * removeContentEDCLang "/content/edc/<lang>/" using regex
     * 
     * @param pagePath
     * @return the path without "/content/edc/<lang>/"
     */
    public static String removeContentEDCLang(String pagePath) {
        pagePath = pagePath.toLowerCase();
        // remove "/content/edc/<lang>/" using regex
        String regExp = Constants.Paths.CONTENT_EDC.concat("/(en|fr)/");
        return pagePath.replaceAll(regExp, "");
    }

    /**
     * guessTeaserPage find out the "gated path" and return the path without it. The
     * teaser page path should match this structure
     * 
     * @param pagePath
     * @return the teaser page path
     */
    public static String guessTeaserPage(String pagePath) {
        // remove "/content/edc/<lang>/"
        String noLang = removeContentEDCLang(pagePath);
        // find the first "/"
        int index = noLang.indexOf(Constants.Paths.FORWARD_SLASH);
        // get our gated folder
        String gatedFolder = noLang.substring(0, index + 1);
        // remove the gated folder from original path
        return StringUtils.remove(pagePath, gatedFolder);
    }
}
