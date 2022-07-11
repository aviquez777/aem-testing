package com.edc.edcweb.core.helpers;

import java.io.IOException;

import org.apache.sling.api.SlingHttpServletResponse;

import com.edc.edcweb.core.restful.RestClientConstants;


public class ServletResponseHelper {
    private ServletResponseHelper() {
        // Sonar LInt
    }

    /**
     * writeResponse Helper, returns the SlingHttpServletResponse
     * 
     * @param response original SlingHttpServletResponse
     * @param json     to include on response
     * @return SlingHttpServletResponse
     * @throws IOException
     */
    public static SlingHttpServletResponse writeResponse(SlingHttpServletResponse response, String json)
            throws IOException {
        response.setHeader(RestClientConstants.CACHE_CONTROL_HEADER, RestClientConstants.CACHE_CONTROL_HEADER_VALUE);
        response.setHeader(RestClientConstants.PRAGMA_HEADER, RestClientConstants.PRAGMA_HEADER_NO_CACHE_VALUE);
        response.setDateHeader(RestClientConstants.EXPIRES_HEADER, 0);
        response.setContentType(Constants.Properties.APPLICATION_SLASH_JSON);
        response.setCharacterEncoding(Constants.UTF_8);
        response.getWriter().append(json);
        response.getWriter().flush();
        return response;
    }
}
