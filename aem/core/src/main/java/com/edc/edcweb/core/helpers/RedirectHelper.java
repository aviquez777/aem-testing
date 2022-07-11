package com.edc.edcweb.core.helpers;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.sling.api.SlingHttpServletResponse;

import com.edc.edcweb.core.restful.RestClientConstants;

/**
 * Utility to do the redirects as suggested by best practices.
 *
 */
public class RedirectHelper {
    RedirectHelper() {
        // Sonar Lint
    }

    /**
     * Setting content type "text/html" as the sendRedirect() method works at client
     * side. .
     * 
     * @param response   the SlingHttpServletResponse
     * @param redirectTo url to redirect
     * @throws IOException
     */
    public static void redirectTo(SlingHttpServletResponse response, String redirectTo) throws IOException {
        response.setContentType(Constants.Properties.TEXT_SLASH_HTML);
        response.setHeader(RestClientConstants.CACHE_CONTROL_HEADER, RestClientConstants.CACHE_CONTROL_HEADER_VALUE);
        response.setHeader(RestClientConstants.PRAGMA_HEADER, RestClientConstants.PRAGMA_HEADER_NO_CACHE_VALUE);
        response.setDateHeader(RestClientConstants.EXPIRES_HEADER, 0);
        PrintWriter printWriter = response.getWriter();
        response.sendRedirect(redirectTo);
        printWriter.close();
    }

}