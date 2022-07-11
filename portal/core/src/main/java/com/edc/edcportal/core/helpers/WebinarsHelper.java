package com.edc.edcportal.core.helpers;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Template;
import com.edc.edcportal.core.appauth.services.AppAuthDAOService;
import com.edc.edcportal.core.arkadin.services.ArkadinDAOService;
import com.edc.edcportal.core.helpers.constants.ConstantsWebinars;

public class WebinarsHelper {

    private static final Logger log = LoggerFactory.getLogger(WebinarsHelper.class);

    private WebinarsHelper() {
        // Sonar Lint
    }

    /**
     * Return the Webinar "Time Status" comparing the current date and webinar's
     * properties
     * 
     * @param webinarPage Webinar Page
     * @return String with the status
     */
    public static String getWebinarStatus(Page webinarPage, int windowTime) {
        // Always default to none
        String result = "";
        String pageTypePath = getWebinarPageTypePath(webinarPage);

        if (Constants.Paths.EVENT_TEMPLATE_URL.contains(pageTypePath)) {
            // Get pageProperties
            ValueMap pageProperties = webinarPage.getProperties();
            // Get event dates from pageProperties
            Calendar startDate = pageProperties.get(ConstantsWebinars.WEBINAR_START_DATE, Calendar.class);
            Calendar endDate = pageProperties.get(ConstantsWebinars.WEBINAR_END_DATE, Calendar.class);
            Calendar startTime = pageProperties.get(ConstantsWebinars.WEBINAR_START_TIME, Calendar.class);
            startTime.add(Calendar.MINUTE, windowTime);
            Calendar endTime = pageProperties.get(ConstantsWebinars.WEBINAR_END_TIME, Calendar.class);

            // Check if dates were set
            if (startDate != null && endDate != null && endTime != null) {
                Calendar today = Calendar.getInstance();

                // Set start date with correct time
                joinDateAndTimeFor(startDate, startTime);

                // Set end date with correct time
                joinDateAndTimeFor(endDate, endTime);

                // Check and set event status
                if (endDate.before(today)) {
                    result = ConstantsWebinars.WEBINAR_STATE_ONDEMAND;
                } else if (startDate.after(today)) {
                    result = ConstantsWebinars.WEBINAR_STATE_UPCOMING;
                } else {
                    result = ConstantsWebinars.WEBINAR_STATE_LIVE;
                }
            }
        }
        return result;
    }

    /**
     * Join date and time
     * 
     * @param date
     * @param time
     */
    private static void joinDateAndTimeFor(Calendar date, Calendar time) {
        date.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
        date.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
    }

    /**
     * getPartnerArr Helper function to get Partner's data from the page
     * 
     * @param myPage
     * @return
     */
    public static Map<String, String> getPartnerArr(Page myPage) {
        Map<String, String> prtnerArr = new HashMap<>();
        // Get the multi field node from the list
        Resource pageRes = myPage.getContentResource().getChild(ConstantsWebinars.WEBINAR_PARTNERS_MULTIFIELD);
        // Populate from values
        if (pageRes != null) {
            Integer counter = 1;
            Iterator<Resource> fieldsIterator = pageRes.listChildren();
            while (fieldsIterator.hasNext()) {
                String keyName = "p".concat(counter.toString());
                ValueMap vm = fieldsIterator.next().getValueMap();
                String partner = vm.get(ConstantsWebinars.WEBINAR_PARTNER_FIELD, String.class);
                prtnerArr.put(keyName, partner);
                counter++;
            }
        }
        return prtnerArr;
    }

    public static String getDefaultWitValue(Page myPage) {
        String lastValue = null;
        // Get the Multi field node from the list
        Resource pageRes = myPage.getContentResource().getChild(ConstantsWebinars.WEBINAR_WIT_RADIOS);
        // Populate from values
        if (pageRes != null) {
            Iterator<Resource> fieldsIterator = pageRes.listChildren();
            while (fieldsIterator.hasNext()) {
                ValueMap vm = fieldsIterator.next().getValueMap();
                // Last iteration will set the last value
                lastValue = vm.get(ConstantsWebinars.WEBINAR_WIT_VALUE_FIELD, String.class);
            }
        }
        return lastValue;
    }

    /**
     * registerNewUserToWebinar Register New User To a Webinar, creating both
     * accounts. Any error will get logged, and the webinar service will take care
     * of displaying the error on the UI
     * 
     * @param request
     * @param redirectUrl
     * @param externalId
     * @param appAuthDAOService
     * @param arkadinDAOService
     */
    public static void registerNewUserToWebinar(SlingHttpServletRequest request, String redirectUrl, String externalId,
            AppAuthDAOService appAuthDAOService, ArkadinDAOService arkadinDAOService) {
        // Get the page from the redirectUrl
        PageManager pageManager = request.getResourceResolver().adaptTo(PageManager.class);
        String baseEdcPath = Constants.Paths.CONTENT_EDC;
        String fullPath = redirectUrl.startsWith(baseEdcPath) ? redirectUrl : baseEdcPath.concat(redirectUrl);
        String path = fullPath.replace(".html", "");
        Page webinarPage = pageManager.getPage(path);
        if (webinarPage != null) {
            ValueMap pageProperties = webinarPage.getProperties();
            // Get showKey from pageProperties
            String showKey = pageProperties.get(ConstantsWebinars.SHOW_KEY, String.class);
            // Task 22143 squid:S2221 Errors are handled on the Service
            appAuthDAOService.createAppAuthAccount(externalId);
            arkadinDAOService.registerUserToWebinar(externalId, showKey);
        } else {
            log.error("error on Webinar Page Resource is null");
        }
    }

    /**
     * Helper function to get the webinar page path
     * 
     * @param webinarPage
     * @return page path
     */
    public static String getWebinarPageTypePath(Page webinarPage) {
        Template template = webinarPage.getTemplate();
        String pageTypePath = webinarPage.getTemplate().getPageTypePath();
        if (StringUtils.isBlank(pageTypePath)) {
            Resource templateRes = template.adaptTo(Resource.class);
            if (templateRes != null) {
                Resource structure = templateRes.getChild(ConstantsWebinars.STRUCTURE)
                        .getChild(ConstantsWebinars.JCR_CONTENT);
                pageTypePath = structure.getResourceType();
            }
        }
        return pageTypePath;
    }
}
