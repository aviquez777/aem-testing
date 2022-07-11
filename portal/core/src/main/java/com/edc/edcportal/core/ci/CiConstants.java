package com.edc.edcportal.core.ci;

import javax.inject.Inject;

import org.osgi.service.component.annotations.Reference;

import com.edc.edcportal.core.ci.services.CiConfigService;

/**
 * Values and properties needed to prepare the CI Requests
 *
 */

public class CiConstants {

    @Reference
    @Inject
    private static CiConfigService ciConfigService;

    public static final String QUESTION_MARK = "?";
    public static final String EQUAL = "=";
    public static final String AMPERSAND = "&";
    public static final String POUND = "#";
    public static final String COMMA = ",";
    public static final String SPACE = " ";
    public static final String COMMA_SPACE = COMMA.concat(SPACE);
    public static final String SAMPLE_TEXT = SPACE.concat("sample");

    public static final String GRANT_TYPE = "client_credentials";

    public static final String CI_COUNTRY_LIST_JSON_SERVLET_URL = "/_jcr_content.countrylist.json";
    public static final String CI_COMPANY_SEARCH_SERVICE_SERVLET_URL = "/bin/myedc/ciCompanySearch";

    public static final String SEARCH_SESSION_VAR = "ciObject";

    public static final String COUNTRY_SEARCH_FORM_FIELD = "country";
    public static final String COMPANY_SEARCH_FORM_FIELD = "name";
    public static final String LANGUAGE_SEARCH_FORM_FIELD = "language";

    public static final String RESPONSE_STATUS_JSON_KEY = "responseStatus";

    public static final String JSON_COMPANY_ID_KEY = "companyId";
    public static final String JSON_COMPANY_NAME_KEY = "companyName";

    public static final String SECTION_ARAY_IDENTIFIED = "_array";

    public static final String SECTION_HEADQUARTER = "headquarter";

    public static final String MAP_LINK_VAR = "googleMapLink";

    public static final String BANNER_TYPE_DEFAULT = "default";
    public static final String BANNER_TYPE_POPERTY_FIELD = "displaytype";
    
    public static final String SERVLET_SELECTOR = "countrylist";
    public static final String SERVLET_RESOURCE_TYPE="edc/components/structure/page";
    
    public static final String PREMIUM = "/premium";
    /**
     * Api endopoints
     *
     */
    public enum EndPoints {

        CI_ENDPOINT_GET_COUNTRIES("/countries"), CI_ENDPOINT_SEARCH_COMPANY_BY_NAME("/companies/search"),
        CI_ENDPOINT_SEARCH_COMPANY_BY_NAME_COUNTRY_QUERY_PARAM(COUNTRY_SEARCH_FORM_FIELD.concat(EQUAL)),
        CI_ENDPOINT_SEARCH_COMPANY_BY_NAME_COMPANY_NAME_QUERY_PARAM(COMPANY_SEARCH_FORM_FIELD.concat(EQUAL)),
        CI_ENDPOINT_SEARCH_COMPANY_BY_NAME_LANGUAGE_QUERY_PARAM(LANGUAGE_SEARCH_FORM_FIELD.concat(EQUAL)),
        CI_ENDPOINT_GET_COMPANY_DETAILS_BY_ID("/companies/");

        private String endPoint;

        EndPoints(String endPoint) {
            this.endPoint = endPoint;
        }

        public String getEndPoint() {
            return endPoint;
        }
    }

    /**
     * Header Key that must be present on all requests
     *
     * 
     */
    public enum HeaderParams {
        OCP_CI_SUB_KEY("Ocp-Apim-Subscription-Key");

        private String headerValue;

        HeaderParams(String headerValue) {
            this.headerValue = headerValue;
        }

        public String getHeaderValue() {
            return headerValue;
        }
    }

    /**
     * APIErrors
     *
     * 
     */
    public enum APIErrors {
        RESULTS_FOUND("ResultsFound"), NO_RESULTS("NoResults"), TOO_MANY_RESULTS("TooManyResults");

        private String error;

        APIErrors(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}
