package com.edc.edcweb.core.helpers.webinars;

import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.edc.edcweb.core.helpers.Constants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.Template;
import com.edc.edcweb.core.helpers.constants.ConstantsWebinars;

public class WebinarsHelper {

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
     *  Add the Time to the date
     * @param webinarPage
     * @param useStartDate
     * @return date time
     */
    public static Calendar getWebinarDateTime(Page webinarPage, boolean useStartDate) {
        // Always default to none
        Calendar date = null;
        Calendar time;
        String pageTypePath = getWebinarPageTypePath(webinarPage);

        if (Constants.Paths.EVENT_TEMPLATE_URL.contains(pageTypePath) || Constants.Paths.BC_EVENT_TEMPLATE_URL.contains(pageTypePath)) {
            // Get pageProperties
            ValueMap pageProperties = webinarPage.getProperties();
            // Get event dates from pageProperties
            if (useStartDate) {
                date = pageProperties.get(ConstantsWebinars.WEBINAR_START_DATE, Calendar.class);
                time = pageProperties.get(ConstantsWebinars.WEBINAR_START_TIME, Calendar.class);
            } else {
                date = pageProperties.get(ConstantsWebinars.WEBINAR_END_DATE, Calendar.class);
                time = pageProperties.get(ConstantsWebinars.WEBINAR_END_TIME, Calendar.class);
            }
            // Check if dates were set
            if (date != null && time != null) {
                // Set start date with correct time
                joinDateAndTimeFor(date, time);
            }
        }
        return date;
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
