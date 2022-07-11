package com.edc.edcportal.core.restful.calls;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.restful.RestClientConstants;
import com.edc.edcportal.core.restful.utils.RestClientV2Utils;

public class RestPut {

    private static final Logger log = LoggerFactory.getLogger(RestPut.class);

    private RestPut() {
        // Sonar Lint
    }

    public static String jsonPut(String endPoint, Map<String, String> headers, String json, boolean debug) {
        CloseableHttpClient client = RestClientV2Utils.createHttpClient();

        HttpPut httpPut = new HttpPut(endPoint);
        // Add any custom headers, authorization should be part of the headers
        httpPut = RestClientV2Utils.addCustomHeadersToPut(httpPut, headers);

        StringEntity entity = new StringEntity(json, RestClientConstants.UTF_8_CHAR_SET);
        entity.setContentType(new BasicHeader(RestClientConstants.CONTENT_TYPE,
                    RestClientConstants.APPLICATION_JSON_CHARACTER_SET.concat(RestClientConstants.UTF_8_CHAR_SET)));
        httpPut.setEntity(entity);
        if (debug) {
            log.debug("RestPut jsonPut posting to {} with json {}", endPoint, json);
            log.debug("RestPut jsonPut Raw Data {}", RestClientV2Utils.getRawPutData(httpPut, entity));
        }
        return RestClientV2Utils.getPutStringResponse(client, httpPut);
    }
}
