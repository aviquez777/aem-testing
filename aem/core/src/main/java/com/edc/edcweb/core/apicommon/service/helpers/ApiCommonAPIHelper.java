package com.edc.edcweb.core.apicommon.service.helpers;

import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.apicommon.ApiCommonJsonDataBindingUtil;
import com.edc.edcweb.core.apicommon.pojo.APICommonToken;
import com.edc.edcweb.core.restful.RestClientV2;
import com.fasterxml.jackson.core.JsonProcessingException;

public class ApiCommonAPIHelper {

    private static final Logger log = LoggerFactory.getLogger(ApiCommonAPIHelper.class);

    private ApiCommonAPIHelper() {
        // Sonar Lint
    }

    public static APICommonToken postTokenRequest(String apiUrl, Map<String, String> headers,
            List<NameValuePair> formParams) {
        APICommonToken apimToken = new APICommonToken();
        String jsonToken = RestClientV2.doFormPost(apiUrl, headers, formParams, false);
        try {
            apimToken = ApiCommonJsonDataBindingUtil.jsonToApimToken(jsonToken);
        } catch (JsonProcessingException e) {
            log.error("ApiCommonAPIHelper postTokenRequest failed processing Json {}", apimToken, e);
        }
        return apimToken;
    }

}
