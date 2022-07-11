package com.edc.edcweb.core.onlinepayments.pojo.moneris.receipt;

import com.edc.edcweb.core.onlinepayments.pojo.moneris.common.MonerisError;
import com.edc.edcweb.core.onlinepayments.pojo.moneris.common.Request;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecResponse {
    private String success;
    private MonerisError error;
    private Request request;
    private Receipt receipt;

    @JsonProperty("success")
    public String getSuccess() {
        return this.success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    @JsonProperty("error")
    public MonerisError getError() {
        return error;
    }

    public void setError(MonerisError error) {
        this.error = error;
    }

    @JsonProperty("request")
    public Request getRequest() {
        return this.request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    @JsonProperty("receipt")
    public Receipt getReceipt() {
        return this.receipt;
    }

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
    }

}