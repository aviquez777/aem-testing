package com.edc.edcweb.core.restful.calls;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.restful.RestClientConstants;
import com.edc.edcweb.core.restful.utils.RestClientV2Utils;

public class RestPostV2 {

    private static final Logger log = LoggerFactory.getLogger(RestPostV2.class);

    private RestPostV2() {
        // Sonar Lint
    }

    public static String jsonPost(String endPoint, Map<String, String> headers, String json, boolean debug) {
        CloseableHttpClient client = RestClientV2Utils.createHttpClient();

        HttpPost httpPost = new HttpPost(endPoint);
        // Add any custom headers, authorization should be part of the headers
        httpPost = RestClientV2Utils.addCustomHeadersToPost(httpPost, headers);

        StringEntity entity = new StringEntity(json, RestClientConstants.UTF_8_CHAR_SET);
        entity.setContentType(new BasicHeader(RestClientConstants.CONTENT_TYPE,
                    RestClientConstants.APPLICATION_JSON_CHARACTER_SET.concat(RestClientConstants.UTF_8_CHAR_SET)));
        httpPost.setEntity(entity);
        if (debug) {
            log.debug("RestPost jsonPost posting to {} with json {}", endPoint, json);
            log.debug("RestPost jsonPost Raw Data {}", RestClientV2Utils.getRawPostedData(httpPost, entity));
        }
        return RestClientV2Utils.getPostStringResponse(client, httpPost);
    }

    public static String urlencodedFormPost(String endPoint, Map<String, String> headers,
            List<NameValuePair> formParams, boolean debug) throws UnsupportedEncodingException {
        if (debug) {
            log.debug("RestPost jsonPost posting to {} with formParams {}", endPoint, formParams);
        }
        //This resource is properly closed on RestClientV2Utils.getPostStringResponse
        CloseableHttpClient client = RestClientV2Utils.createHttpClient(); // NOSONAR

        HttpPost httpPost = new HttpPost(endPoint);
        // Add any custom headers, authorization should be part of the headers
        httpPost = RestClientV2Utils.addCustomHeadersToPost(httpPost, headers);
        StringEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(formParams);
        } catch (UnsupportedEncodingException e) {
            log.error("RestClientV2 formPost invalid JSON: {}", formParams, e);
        }
        httpPost.setEntity(entity);

        return RestClientV2Utils.getPostStringResponse(client, httpPost);
    }
}
