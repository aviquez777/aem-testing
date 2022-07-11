package com.edc.edcweb.core.lovapi.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SingleLovDO {

    @JsonProperty("Version")
    private String version;
    @JsonProperty("StatusCode")
    private String statusCode;
    @JsonProperty("ErrorMessage")
    private String errorMessage;
    @JsonProperty("Result")
    private GenericLov result;

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

    public GenericLov getResult() {
        return result;
    }

    public void setResult(GenericLov result) {
        this.result = result;
    }

}
