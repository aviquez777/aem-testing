package com.edc.edcportal.core.consentrequestgating;

import com.edc.edcportal.core.consentrequestgating.pojo.ConsentRequestGatingDO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utilities functions to parse JSON and retrieve data
 * 
 *
 */
public class CRGsonDataBindingUtil {
    
    private static final ObjectMapper mapper = new ObjectMapper();

    private CRGsonDataBindingUtil() {
        // Utility classes should not have public constructors
    }

    public static String consentRequestGatingDOToJson(ConsentRequestGatingDO consentRequestGatingDO)
            throws JsonProcessingException {
        mapper.disable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES);
        return mapper.writeValueAsString(consentRequestGatingDO);
    }
}
