package com.edc.edcportal.core.apim;

import java.io.IOException;

import com.edc.edcportal.core.apim.pojo.InfoPaymentDO;
import com.edc.edcportal.core.apim.pojo.InfoPaymentUpdateDO;
import com.edc.edcportal.core.apim.pojo.InfoPaymentStatusDO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utilities functions to parse JSON and retrieve data
 * 
 *
 */
public class APIMJsonDataBindingUtil {

    private APIMJsonDataBindingUtil() {
        // sonar lint
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    public static InfoPaymentStatusDO jsonToInfoPaymentStatusDO(String jsonString) throws IOException {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper.readValue(jsonString, InfoPaymentStatusDO.class);
    }

    public static InfoPaymentUpdateDO jsonToInfoPaymentUpdateDO(String jsonString) throws IOException {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper.readValue(jsonString, InfoPaymentUpdateDO.class);
    }

    public static String infoPaymentDOToJson(InfoPaymentDO infoPaymentDO) throws JsonProcessingException {
        mapper.disable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES);
        return mapper.writeValueAsString(infoPaymentDO);
    }

}
