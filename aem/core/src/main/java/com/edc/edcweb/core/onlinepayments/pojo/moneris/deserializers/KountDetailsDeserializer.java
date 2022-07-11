package com.edc.edcweb.core.onlinepayments.pojo.moneris.deserializers;

import java.io.IOException;

import com.edc.edcweb.core.onlinepayments.pojo.moneris.receipt.fraud.KountDetails;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class KountDetailsDeserializer extends JsonDeserializer<KountDetails> {

    public KountDetailsDeserializer () {
        // No arguments constructor
    }

    @Override
    public KountDetails deserialize(JsonParser jp, DeserializationContext dc)
            throws IOException {
        JsonToken jsonToken = jp.getCurrentToken();
        KountDetails detail = new KountDetails();
        if (jsonToken != JsonToken.VALUE_STRING) {
            ObjectCodec oc = jp.getCodec();
            detail =  oc.readValue(jp, KountDetails.class);
        }
        return detail;
    }
}
