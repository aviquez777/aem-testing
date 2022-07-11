package com.edc.edcportal.core.helpers;

import java.util.Iterator;

import javax.jcr.query.Query;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

public class PagePathsHelper {

    PagePathsHelper() {
        // Sonar lint
    }

    public static String getBasePath(String pageLanguage) {
        return Constants.Paths.CONTENT_MYEDC.concat(Constants.FORWARD_SLASH).concat(pageLanguage)
                .concat(Constants.Paths.MYACCOUNT);
    }

    public static String getHomePagePath(String pageLanguage) {
        String basePath = getBasePath(pageLanguage);
        return basePath.concat(Constants.Paths.MYEDC_HOME_PAGE);
    }

    public static String getRegisterPagePath(String pageLanguage) {
        String basePath = getBasePath(pageLanguage);
        return basePath.concat(Constants.Paths.REGISTER);
    }

    public static String getEditProfilePagePath(String pageLanguage) {
        String basePath = getBasePath(pageLanguage);
        return basePath.concat(Constants.Paths.EDIT_PROFILE);
    }

    public static String getSelectPagePath(String pageLanguage) {
        String basePath = getBasePath(pageLanguage);
        return basePath.concat(Constants.Paths.SELECT_PROFILE);
    }

    public static String getProfilePagePath(String pageLanguage) {
        String basePath = getBasePath(pageLanguage);
        return basePath.concat(Constants.Paths.PROFILE);
    }

    public static String getRenewalPagePath(String pageLanguage) {
        String basePath = getBasePath(pageLanguage);
        return basePath.concat(Constants.Paths.RENEWAL);
    }

    public static String getResourceNameFromAlias(SlingHttpServletRequest request, String pageName) {
        Resource resource = null;
        String query = "SELECT * FROM [cq:PageContent] AS s WHERE ISDESCENDANTNODE(s,'%s') AND [sling:alias] = '%s'";
        String searchPath = Constants.Paths.CONTENT_MYEDC;
        ResourceResolver resourceResolver = request.getResourceResolver();
        Iterator<Resource> pageResources = resourceResolver.findResources(String.format(query, searchPath, pageName),
                Query.JCR_SQL2);
        if(pageResources.hasNext()) {
            resource = pageResources.next().getParent(); 
        }
        if (resource != null) {
            pageName = resource.getName();
        }
        return pageName;
    }

    public static String removeContentPath(String path) {
        String longPath = Constants.Paths.CONTENT_MYEDC;
        String edcLongPath = Constants.Paths.CONTENT_EDC;
        if (path.contains(longPath)) {
            path = path.replace(longPath, "");
        }
        if (path.contains(edcLongPath)) {
            path = path.replace(edcLongPath, "");
        }
        return path;
    }
    
    
    public static boolean isAllowedPage (String[] dissalowedPages, String currentPath) {
        return ArrayUtils.contains(dissalowedPages, currentPath);
    }
}
