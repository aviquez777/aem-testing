package com.edc.edcweb.core.onlinepayments.pojo.moneris.receipt.fraud;

import com.edc.edcweb.core.onlinepayments.pojo.moneris.deserializers.KountDetailsDeserializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Kount extends FraudDetailsBase {

    @JsonDeserialize(using = KountDetailsDeserializer.class)
    private KountDetails details;

    @JsonProperty("details")
    public KountDetails getDetails() {
        return this.details;
    }

    public void setDetails(KountDetails details) {
        this.details = details;
    }

}
