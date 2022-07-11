package com.edc.edcweb.core.models;
/**
 * POJO for Webinar Status Service
 *
 */

public class WebinarStatusResponse {

    private String timeStatus;
    private String errorMessage;

    public String getTimeStatus() {
        return timeStatus;
    }

    public void setTimeStatus(String timeStatus) {
        this.timeStatus = timeStatus;
    }

    public String geterrorMessage() {
        return errorMessage;
    }

    public void seterrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
