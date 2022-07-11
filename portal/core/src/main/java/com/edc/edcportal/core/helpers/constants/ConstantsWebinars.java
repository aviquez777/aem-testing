package com.edc.edcportal.core.helpers.constants;

public class ConstantsWebinars {

    private ConstantsWebinars() {
        // Sonar lint
    }

    // config constants
    public static final String WEBINAR_SERVICE_SERVLET_URL = "/bin/myedc/webinarservice";
    public static final String EVENT_TEMPLATE_URL = "edc/components/structure/event/page";
    // Event page variables
    public static final String SHOW_KEY = "webinarshowkey";
    public static final String WEBINAR_START_DATE = "startDate";
    public static final String WEBINAR_END_DATE = "endDate";
    public static final String WEBINAR_START_TIME = "startTime";
    public static final String WEBINAR_END_TIME = "endTime";
    public static final String DECOM_VIDEO_URL = "decomvideourl";
    // Json responses
    public static final String WEBINAR_STATE_ONDEMAND = "ondemand";
    public static final String WEBINAR_STATE_UPCOMING = "upcoming";
    public static final String WEBINAR_STATE_LIVE = "live";
    // Error Messages
    public static final String WEBINAR_PAGE_LOAD_ERROR_KEY = "webinar-page-load-error";
    public static final String WEBINAR_API_ERROR_KEY = "webinar-api-error";
    public static final String WEBINAR_PARTNER_FIELD = "partner";
    public static final String WEBINAR_PARTNERS_MULTIFIELD = WEBINAR_PARTNER_FIELD.concat("s");
    public static final String WIT_RADIO_FIELD = "wit";
    public static final String WEBINAR_WIT_RADIOS = "witradios";
    public static final String WEBINAR_WIT_VALUE_FIELD = "witvalue";

    public static final String WEBINAR_API_OK = "OK";
    public static final String WEBINAR_API_ZERO = "0";
    public static final String WEBINAR_API_ALREADY_REGISTERED_CODE = "44";

    public static final String MY_DASHBOARD_SERVICE_BASE_URL = "/bin/myedc/mydashboard";
    public static final String MY_WEBINARS_TAB_SERVICE_SERVLET_URL = MY_DASHBOARD_SERVICE_BASE_URL + "-webinars";
    public static final String MY_RESOUCRES_TAB_SERVICE_SERVLET_URL = MY_DASHBOARD_SERVICE_BASE_URL + "-resources";

    public static final String QUERY_WEBINAR_PATH = "/content/edc/%s/events/webinar/";

    public static final String STRUCTURE = "structure";
    public static final String JCR_CONTENT = "jcr:content";


}
