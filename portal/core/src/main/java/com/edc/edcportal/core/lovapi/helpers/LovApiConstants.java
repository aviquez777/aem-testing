package com.edc.edcportal.core.lovapi.helpers;

public class LovApiConstants {

    private LovApiConstants() {
        // Sonar LInt
    }

    public static final String GRANT_TYPE = "client_credentials";
    public static final String BANK_DOMAIN_SEPARATOR = "::";
    public static final String OTHER_VALUE = "OTH";
    
    public static final String LOV_API_TOKEN = "lov_api_token";
    public static final String FIND_BY_CODE_ENDPOINT = "/findcode/";

    /**
     * Header Key that must be present on all requests
     *
     * 
     */
    public enum HeaderParams {
        OCP_APIM_SUB_KEY("Ocp-apim-subscription-key");

        private String headerValue;

        HeaderParams(String headerValue) {
            this.headerValue = headerValue;
        }

        public String getHeaderValue() {
            return headerValue;
        }
    }

}
