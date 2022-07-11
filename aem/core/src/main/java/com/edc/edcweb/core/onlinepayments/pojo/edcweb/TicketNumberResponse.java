package com.edc.edcweb.core.onlinepayments.pojo.edcweb;

import com.edc.edcweb.core.onlinepayments.pojo.moneris.common.MonerisError;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TicketNumberResponse {

    private MonerisError error;
    private String ticket;

    @JsonProperty("error")
    public MonerisError getError() {
        return error;
    }

    public void setError(MonerisError error) {
        this.error = error;
    }

    @JsonProperty("ticket")
    public String getTicket() {
        return this.ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

}
