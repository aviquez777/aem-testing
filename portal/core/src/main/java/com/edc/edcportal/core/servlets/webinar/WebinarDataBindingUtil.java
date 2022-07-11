package com.edc.edcportal.core.servlets.webinar;

import com.edc.edcportal.core.servlets.webinar.pojo.WebinarResponseDO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WebinarDataBindingUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    private WebinarDataBindingUtil() {
        // Sonar lint
    }

    public static String webinarResponseDOToJson(WebinarResponseDO webinarResponse) throws JsonProcessingException {
        return mapper.writeValueAsString(webinarResponse);
    }

}
