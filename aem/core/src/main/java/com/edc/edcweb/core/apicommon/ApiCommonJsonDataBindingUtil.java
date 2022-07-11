package com.edc.edcweb.core.apicommon;

import com.edc.edcweb.core.apicommon.pojo.APICommonToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApiCommonJsonDataBindingUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    private ApiCommonJsonDataBindingUtil() {
        // Sonar lint
    }

    /**
     * Generic function to convert an user object into a JSON formatted string.
     * 
     * @param pojo
     * @return JSON formatted string
     * @throws JsonProcessingException
     */
    public static String pojoToJson(Object pojo) throws JsonProcessingException {
        return mapper.writeValueAsString(pojo);
    }

    public static APICommonToken jsonToApimToken(String json) throws JsonProcessingException {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper.readValue(json, APICommonToken.class);
    }
}
