package com.edc.edcweb.core.helpers;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import com.day.cq.wcm.api.Page;

import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Request {
    private static final Logger log = LoggerFactory.getLogger(Request.class);

    private Request() {

    }

    /**
     * Get the path to the page related to the given request.
     *
     * @param request Request whose page path is to be returned
     *                ({@see org.apache.sling.api.SlingHttpServletRequest} Request).
     * @return Path to the page for the given request.
     */
    public static String getPagePath(SlingHttpServletRequest request) {
        String path = "";
        Resource suffixResource = request.getRequestPathInfo().getSuffixResource();
        // ---------------------------------------------------------------------
        if (suffixResource != null) {
            path = suffixResource.getPath();
        } else {
            path = request.getRequestPathInfo().getResourcePath();
        }
        // ---------------------------------------------------------------------
        int index = path.indexOf(Constants.Paths.JCR_CONTENT);
        if (index == -1) {
            index = path.length();
        }
        // ---------------------------------------------------------------------
        path = path.substring(0, index);
        return path;
    }

    /**
     * Get the page related to the given request.
     *
     * @param request Request whose page is to be returned
     *                ({@see org.apache.sling.api.SlingHttpServletRequest} Request).
     * @return Page for the given request.
     */
    public static Page adaptToPage(SlingHttpServletRequest request) {
        String pagePath = Request.getPagePath(request);
        return request.getResourceResolver().resolve(pagePath).adaptTo(Page.class);
    }

    /**
     * Return current page resource from referer header param
     * 
     * @param request: it's the SlingHttpServletRequest
     * @return Resource currentPage
     */
    public static Resource getCurrentPageResource(SlingHttpServletRequest request, String overridePath) {
        // Get the page from the referrer or specific path
        Resource currentPage = null;

        try {
            URI urlPage = null;
            String referer = null;
            //check if there is an override Path
            if (StringUtils.isNotBlank(overridePath)) {
                // use it
                referer = overridePath;
            } else {
                // get the referrer from header
                referer = request.getHeader(Constants.Properties.REFERER);
            }

            if (referer != null) {
                urlPage = new URI(referer);
            }

            if (urlPage != null) {
                String currentPagePath = urlPage.getPath().replace(".html", "");
                currentPage = request.getResourceResolver()
                        .resolve(LinkResolver.changeManualExternalURLtoInternal(currentPagePath));

                if (currentPage.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
                    currentPage = null;
                }
            }
        } catch (URISyntaxException e) {
            log.error("error ", e);
            log.debug("error get current page from referer");
        }

        return currentPage;
    }
}
