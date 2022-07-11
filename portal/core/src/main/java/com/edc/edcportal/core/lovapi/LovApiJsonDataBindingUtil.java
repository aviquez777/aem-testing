package com.edc.edcportal.core.lovapi;

import java.io.IOException;

import com.edc.edcportal.core.lovapi.pojo.GenericLovDO;
import com.edc.edcportal.core.lovapi.pojo.SingleLovDO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * Use Jackson Databind to transform from JSON to the Object
 *
 */
public class LovApiJsonDataBindingUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    private LovApiJsonDataBindingUtil() {
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
     * Transform JSON String from API to SingleLovDO
     * 
     * @param jsonString
     * @return SingleLovDO with API data
     * @throws IOException
     */
    public static SingleLovDO jsonToSingleLovDO(String jsonString) throws IOException {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper.readValue(jsonString, SingleLovDO.class);
    }

    /**
     * Transform JSON String from API to GenericSingleLovDO
     * 
     * @param jsonString
     * @return GenericSingleLovDO with API data
     */
    public static GenericLovDO jsonToGenericLovDO(String jsonString) throws IOException {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper.readValue(jsonString, GenericLovDO.class);
    }

}
