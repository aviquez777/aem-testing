package com.edc.edcportal.core.transactionhistory;

import com.edc.edcportal.core.transactionhistory.pojo.THRecordDO;
import com.edc.edcportal.core.transactionhistory.pojo.THSearchResults;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class THJsonDataBindingUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    private THJsonDataBindingUtil() {

    }

    public static String thRecordDOToJson(THRecordDO threcord) throws JsonProcessingException {
        mapper.disable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES);
        return mapper.writeValueAsString(threcord);
    }

    public static THSearchResults jsonToTHSearchResults(String jsonString) throws JsonProcessingException {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper.readValue(jsonString, THSearchResults.class);
    }

}