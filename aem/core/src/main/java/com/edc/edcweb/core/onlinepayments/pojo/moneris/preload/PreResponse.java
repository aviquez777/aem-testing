package com.edc.edcweb.core.onlinepayments.pojo.moneris.preload;

import com.edc.edcweb.core.onlinepayments.pojo.moneris.common.MonerisError;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PreResponse {

    private String success;
    private MonerisError error;
    private String ticket;

    @JsonProperty("success")
    public String getSuccess() {
        return success;
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

    @JsonProperty("ticket")
    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}