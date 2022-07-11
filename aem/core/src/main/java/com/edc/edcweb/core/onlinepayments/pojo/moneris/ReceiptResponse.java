package com.edc.edcweb.core.onlinepayments.pojo.moneris;

import com.edc.edcweb.core.onlinepayments.pojo.moneris.receipt.RecResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReceiptResponse {

    private RecResponse response = new RecResponse();

    @JsonProperty("response")
    public RecResponse getResponse() {
        return this.response;
    }

    public void setResponse(RecResponse response) {
        this.response = response;
    }

}
