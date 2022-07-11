package com.edc.edcweb.core.gatedcontentaccess;

import com.edc.edcweb.core.gatedcontentaccess.pojo.GatedContentAccesJsonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GatedContentAccessDataBindingUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    private GatedContentAccessDataBindingUtil() {
        // Sonar lint
    }

    public static String jsonResponseToJson(GatedContentAccesJsonResponse jsonResponse) throws JsonProcessingException {
        return mapper.writeValueAsString(jsonResponse);
    }
}
