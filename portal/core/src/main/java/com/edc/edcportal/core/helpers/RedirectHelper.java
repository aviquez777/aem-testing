package com.edc.edcportal.core.helpers;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Utility to do the redirects as suggested by best practices.
 *
 */
public class RedirectHelper {
    RedirectHelper() {
        // Sonar Lint
    }

    private static final Logger log = LoggerFactory.getLogger(RedirectHelper.class);
            
            
    /**
     * Setting content type "text/html" as the sendRedirect() method works at client
     * side. .
     * 
     * @param response   the SlingHttpServletResponse
     * @param redirectTo url to redirect
     * @throws IOException
     */
    public static void redirectTo(SlingHttpServletResponse response, String redirectTo) throws IOException {
        // Check if redirectTo is valid
        redirectTo = checkValidReturn(response, redirectTo);
        if(redirectTo.endsWith(Constants.HTML_EXTENSION) && redirectTo.contains(Constants.Properties.SUB_QUERY_PARAM_ACTION_TYPE_AMPERSAND)) {
            // Has a query string and ends on html, remove last .html
            redirectTo = StringUtils.substring(redirectTo, 0, redirectTo.length() - 5);
        }
        // remove any content/myedc from path
        redirectTo = StringUtils.remove(redirectTo, Constants.Paths.CONTENT_MYEDC);
        response.setContentType("text/html");
        PrintWriter printWriter = response.getWriter();
        response.sendRedirect(redirectTo);
        printWriter.close();
    }

    /**
     * checkValidReturn Check if redirectTo is valid
     * @param response
     * @param redirectUrl
     * @return redirectUrl if ok, home page otherwise 
     */
    private static String checkValidReturn(SlingHttpServletResponse response, String redirectUrl) {
        Pattern pattern = Pattern.compile(Constants.ALLOWED_REDIRECT_REGEX);
        Matcher matcher = pattern.matcher(redirectUrl);

        if (!matcher.find()) {
            log.error("RedirectHelper Invalid Redirect URL: {} ", redirectUrl);
            // Will fallback to en if unable to resolve
            String pageLanguage = response.getLocale().getLanguage();
            String homePagePath = PagePathsHelper.getHomePagePath(pageLanguage);
            redirectUrl = homePagePath.concat(Constants.HTML_EXTENSION);
        }

        return redirectUrl;
    }

}