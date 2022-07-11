package com.edc.edcweb.core.onlinepayments.pojo.moneris.receipt.fraud;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ThreeDSecureDetails {
    private String veRes;
    private String paRes;
    private String message;
    private String cavv;
    private boolean loadvbv;

    @JsonProperty("VERes")
    public String getVeRes() {
        return veRes;
    }

    public void setVeRes(String veRes) {
        this.veRes = veRes;
    }

    @JsonProperty("PARes")
    public String getPaRes() {
        return paRes;
    }

    public void setPaRes(String paRes) {
        this.paRes = paRes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCavv() {
        return cavv;
    }

    public void setCavv(String cavv) {
        this.cavv = cavv;
    }

    public boolean getLoadvbv() {
        return loadvbv;
    }

    public void setLoadvbv(boolean loadvbv) {
        this.loadvbv = loadvbv;
    }

}