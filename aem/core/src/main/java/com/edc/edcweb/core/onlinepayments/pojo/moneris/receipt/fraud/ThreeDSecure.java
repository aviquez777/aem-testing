package com.edc.edcweb.core.onlinepayments.pojo.moneris.receipt.fraud;

import com.edc.edcweb.core.onlinepayments.pojo.moneris.deserializers.ThreeDSecureDetailsDeserializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThreeDSecure extends FraudDetailsBase {

    @JsonDeserialize(using = ThreeDSecureDetailsDeserializer.class)
    ThreeDSecureDetails details = new ThreeDSecureDetails();

    @JsonProperty("details")
    public ThreeDSecureDetails getDetails() {
        return this.details;
    }

    public void setDetails(ThreeDSecureDetails details) {
        this.details = details;
    }
}
