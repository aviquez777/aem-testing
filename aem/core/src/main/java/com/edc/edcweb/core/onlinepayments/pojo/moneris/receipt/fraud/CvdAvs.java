package com.edc.edcweb.core.onlinepayments.pojo.moneris.receipt.fraud;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CvdAvs extends FraudDetailsBase {

    private String details;

    @JsonProperty("details")
    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

}
