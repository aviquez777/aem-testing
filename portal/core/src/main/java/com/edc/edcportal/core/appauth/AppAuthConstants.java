package com.edc.edcportal.core.appauth;

/**
 * Values and properties needed to prepare the APPAUTH Requests
 *
 */

public class AppAuthConstants {

    public static final String MULTI_VALUE_SEPARATOR = "?";
    public static final String GRANT_TYPE = "client_credentials";
    public static final String APPAUTH_TOKEN = "appauth_token";


    /**
     * Api endopoints
     *
     */
    public enum EndPoints {

        APPAUTH_ENDPOINT_EXTERNAL_ID_QUERY_PARAM("?externalUserIdentifier="),APPAUTH_ENDPOINT_APP_ID_QUERY_PARAM("&appId="), ;

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
     */
    public enum HeaderParams {
        OCP_APPAUTH_SUB_KEY("Ocp-Apim-Subscription-Key");

        private String headerValue;

        HeaderParams(String headerValue) {
            this.headerValue = headerValue;
        }

        public String getHeaderValue() {
            return headerValue;
        }
    }
}
