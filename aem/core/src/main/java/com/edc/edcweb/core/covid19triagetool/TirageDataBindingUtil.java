package com.edc.edcweb.core.covid19triagetool;

import com.edc.edcweb.core.covid19triagetool.pojo.TriageQestionnaireDO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TirageDataBindingUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    private TirageDataBindingUtil() {
        // Sonar lint
    }

    public static String triageQestionnaireDOToJson(TriageQestionnaireDO triageQestionnaireDO)
            throws JsonProcessingException {
        mapper.disable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(triageQestionnaireDO);
    }
}