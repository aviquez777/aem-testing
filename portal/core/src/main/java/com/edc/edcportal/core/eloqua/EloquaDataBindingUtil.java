package com.edc.edcportal.core.eloqua;

import com.edc.edcportal.core.eloqua.pojo.EloquaCDO;
import com.edc.edcportal.core.eloqua.pojo.EloquaEndPoint;
import com.edc.edcportal.core.eloqua.pojo.EloquaRecordDO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EloquaDataBindingUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    private EloquaDataBindingUtil() {
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

    public static EloquaEndPoint jsonToEloquaEndPoint(String json) throws JsonProcessingException {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper.readValue(json, EloquaEndPoint.class);
    }

    public static EloquaCDO jsonToEloquaCDO(String json) throws JsonProcessingException {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper.readValue(json, EloquaCDO.class);
    }

    public static EloquaRecordDO jsonToEloquaRecordDO(String json) throws JsonProcessingException {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper.readValue(json, EloquaRecordDO.class);
    }

}
