package com.edc.edcweb.core.gatedcontentaccess.helpers;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.Request;
import com.edc.edcweb.core.helpers.ResourceResolverHelper;

public class GatedContentAccessHelper {

    private static final Logger log = LoggerFactory.getLogger(GatedContentAccessHelper.class);

    private GatedContentAccessHelper() {
        // Utility classes should not have public constructors
    }

    /**
     * checkSessionAccessGranted check if the flag to access this assetGroupName has
     * been set
     * 
     * @param request         to get the session from
     * @param assetGroupName: common variable name in case forms will share assets
     * @return true if does, false if not
     */
    public static boolean checkSessionAccessGranted(SlingHttpServletRequest request, String assetGroupName) {
        String sessionVal = (String) request.getSession().getAttribute(assetGroupName);
        return StringUtils.isNotBlank(sessionVal);
    }

    /**
     * setSessionAccessGranted set the flag to access this assetGroupName
     * 
     * @param request         to get the session from
     * @param assetGroupName: common variable name in case forms will share assets
     */
    public static void setSessionAccessGranted(SlingHttpServletRequest request, String assetGroupName) {
        request.getSession().setAttribute(assetGroupName, GatedContentAccessConstats.STRING_YES);
    }

    /**
     * Get the Form Resource from the current page if not override path is given
     * 
     * @param request      to get the resource resolved and current Page
     * @param overridePath Get this resource instead of the current Page
     * @return
     */
    public static Resource getFormRes(String resourceType, SlingHttpServletRequest request, String overridePath) {
        Resource formRes = null;
        // Get currentPage from referrer
        Resource pageRes = Request.getCurrentPageResource(request, overridePath);
        if (pageRes != null) {
            Page currentPage = pageRes.adaptTo(Page.class);
            // Find the questionnaire
            formRes = ResourceResolverHelper.getResourceByType(currentPage, resourceType);
        } else {
            log.error("GatedContentAccessServlet: No Page Resource found!");
        }
        return formRes;
    }
}
