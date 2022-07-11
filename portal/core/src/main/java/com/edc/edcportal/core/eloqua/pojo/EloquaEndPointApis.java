package com.edc.edcportal.core.eloqua.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EloquaEndPointApis {

    private EloquaEndPointRest rest;
    private EloquaEndPointSoap soap;

    @JsonProperty("rest")
    public EloquaEndPointRest getRest() {
        return rest;
    }

    public void setRest(EloquaEndPointRest rest) {
        this.rest = rest;
    }

    @JsonProperty("soap")
    public EloquaEndPointSoap getSoap() {
        return soap;
    }

    public void setSoap(EloquaEndPointSoap soap) {
        this.soap = soap;
    }

}
