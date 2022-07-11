package com.edc.edcweb.core.onlinepayments.pojo.moneris.deserializers;

import java.io.IOException;

import com.edc.edcweb.core.onlinepayments.pojo.moneris.receipt.fraud.ThreeDSecureDetails;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class ThreeDSecureDetailsDeserializer extends JsonDeserializer<ThreeDSecureDetails> {

    public ThreeDSecureDetailsDeserializer () {
        // No arguments constructor
    }

    @Override
    public ThreeDSecureDetails deserialize(JsonParser jp, DeserializationContext dc)
            throws IOException {
        JsonToken jsonToken = jp.getCurrentToken();
        ThreeDSecureDetails detail = new ThreeDSecureDetails();
        if (jsonToken != JsonToken.VALUE_STRING) {
            ObjectCodec oc = jp.getCodec();
            detail =  oc.readValue(jp, ThreeDSecureDetails.class);
        }
        return detail;
    }
}