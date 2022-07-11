package com.edc.edcportal.core.businessconnections;

import java.io.IOException;

import com.edc.edcportal.core.businessconnections.pojo.BCRecordDO;
import com.edc.edcportal.core.businessconnections.pojo.BCTokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BCJsonDataBindingUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    private BCJsonDataBindingUtil() {
        // sonar lint
    }

    public static String bcRecordDOToJson(BCRecordDO bcRecordDO) throws JsonProcessingException {
        mapper.disable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES);
        return mapper.writeValueAsString(bcRecordDO);
    }

    public static String bcTokenResponseToJson(BCTokenResponse bcTokenResponse) throws JsonProcessingException {
        mapper.disable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES);
        return mapper.writeValueAsString(bcTokenResponse);
    }

    public static BCTokenResponse jsonToTokenResponse(String jsonString) throws IOException {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper.readValue(jsonString, BCTokenResponse.class);
    }
}