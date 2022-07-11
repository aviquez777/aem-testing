package com.edc.edcportal.core.eloqua.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EloquaEndPoint {

    private EloquaEndPointSite site;
    private EloquaEndPointUrls urls;
    private EloquaEndPointUser user;

    @JsonProperty("site")
    public EloquaEndPointSite getSite() {
        return site;
    }

    public void setSite(EloquaEndPointSite site) {
        this.site = site;
    }

    @JsonProperty("urls")
    public EloquaEndPointUrls getUrls() {
        return urls;
    }

    public void setUrls(EloquaEndPointUrls urls) {
        this.urls = urls;
    }

    @JsonProperty("user")
    public EloquaEndPointUser getUser() {
        return user;
    }

    public void setUser(EloquaEndPointUser user) {
        this.user = user;
    }

}
