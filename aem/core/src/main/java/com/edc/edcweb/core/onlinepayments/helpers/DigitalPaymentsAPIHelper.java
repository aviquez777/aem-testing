package com.edc.edcweb.core.onlinepayments.helpers;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.onlinepayments.OPJsonDataBindingUtil;
import com.edc.edcweb.core.onlinepayments.pojo.digitalpayments.DPRequest;
import com.edc.edcweb.core.restful.RestClientV2;
import com.fasterxml.jackson.core.JsonProcessingException;

public class DigitalPaymentsAPIHelper {
    
    private static final Logger log = LoggerFactory.getLogger(DigitalPaymentsAPIHelper.class);
    
    private DigitalPaymentsAPIHelper () {
        // SonarLint
    }
    
    public static String postDigtalPayment(String apiUrl, DPRequest dpRequest, Map<String, String> headers) {
        String dpRequestJson = null;
        String dpResponseJson = null;
        try {
            dpRequestJson =  OPJsonDataBindingUtil.pojoToJson(dpRequest);
        } catch (JsonProcessingException e) {
            log.error("DigitalPaymentsAPIHelper postDigtalPayment failed processing Json {}", dpRequestJson, e);
        }
        if (StringUtils.isNotBlank(dpRequestJson)) {
            // Call Rest Client
            dpResponseJson = RestClientV2.doJsonPost(apiUrl, dpRequestJson, headers, true);
        }
        return dpResponseJson;
    }
}
