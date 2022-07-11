package com.edc.edcportal.core.helpers;

import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import com.day.cq.wcm.api.Page;

public class ResourceResolverHelper {
    
    private ResourceResolverHelper () {
        // Utility classes should not have public constructors
    }

    private static Resource theResource = null;
    /**
     * Recursively searches all the content nodes on the pages until the requested
     * resource type is found and returns it.
     * <p>
     * Returns null if not found=
     * 
     * @param thisPage
     *            Page to search the resource.
     * @param resourceType
     *            String Resource type
     * @return theResource Resource requested resource type is found null otherwise.
     */

    public static Resource getResourceByType(Page thisPage, String resourceType) {
        Iterator<Resource> level1 = thisPage.getContentResource().listChildren();
        if (level1.hasNext()) {
            theResource = null;
            recursiveSearch(level1, resourceType, null);
        }
        return theResource;
    }
    
    /**
     * Recursively searches all the content nodes on the pages until the requested
     * resource type is found and returns it.
     * This add a node name parameter to point to the resource on that specific node
     * <p>
     * Returns null if not found=
     * 
     * @param thisPage
     *            Page to search the resource.
     * @param resourceType
     *            String Resource type
     * @return theResource Resource requested resource type is found null otherwise.
     */

    public static Resource getResourceByTypeAndNode(Page thisPage, String resourceType, String nodeName) {
        Iterator<Resource> level1 = thisPage.getContentResource().listChildren();
        if (level1.hasNext()) {
            theResource = null;
            recursiveSearch(level1, resourceType, nodeName);
        }
        return theResource;
    }

    /**
     * Iterates over the given iterator and sets the resource variable if found. Breaks
     * the loop when the first Resource is found. If not found, calls itself with any
     * children resources.
     * 
     * @param toSearch
     *            Iterator<Resource> to check for the given Resource type
     * @param resourceType
     *            String Resource type
     */
    private static void recursiveSearch(Iterator<Resource> toSearch, String resourceType, String nodeName) {
        while (toSearch.hasNext()) {
            Resource thisResource = toSearch.next();
            boolean foundIt = thisResource.isResourceType(resourceType);
            if(StringUtils.isNotBlank(nodeName)) {
                foundIt = thisResource.getPath().endsWith(nodeName);
            }
            if (foundIt) {
                theResource = thisResource;
                break;
            } else {
                if (thisResource.hasChildren()) {
                recursiveSearch(thisResource.listChildren(), resourceType, nodeName);
                }
            }
        }
    }

}