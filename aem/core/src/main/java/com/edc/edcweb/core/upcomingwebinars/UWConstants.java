package com.edc.edcweb.core.upcomingwebinars;

public class UWConstants {
    
    private UWConstants () {
         // SOnar LInt
    }

    // Servlet Info.
    public static final String SERVLET_URL = "/bin/upcomingeventsservlet";
    public static final String JSON_EXT = "json";
    public static final String REQ_PARAMETER = "pageUrl";

    // Component properties.
    public static final String PAGE_TITLE = "pageTitle";
    public static final String WEBINAR_LINK_TARGET = "webinarLinkTarget";
    public static final String PAGE_DESCRIPTION = "jcr:description";
    public static final String TEASER_IMAGE = "teaserimage";
    public static final String IMAGE_ALT_TEXT = "imagealttext";
    public static final String UW_START_DATE = "startDate";
    public static final String UW_END_DATE = "endDate";
    public static final String UW_DATE_FORMAT = "dd, yyyy";

    // UI Resources.
    public static final String RESOURCE_TYPE = "edc/components/content/event/upcomingevents";
    public static final String NODE = "upcomingevents";

    public static final String PATH_DIALOG_FIELD = "paths";
    public static final String TAGS_DIALOG_FIELD = "tags";}
