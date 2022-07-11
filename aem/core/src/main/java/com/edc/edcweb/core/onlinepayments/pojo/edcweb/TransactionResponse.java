package com.edc.edcweb.core.onlinepayments.pojo.edcweb;

import com.edc.edcweb.core.onlinepayments.pojo.moneris.common.MonerisError;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponse {

    private MonerisError error;
    private EDCReceipt receipt;

    @JsonProperty("error")
    public MonerisError getError() {
        return error;
    }

    public void setError(MonerisError error) {
        this.error = error;
    }

    @JsonProperty("receipt")
    public EDCReceipt getReceipt() {
        return this.receipt;
    }

    public void setReceipt(EDCReceipt receipt) {
        this.receipt = receipt;
    }
    
    
}
