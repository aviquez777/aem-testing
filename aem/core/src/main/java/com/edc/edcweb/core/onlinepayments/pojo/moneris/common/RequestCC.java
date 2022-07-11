package com.edc.edcweb.core.onlinepayments.pojo.moneris.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestCC {
    private String first6last4;
    private String expiry;
    private String cardholder;

    @JsonProperty("first6last4")
    public String getFirst6last4() {
        return this.first6last4;
    }

    public void setFirst6last4(String first6last4) {
        this.first6last4 = first6last4;
    }

    @JsonProperty("expiry")
    public String getExpiry() {
        return this.expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    @JsonProperty("cardholder")
    public String getCardholder() {
        return this.cardholder;
    }

    public void setCardholder(String cardholder) {
        this.cardholder = cardholder;
    }
}
