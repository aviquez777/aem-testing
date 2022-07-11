package com.edc.edcweb.core.onlinepayments.pojo.moneris.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MonerisError {
    private String field;
    private String message;
    private String txnTotal;

    @JsonProperty("field")
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("txn_total")
    public String getTxnTotal() {
        return txnTotal;
    }

    public void setTxnTotal(String txnTotal) {
        this.txnTotal = txnTotal;
    }
}
