package com.edc.edcportal.core.ci.helpers;

import java.io.IOException;

import org.json.JSONObject;

import com.edc.edcportal.core.ci.pojo.CiCompanySearchDO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utilities functions to parse JSON and retrieve data
 * 
 *
 */
public class CiJsonDataBindingUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    private CiJsonDataBindingUtil() {
        // sonar lint
    }

    public static CiCompanySearchDO jsonToCiCompanySearchDO(JSONObject jsonObject) throws IOException {
        String jsonInString = jsonObject.toString();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        return mapper.readValue(jsonInString, CiCompanySearchDO.class);
    }

}
