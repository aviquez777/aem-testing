package com.edc.edcweb.core.telp.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TelpCountry {
    @JsonProperty("CountryCode")
    private String countryCode;

    @JsonProperty("CountryNameEn")
    private String countryNameEn;

    @JsonProperty("CountryNameFr")
    private String countryNameFr;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryNameEn() {
        return countryNameEn;
    }

    public void setCountryNameEn(String countryNameEn) {
        this.countryNameEn = countryNameEn;
    }

    public String getCountryNameFr() {
        return countryNameFr;
    }

    public void setCountryNameFr(String countryNameFr) {
        this.countryNameFr = countryNameFr;
    }

}
