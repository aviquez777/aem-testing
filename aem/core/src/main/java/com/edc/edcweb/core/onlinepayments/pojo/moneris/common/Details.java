package com.edc.edcweb.core.onlinepayments.pojo.moneris.common;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Details {
    private String vERes;
    private String pARes;
    private String message;
    private String cavv;
    private String responseCode;
    private String receiptID;
    private String result;
    private List<String> error;

    boolean loadvbv;

    @JsonProperty("VERes")
    public String getVERes() {
        return this.vERes;
    }

    public void setVERes(String vERes) {
        this.vERes = vERes;
    }

    @JsonProperty("PARes")
    public String getPARes() {
        return this.pARes;
    }

    public void setPARes(String pARes) {
        this.pARes = pARes;
    }

    @JsonProperty("message")
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("cavv")
    public String getCavv() {
        return this.cavv;
    }

    public void setCavv(String cavv) {
        this.cavv = cavv;
    }

    @JsonProperty("loadvbv")
    public boolean getLoadvbv() {
        return this.loadvbv;
    }

    public void setLoadvbv(boolean loadvbv) {
        this.loadvbv = loadvbv;
    }

    @JsonProperty("responseCode")
    public String getResponseCode() {
        return this.responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    @JsonProperty("receiptID")
    public String getReceiptID() {
        return this.receiptID;
    }

    public void setReceiptID(String receiptID) {
        this.receiptID = receiptID;
    }

    @JsonProperty("result")
    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @JsonProperty("error")
    public List<String> getError() {
        return this.error;
    }

    public void setError(List<String> error) {
        this.error = error;
    }

}