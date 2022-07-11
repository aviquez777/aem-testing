package com.edc.edcweb.core.gps.pojo;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GpsResponse {

    @JsonProperty("Version")
    private String version;

    @JsonProperty("ErrorMessage")
    private String errorMessage = "Error";

    @JsonProperty("StatusCode")
    private int statusCode = 500;

    @JsonProperty("Result")
    GpsResponseResult gpsResult;

    // AEM Use Only
    @JsonProperty("ConfirmationNumber")
    private String confirmationNumber;

    // AEM Use Only
    @JsonProperty("ExtErrorMessages")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Map<String, String> extErrorMessages = new HashMap<>();

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public GpsResponseResult getGpsResult() {
        return gpsResult;
    }

    public void setGpsResult(GpsResponseResult gpsResult) {
        this.gpsResult = gpsResult;
    }

    public String getConfirmationNumber() {
        return confirmationNumber;
    }

    public void setConfirmationNumber(String confirmationNumber) {
        this.confirmationNumber = confirmationNumber;
    }

    public Map<String, String> getExtErrorMessages() {
        return extErrorMessages;
    }

    public void setExtErrorMessages(Map<String, String> extErrorMessages) {
        this.extErrorMessages = extErrorMessages;
    }
}
