package com.edc.edcportal.core.eloqua.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EloquaEndPointUrls {

    private EloquaEndPointApis apis;
    private String base;

    @JsonProperty("apis")
    public EloquaEndPointApis getApis() {
        return apis;
    }

    public void setApis(EloquaEndPointApis apis) {
        this.apis = apis;
    }

    @JsonProperty("base")
    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

}
