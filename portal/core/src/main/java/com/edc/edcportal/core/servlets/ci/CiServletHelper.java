package com.edc.edcportal.core.servlets.ci;

import java.io.IOException;

import org.apache.sling.api.SlingHttpServletResponse;

import com.edc.edcportal.core.helpers.ServletResponseHelper;

public class CiServletHelper {
    
    private CiServletHelper () {
        // Sonar Lint
    }

    public static SlingHttpServletResponse writeResponse(SlingHttpServletResponse response, String json) throws IOException {
        return ServletResponseHelper.writeResponse(response, json);
    }
}
