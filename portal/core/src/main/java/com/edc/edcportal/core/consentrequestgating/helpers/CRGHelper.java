package com.edc.edcportal.core.consentrequestgating.helpers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.edc.edcportal.core.helpers.Request;
import com.edc.edcportal.core.helpers.ResourceResolverHelper;

public class CRGHelper {

    private static final Logger log = LoggerFactory.getLogger(CRGHelper.class);

    private CRGHelper() {
        // Utility classes should not have public constructors
    }

    /**
     * Get the Values required to look for the record
     * 
     * @param request
     * @return List<String> GatedUrl position 0, Partner position 1
     */
    public static Map<String, String> getGatedUrlAndPartner(SlingHttpServletRequest request, String overridePath) {
        Map<String, String> values = new HashMap<>();
        Resource component = getComponentRes(request, overridePath);
        String path = null;
        String partners = null;

        if (component != null) {
            ValueMap properties = component.getValueMap();
            path = properties.get(CRGConstants.GATED_URL_PROPERTY, String.class);
            Resource checkboxes = component.getChild(CRGConstants.MULTIFIELD_NAME);
            if (checkboxes != null) {
                List<String> partnersList = new LinkedList<>();
                Iterator<Resource> multifield = checkboxes.listChildren();
                while (multifield.hasNext()) {
                    Resource item = multifield.next();
                    partnersList.add(item.getValueMap().get(CRGConstants.MULTIFIELD_ITEM_VALUE_PROPERTY, String.class));
                }
                partners = String.join(",", partnersList);
            }
        }

        values.put(CRGConstants.GATED_URL_PROPERTY, path);
        values.put(CRGConstants.MULTIFIELD_NAME, partners);
        return values;
    }

    /**
     * getComponentRes Get The component's resource
     * 
     * @param request
     * @return The component's resource, null if not found
     */
    private static Resource getComponentRes(SlingHttpServletRequest request, String overridePath) {
        Resource formRes = null;
        // Get currentPage from referrer
        Resource pageRes = Request.getCurrentPageResource(request, overridePath);
        if (pageRes != null) {
            Page currentPage = pageRes.adaptTo(Page.class);
            // Find the form
            formRes = ResourceResolverHelper.getResourceByType(currentPage, CRGConstants.GRS_COMPONENT_RESOURCE_TYPE);
        } else {
            log.error("GatedContentAccessServlet: No Page Resource found!");
        }
        return formRes;
    }
}
