package com.edc.edcweb.core.consentrequestgating.helpers;

public class CRGConstants {

    private CRGConstants() {
        // Utility classes should not have public constructors
    }

    public static final String CRG_SERVLET_URL = "/bin/myedc/crgservlet";
    public static final String PREMIUM_RESOURCE_TYPE_BASE = "edc/components/content/premium";
    public static final String GRS_COMPONENT_RESOURCE_TYPE = PREMIUM_RESOURCE_TYPE_BASE.concat("/consentrequestgating");
    public static final String PP_COMPONENT_RESOURCE_TYPE = PREMIUM_RESOURCE_TYPE_BASE.concat("/progressiveprofiling");
    public static final String GATED_URL_PROPERTY = "gatedURL";
    public static final String MULTIFIELD_NAME = "checkboxes";
    public static final String MULTIFIELD_ITEM_VALUE_PROPERTY = "checkboxValue";
    public static final String API_ERROR = "webinar-api-error";
    public static final String PP_MODE_PROPERTY = "mode";

}
