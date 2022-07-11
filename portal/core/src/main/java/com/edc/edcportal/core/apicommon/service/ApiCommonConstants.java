package com.edc.edcportal.core.apicommon.service;

public class ApiCommonConstants {

    private ApiCommonConstants() {
        // Sonar LInt
    }

    public static final String ERROR_MSG = "error";
    public static final String TOKEN_ERROR_MSG = "unauthorized_client";

    /**
     * 
     * Token Form Parameter Values
     *
     */
    public enum TokenFormParams {

        CLIENT_ID_KEY("client_id"), CLIENT_SECRET_KEY("client_secret"), GRANT_TYPE_KEY("grant_type"),
        RESOURCE_KEY("resource"), GRANT_TYPE_VALUE("client_credentials");

        private String value;

        TokenFormParams(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    /**
     * Header Key that must be present on all requests
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
