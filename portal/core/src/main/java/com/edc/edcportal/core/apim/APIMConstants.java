package com.edc.edcportal.core.apim;

/**
 * Values and properties needed to prepare the APIM Requests
 *
 */

public class APIMConstants {

    public static final String MULTI_VALUE_SEPARATOR = "?";
    public static final String GRANT_TYPE = "client_credentials";

    public static final String APIM_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String HARD_VALUE = "hardValue";
    public static final String DATE = "date";
    public static final String FIELD_NAME = "fieldName";
    public static final String LABEL = "label";

    public static final String USER_CASL_CONSENT = "Web Form";
    public static final String USER_CASL_CONSENT_CODE = "Expressed";

    public static final String PHONE_EXT_PREFIX = "ext.";

    public static final String APIM_TOKEN = "apim_token";

    /**
     * Api endopoints
     *
     */
    public enum EndPoints {

        APIM_ENDPOINT_INFOPAYMMENT("infopayment"), APIM_ENDPOINT_EXTERNAL_ID_QUERY_PARAM("?externalUserId=");

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
        OCP_APIM_SUB_KEY("Ocp-Apim-Subscription-Key");

        private String headerValue;

        HeaderParams(String headerValue) {
            this.headerValue = headerValue;
        }

        public String getHeaderValue() {
            return headerValue;
        }
    }

    /**
     * Infopayment expected results for comparison
     *
     * 
     */
    public enum InfoPaymentResults {
        NONE("None"), ACTIVE("Active"), EXPIRED("Expired");

        private String result;

        InfoPaymentResults(String result) {
            this.result = result;
        }

        public String getResult() {
            return result;
        }

    }
}
