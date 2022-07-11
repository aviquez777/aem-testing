package com.edc.edcportal.core.appauth.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * AppAuthBodyRequest POJO to use with Jackson databind
 * 
 **/

public class AppAuthBodyRequest {
    @JsonProperty("ExternalUserIdentifier")
    private String externalUserIdentifier;

    @JsonProperty("AppId")
    private String appId;

    public AppAuthBodyRequest() {
        // empty constructor if necessary
    }

    public AppAuthBodyRequest(String externalUserID, String appId) {
        this.setExternalUserIdentifier(externalUserID);
        this.setAppId(appId);
    }

    public String getExternalUserIdentifier() {
        return externalUserIdentifier;
    }

    public void setExternalUserIdentifier(String externalUserIdentifier) {
        this.externalUserIdentifier = externalUserIdentifier;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
