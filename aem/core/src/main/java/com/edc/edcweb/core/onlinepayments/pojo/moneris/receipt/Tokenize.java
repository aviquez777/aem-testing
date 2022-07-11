package com.edc.edcweb.core.onlinepayments.pojo.moneris.receipt;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tokenize {
    private String success;
    private String firstlast4;
    private String datakey;
    private String status;
    private String message;

    @JsonProperty("success")
    public String getSuccess() {
        return this.success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    @JsonProperty("firstlast4")
    public String getFirstlast4() {
        return this.firstlast4;
    }

    public void setFirstlast4(String firstlast4) {
        this.firstlast4 = firstlast4;
    }

    @JsonProperty("datakey")
    public String getDatakey() {
        return this.datakey;
    }

    public void setDatakey(String datakey) {
        this.datakey = datakey;
    }

    @JsonProperty("status")
    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("message")
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}