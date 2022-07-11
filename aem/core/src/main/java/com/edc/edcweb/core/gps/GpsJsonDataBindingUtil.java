package com.edc.edcweb.core.gps;

import com.edc.edcweb.core.gps.pojo.GpsResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GpsJsonDataBindingUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    private GpsJsonDataBindingUtil() {
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

    /**
     * Convert GpsResponseJson to GpsResponse object
     * 
     * @param json GpsResponseJson
     * @return GpsResponse
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    public static GpsResponse jsonToGpsResponse(String json) throws JsonProcessingException {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper.readValue(json, GpsResponse.class);
    }
}
