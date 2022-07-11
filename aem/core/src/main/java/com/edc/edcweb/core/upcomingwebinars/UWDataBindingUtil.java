package com.edc.edcweb.core.upcomingwebinars;


import com.edc.edcweb.core.upcomingwebinars.pojo.UWResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UWDataBindingUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    private UWDataBindingUtil() {
        // Sonar lint
    }

    /**
     * Generic function to convert an user object into a JSON formatted string.
     * 
     * @param pojo
     * @return JSON formatted string
     * @throws JsonProcessingException
     */
    public static String uWResponseToJson(UWResponse uWResponseToJson) throws JsonProcessingException {
        return mapper.writeValueAsString(uWResponseToJson);
    }

}
