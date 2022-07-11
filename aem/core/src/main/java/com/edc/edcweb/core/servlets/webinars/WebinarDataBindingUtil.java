package com.edc.edcweb.core.servlets.webinars;

import com.edc.edcweb.core.models.WebinarStatusResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WebinarDataBindingUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    private WebinarDataBindingUtil() {
        // Sonar lint
    }

    public static String webinarStatusResponseToJson(WebinarStatusResponse webinarResponse) throws JsonProcessingException {
        return mapper.writeValueAsString(webinarResponse);
    }

}
