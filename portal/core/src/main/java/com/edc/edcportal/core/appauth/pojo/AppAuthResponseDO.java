package com.edc.edcportal.core.appauth.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * AppAuthResponseDO POJO to use with Jackson databind
 * 
 **/

public class AppAuthResponseDO {

    @JsonProperty("Version")
    private String version;

    @JsonProperty("StatusCode")
    private String statusCode;

    @JsonProperty("ErrorMessage")
    private String errorMessage;

    @JsonProperty("Result")
    private AppAuthResult result = new AppAuthResult();

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public AppAuthResult getResult() {
        return result;
    }

    public void setResult(AppAuthResult result) {
        this.result = result;
    }

}