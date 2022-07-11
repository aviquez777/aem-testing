package com.edc.edcportal.core.errorhandler.pojo;

import java.io.Serializable;

public class EDCErrorCode implements Serializable {

    private static final long serialVersionUID = 2408280133379452681L;

    private String entityID;
    private String statusCode;
    private String statusMessage;
    private String redirectURL;

    public EDCErrorCode(String entityID, String statusCode, String statusMessage, String redirectURL) {
        this.entityID = entityID;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.redirectURL = redirectURL;
    }

    public String getEntityID() {
        return entityID;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getRedirectURL() {
        return redirectURL;
    }
}