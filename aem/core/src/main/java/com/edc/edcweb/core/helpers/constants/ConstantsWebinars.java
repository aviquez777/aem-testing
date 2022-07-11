package com.edc.edcweb.core.helpers.constants;

public class ConstantsWebinars {

    private ConstantsWebinars() {
        // Sonar lint
    }

    public static final String SHOW_KEY = "webinarshowkey";
    public static final String SHOW_BASE_URL = "https://onlinexperiences.com/Launch/Event/ShowKey=";
    public static final String PRODUCT_TYPE = "Webinar";
    // URLS
    public static final String WEBINAR_STATUS_SERVLET_URL = "/bin/webinarstatus";
    public static final String WEBINAR_REGISTER_SERVLET_URL = "/bin/myedc/webinarservice";
    // JSON Key
    public static final String WEBINAR_TIME_STATUS_JSON_KEY = "timeStatus";
    // Event Page propoerties
    public static final String WEBINAR_START_DATE = "startDate";
    public static final String WEBINAR_END_DATE = "endDate";
    public static final String WEBINAR_START_TIME = "startTime";
    public static final String WEBINAR_END_TIME = "endTime";
    // Responses value
    public static final String WEBINAR_STATE_ONDEMAND = "ondemand";
    public static final String WEBINAR_STATE_UPCOMING = "upcoming";
    public static final String WEBINAR_STATE_LIVE = "live";
    // General Text
    public static final String DONT_HAVE_ACCOUNT = "donthaveanaccounttext";
    public static final String CREATE_ACCOUNT = "createaccountlinktext";
    public static final String DEFAULT_ERROR = "defaulterrortext";
    // Upcoming
    public static final String UPCOMING_TAGTEXT = "upcomingtagtext";
    public static final String LOGGEDOUT_UPCOMING_BTNTEXT = "loggedoutupcomingbtntext";
    public static final String LOGGEDIN_UPCOMING_BTNTEXT = "loggedinupcomingbtntext";
    public static final String REGISTERED_TO_UPCOMING_TEXT = "registeredtoupcomingtext";
    // Live
    public static final String LIVE_TAGTEXT = "livetagtext";
    public static final String LOGGEDOUT_LIVE_BTNTEXT = "loggedoutlivebtntext";
    public static final String LOGGEDIN_LIVE_BTNTEXT = "loggedinlivebtntext";
    // On Demand
    public static final String ONDEMAND_TAGTEXT = "ondemandtagtext";
    public static final String LOGGEDOUT_ONDEMAND_BTNTEXT = "loggedoutondemandbtntext";
    public static final String LOGGEDIN_ONDEMAND_BTNTEXT = "loggedinondemandbtntext";
    
    public static final String ARKADIN_MODAL_TITLE = "arkadinmodaltitle";
    public static final String ARKADIN_MODAL_TEXT = "arkadinmodaltext";
    public static final String SPECIAL_MODAL_TEXT = "specialmodaltext";
    public static final String SPECIAL_ARKADIN_MODAL_TEXT = "specialarkadinmodaltext";
    public static final String ARKADIN_AGREE_BUTTON_TEXT = "arkadinagreebuttontext";
    public static final String ARKADIN_CANCEL_BUTTON_TEXT = "arkadincancelbuttontext";
    public static final String ARKADIN_MODAL_ARIA_CLOSE = "arkadinmodalariaclose";
    public static final String ARKADIN_MODAL_ARIA_SCROLL = "arkadinmodalariascroll";
    public static final String WEBINAR_PARTNERS_MULTIFIELD ="partners";
    public static final String WEBINAR_WIT_RADIOS = "witradios";
    // Errors
    public static final String PAGE_LOAD_ERROR = "pageLoadError";

    public static final String PAGE_HERO_BANNER_RESOURCE = "edc/components/content/pageherobanner";
    public static final String WEBNAR_CTA_RESOURCE = "edc/components/content/event/webinarcta";
    public static final String STRUCTURE = "structure";
    public static final String POLICY_STRUCTURE = "/".concat(STRUCTURE);
    public static final String JCR_CONTENT = "jcr:content";

}
