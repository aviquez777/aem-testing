package com.edc.edcportal.core.helpers;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Helper class to deal with the AEM Sessions
 *
 */
public class SessionHelper {
    private static final Logger log = LoggerFactory.getLogger(SessionHelper.class);

    private SessionHelper() {
        // Sonar Lint
    }

    /**
     * startSession Start or refresh the session. Use Cookie as fallback if header
     * not available
     *
     * @param request SlingHttpServletRequest
     */
    public static void startSession(SlingHttpServletRequest request, String profileType, String externalId) {
        String shibSessionId = request.getHeader(Constants.Properties.HEADER_SHIB_SESSION_ID);
        // Use Cookie as fallback if header not available
        if (StringUtils.isBlank(shibSessionId)) {
            String shibCookie =  CookieHelper.getCookieValue(request, Constants.Properties.SHIBBOLETH_COOKIE_NAME);
            // Check if cookie exists
            if (StringUtils.isNotBlank(shibCookie)) {
                shibSessionId = shibCookie;
            }
        }
        request.getSession().setAttribute(Constants.Properties.SHIBBOLETH_SESSION_VAR, shibSessionId);
        request.getSession().setAttribute(Constants.Properties.PROFILE_TYPE_FIELD, profileType);
        // US 144669 need external id on edcweb PremiumPageController
        request.getSession().setAttribute(Constants.Properties.HEADER_EXTERNAL_ID, externalId);
        // remove any error flag
        request.getSession().removeAttribute(Constants.Properties.PROFILE_HAS_ERRORS);
        // log the session start
        log.debug("SessionHelper: Session Started for user {}", externalId);
    }
}
