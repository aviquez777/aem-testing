package com.edc.edcportal.core.appauth.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * AppAuthResult POJO to use with Jackson databind
 * 
 **/
public class AppAuthResult {

    @JsonProperty("User")
    private AppAuthUser user = new AppAuthUser();

    @JsonProperty("AppId")
    private String appId;

    @JsonProperty("HasUpdate")
    private Boolean hasUpdate = false;

    @JsonProperty("IsAllowed")
    private Boolean isAllowed = false;

    @JsonProperty("IsCreated")
    private Boolean isCreated = false;

    public AppAuthUser getUser() {
        return user;
    }

    public void setUser(AppAuthUser user) {
        this.user = user;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Boolean getHasUpdate() {
        return hasUpdate;
    }

    public void setHasUpdate(Boolean hasUpdate) {
        this.hasUpdate = hasUpdate;
    }

    public Boolean getIsAllowed() {
        return isAllowed;
    }

    public void setIsAllowed(Boolean isAllowed) {
        this.isAllowed = isAllowed;
    }

    public Boolean getIsCreated() {
        return isCreated;
    }

    public void setIsCreated(Boolean isCreated) {
        this.isCreated = isCreated;
    }

}
