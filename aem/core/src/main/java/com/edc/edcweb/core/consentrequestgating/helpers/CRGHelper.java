package com.edc.edcweb.core.consentrequestgating.helpers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.helpers.Request;
import com.edc.edcweb.core.helpers.ResourceResolverHelper;
import com.edc.edcweb.core.restful.RestClientConstants;
import com.edc.edcweb.core.service.EloquaService;
import com.edc.edcweb.core.serviceImpl.pojo.MyEDCTransaction;

public class CRGHelper {

    private static final Logger log = LoggerFactory.getLogger(CRGHelper.class);

    private CRGHelper() {
        // Utility classes should not have public constructors
    }

    public static String checkCRGAccess(Page currentPage, SlingHttpServletRequest request,
            EloquaService eloquaService) {
        String redirectTo = null;
        String path = currentPage.getPath();
        String teaserPage = StringUtils.remove(path, Constants.Paths.PROGRESSIVEPROFILING_PREMIUMROOT);
        // If not CRG mode, don't do anything
        if (checkCRGMode(request, teaserPage)) {
            boolean hasConsent = false;
            String externalId = (String) request.getSession().getAttribute(Constants.Properties.HEADER_EXTERNAL_ID);
            // CRG Variables
            Map<String, String> gatedUrlAndPartner = getGatedUrlAndPartner(request, teaserPage);
            String gatedUrl = gatedUrlAndPartner.get(CRGConstants.GATED_URL_PROPERTY);
            String partnersCode = gatedUrlAndPartner.get(CRGConstants.MULTIFIELD_NAME);
            // Debugging info
            String params = "externalId: ".concat(externalId).concat(" gatedUrl: ").concat(gatedUrl)
                    .concat(" partnersCode: ").concat(partnersCode);
            // Get the record
            if (StringUtils.isNotBlank(externalId) && StringUtils.isNotBlank(gatedUrl)
                    && StringUtils.isNotBlank(partnersCode)) {
                String transactionID = externalId.concat(Constants.PIPE).concat(path);
                // Bug 378038 URL Encode the PIPE, not affecting here but is best practice
                try {
                    transactionID = URLEncoder.encode(transactionID, RestClientConstants.UTF_8_CHAR_SET);
                } catch (UnsupportedEncodingException e) {
                    log.error("EloquaConnectionManager getTransactionHistoryRecordJson Could not encode transactionID {}", transactionID, e);
                }
                params = params.concat(" transactionID:" ).concat(transactionID);
                MyEDCTransaction transaction = eloquaService.getMyEDCTransaction(transactionID);
                String currentCasls = transaction.getPartnersCASL();
                hasConsent = StringUtils.contains(currentCasls, partnersCode);
                log.error("EloquaConnectionManager getTransactionHistoryRecordJson Could not found currentCasls {} params {}",currentCasls, params);
            } else {
                log.error("CRGHelper checkCRGAcce could not find ore or more params {}", params);
            }


            /// We do not have consent, redirect to teaser
            if (!hasConsent) {
                redirectTo = LinkResolver.changeInternalURLtoExternal(request,
                        LinkResolver.addHtmlExtension(teaserPage)).concat("?consent=no");
            }
        }
        return redirectTo;
    }

    private static boolean checkCRGMode(SlingHttpServletRequest request, String pagePath) {
        boolean result = false;
        Resource component = getComponentRes(request, pagePath, CRGConstants.PP_COMPONENT_RESOURCE_TYPE);
        if (component != null) {
            ValueMap properties = component.getValueMap();
            String mode = properties.get(CRGConstants.PP_MODE_PROPERTY, String.class);
            result = Constants.Properties.PROGRESSIVEPROFILING_MODE_CRG.equals(mode);
        }
        return result;
    }

    /**
     * Get the Values required to look for the record
     * 
     * @param request
     * @return List<String> GatedUrl position 0, Partner position 1
     */
    private static Map<String, String> getGatedUrlAndPartner(SlingHttpServletRequest request, String pagePath) {
        Map<String, String> values = new HashMap<>();
        Resource component = getComponentRes(request, pagePath, CRGConstants.GRS_COMPONENT_RESOURCE_TYPE);
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
    private static Resource getComponentRes(SlingHttpServletRequest request, String pagePath, String resourceType) {
        Resource compRes = null;
        // Get currentPage from referrer
        Resource pageRes = Request.getCurrentPageResource(request, pagePath);
        if (pageRes != null) {
            Page currentPage = pageRes.adaptTo(Page.class);
            // Find the form
            compRes = ResourceResolverHelper.getResourceByType(currentPage, resourceType);
        } else {
            /**
             * This check runs for every premium page, to find out if it is a CRG page, level
             * should be lowered as it is unnecessary and will increase the log size.
             */
            log.debug("CRGHelper: No Page Resource found on page {}", pagePath);
        }
        return compRes;
    }

}
