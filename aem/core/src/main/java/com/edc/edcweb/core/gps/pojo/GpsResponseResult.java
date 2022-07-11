package com.edc.edcweb.core.gps.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GpsResponseResult {
    @JsonProperty("GpsIdentifier")
    private String gpsIdentifier;

    @JsonProperty("GpsRequest")
    private GpsResponseRequest gpsResponseRequest;

    public String getGpsIdentifier() {
        return gpsIdentifier;
    }

    public void setGpsIdentifier(String gpsIdentifier) {
        this.gpsIdentifier = gpsIdentifier;
    }

    public GpsResponseRequest getGpsResponseRequest() {
        return gpsResponseRequest;
    }

    public void setGpsResponseRequest(GpsResponseRequest gpsResponseRequest) {
        this.gpsResponseRequest = gpsResponseRequest;
    }

}
