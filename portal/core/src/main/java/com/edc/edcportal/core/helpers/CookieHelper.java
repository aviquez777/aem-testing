package com.edc.edcportal.core.helpers;

import javax.servlet.http.Cookie;

import org.apache.sling.api.SlingHttpServletRequest;

import com.edc.edcportal.core.helpers.formvalidation.FormCleaner;

public class CookieHelper {
    private CookieHelper() {
        // Sonar Lint
    }

    public static String getCookieValue(SlingHttpServletRequest request, String cookieName) {
        String cookieVal = null;
        Cookie trafficSourceCookie = request.getCookie(cookieName);
        // Check if cookie exists
        if (trafficSourceCookie != null) {
            cookieVal = FormCleaner.cleanAll(trafficSourceCookie.getValue(), false, null);
        }
        return cookieVal;
    }
}
