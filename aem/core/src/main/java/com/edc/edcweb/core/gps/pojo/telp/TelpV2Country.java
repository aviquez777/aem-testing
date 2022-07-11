package com.edc.edcweb.core.gps.pojo.telp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TelpV2Country {
    @JsonProperty("CountryCode")
    public String getCountryCode() {
        return this.countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    String countryCode;

    @JsonProperty("CountryNameEn")
    public String getCountryNameEn() {
        return this.countryNameEn;
    }

    public void setCountryNameEn(String countryNameEn) {
        this.countryNameEn = countryNameEn;
    }

    String countryNameEn;

    @JsonProperty("CountryNameFr")
    public String getCountryNameFr() {
        return this.countryNameFr;
    }

    public void setCountryNameFr(String countryNameFr) {
        this.countryNameFr = countryNameFr;
    }

    String countryNameFr;
}