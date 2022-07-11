package com.edc.edcweb.core.onlinepayments;

import com.edc.edcweb.core.onlinepayments.pojo.moneris.PreloadResponse;
import com.edc.edcweb.core.onlinepayments.pojo.moneris.ReceiptResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OPJsonDataBindingUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    private OPJsonDataBindingUtil() {
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

    /**
     * Convert Moneris PreloadResponse JSON to PreloadResponse object
     * 
     * @param PreloadResponse JSON
     * @return PreloadResponse
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    public static PreloadResponse jsonToPreloadResponse(String json) throws JsonProcessingException {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper.readValue(json, PreloadResponse.class);
    }

    /**
     * Convert Moneris ReceiptResponse JSON to ReceiptResponse object
     * 
     * @param ReceiptResponse JSON
     * @return ReceiptResponse
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    public static ReceiptResponse jsonToReceiptResponse(String json) throws JsonProcessingException {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper.readValue(json, ReceiptResponse.class);
    }

}
