package com.edc.edcportal.core.servlets.dashboard.helpers;

import com.edc.edcportal.core.servlets.dashboard.pojo.DashboardPageListDO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utilities functions to parse JSON and retrieve data
 * 
 *
 */
public class DashboardJsonDataBindingUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    private DashboardJsonDataBindingUtil() {
        // sonar lint
    }

    public static String dashboardPageListDOToJson(DashboardPageListDO dashboardPageListDO)
            throws JsonProcessingException {
        mapper.disable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES);
        return mapper.writeValueAsString(dashboardPageListDO);
    }

}
