package com.edc.edcportal.core.servlets.webinar.pojo;

public class WebinarResponseDO {

    private String sessionId;
    private Boolean userRegistered = false;
    private Boolean accountProvisioned = false;
    private String errorMessage;
    private String webinarUrl;
    private String timeStatus;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Boolean getUserRegistered() {
        return userRegistered;
    }

    public void setUserRegistered(Boolean userRegistered) {
        this.userRegistered = userRegistered;
    }

    public Boolean getAccountProvisioned() {
        return accountProvisioned;
    }

    public void setAccountProvisioned(Boolean accountProvisioned) {
        this.accountProvisioned = accountProvisioned;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getWebinarUrl() {
        return webinarUrl;
    }

    public void setWebinarUrl(String webinarUrl) {
        this.webinarUrl = webinarUrl;
    }

    public String getTimeStatus() {
        return timeStatus;
    }

    public void setTimeStatus(String timeStatus) {
        this.timeStatus = timeStatus;
    }

}
