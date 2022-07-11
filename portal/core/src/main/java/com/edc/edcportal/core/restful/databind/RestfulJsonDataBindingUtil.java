package com.edc.edcportal.core.restful.databind;

import com.edc.edcportal.core.restful.pojo.ErrorResponseDO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RestfulJsonDataBindingUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    private RestfulJsonDataBindingUtil() {
        // sonar lint
    }

    public static String errorResponseDOToJson(ErrorResponseDO errorResponseDO) throws JsonProcessingException {
        mapper.disable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES);
        return mapper.writeValueAsString(errorResponseDO);
    }
}