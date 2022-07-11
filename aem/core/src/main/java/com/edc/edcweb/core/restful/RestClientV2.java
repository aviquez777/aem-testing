package com.edc.edcweb.core.restful;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.edc.edcweb.core.restful.calls.RestGetV2;
import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.restful.calls.RestPostV2;

public class RestClientV2 {

    private static final Logger log = LoggerFactory.getLogger(RestClientV2.class);

    private RestClientV2() {
        // Sonar Lint
    }

    /**
     * do Json Get
     * @param endPoint
     * @param headers
     * @param debug
     * @return Json String
     */
    public static String doJsonGet(String endPoint, Map<String, String> headers, boolean debug) {
        return RestGetV2.jsonGet(endPoint, headers, debug);
    }

    public static String doJsonPost(String endPoint, String json, Map<String, String> headers, boolean debug) {
        if (headers == null || headers.size() == 0) {
            headers = new HashMap<>();
        }
        // Add JSON Specific headers
        headers.put(RestClientConstants.ACCEPT, RestClientConstants.APPLICATION_JSON);
        headers.put(RestClientConstants.CONTENT_TYPE, RestClientConstants.APPLICATION_JSON_CHARACTER_SET.concat(RestClientConstants.UTF_8_CHAR_SET));
        // Do the RestPost
        return RestPostV2.jsonPost(endPoint, headers, json, debug);
    }

    public static String doFormPost(String endPoint, Map<String, String> headers, List<NameValuePair> formParams, boolean debug) {
        String responseJson = null;
        try {
            responseJson = RestPostV2.urlencodedFormPost(endPoint, headers, formParams, debug);
        } catch (UnsupportedEncodingException e) {
            log.error("ApiCommonAPIHelper doFormPost failed posting form ", e);
        }
        return responseJson;
    }
}
