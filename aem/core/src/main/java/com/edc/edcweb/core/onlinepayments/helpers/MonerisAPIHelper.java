package com.edc.edcweb.core.onlinepayments.helpers;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.onlinepayments.OPJsonDataBindingUtil;
import com.edc.edcweb.core.onlinepayments.pojo.moneris.PreloadRequest;
import com.edc.edcweb.core.onlinepayments.pojo.moneris.PreloadResponse;
import com.edc.edcweb.core.onlinepayments.pojo.moneris.ReceiptRequest;
import com.edc.edcweb.core.onlinepayments.pojo.moneris.ReceiptResponse;
import com.edc.edcweb.core.restful.RestClientV2;
import com.fasterxml.jackson.core.JsonProcessingException;

public class MonerisAPIHelper {

    private static final Logger log = LoggerFactory.getLogger(MonerisAPIHelper.class);

    private MonerisAPIHelper() {
        // SonarLint
    }

    /**
     * getPreload Gets the PreloadRequest object and returns the PreloadResponse
     * @param monerisUrl
     * @param preloadRequest
     * @return PreloadResponse
     */
    public static PreloadResponse getPreload(String monerisUrl, PreloadRequest preloadRequest) {
        String preloadJson = getJsonFromMoneris(monerisUrl, preloadRequest);
        PreloadResponse preloadResponse = new PreloadResponse();
        if (StringUtils.isNotBlank(preloadJson)) {
            try {
                preloadResponse = OPJsonDataBindingUtil.jsonToPreloadResponse(preloadJson);
            } catch (JsonProcessingException e) {
                log.error("MonerisAPIHelper getReceipt failed processing Json", e);
            }
        }
        return preloadResponse;
    }

    /**
     * getReceipt Gets the ReceiptRequest object and returns the ReceiptResponse
     * @param monerisUrl
     * @param receiptRequest
     * @return ReceiptResponse
     */
    public static ReceiptResponse getReceipt(String monerisUrl, ReceiptRequest receiptRequest) {
        String receiptJson = getJsonFromMoneris(monerisUrl, receiptRequest);
        ReceiptResponse receiptResponse = new ReceiptResponse();
        if (StringUtils.isNotBlank(receiptJson)) {
            try {
                receiptResponse = OPJsonDataBindingUtil.jsonToReceiptResponse(receiptJson);
            } catch (JsonProcessingException e) {
                log.error("MonerisAPIHelper getReceipt failed processing Json {}", receiptJson,  e);
            }
        }
        return receiptResponse;
    }

    /**
     *  getJsonFromMoneris Common Method to Get "Raw" Json from Moneris
     * @param monerisUrl
     * @param request
     * @return String "Raw" Json from Moneris
     */
    private static String getJsonFromMoneris(String monerisUrl, Object request) {
        String requestJson = null;
        String responseJson = null;
        try {
            requestJson = OPJsonDataBindingUtil.pojoToJson(request);
        } catch (JsonProcessingException e) {
            log.error("MonerisAPIHelper getJsonFromMoneris failed processing Json {}", request, e);
        }
        if (StringUtils.isNotBlank(requestJson)) {
            // Call Rest Client
            responseJson = RestClientV2.doJsonPost(monerisUrl, requestJson, null, true);
        }
        return responseJson;
    }

}
