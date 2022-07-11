package com.edc.edcportal.core.appauth;

import java.io.IOException;

import com.edc.edcportal.core.appauth.pojo.AppAuthBodyRequest;
import com.edc.edcportal.core.appauth.pojo.AppAuthResponseDO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utilities functions to parse JSON and retrieve data
 * 
 *
 */
public class AppAuthJsonDataBindingUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    private AppAuthJsonDataBindingUtil() {
        // sonar lint
    }

    public static AppAuthResponseDO jsonToAppAuthResponseDO(String jsonInString) throws IOException {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper.readValue(jsonInString, AppAuthResponseDO.class);
    }

    public static String appAuthBodyRequestToJson(AppAuthBodyRequest appAuthBodyRequest)
            throws JsonProcessingException {
        mapper.disable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES);
        return mapper.writeValueAsString(appAuthBodyRequest);
    }

}
