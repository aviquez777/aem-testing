package com.edc.edcportal.core.restful.calls;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.restful.utils.RestClientV2Utils;

public class RestGetV2 {

    private static final Logger log = LoggerFactory.getLogger(RestGetV2.class);

    private RestGetV2() {
        // Sonar Lint
    }

    /**
     * Do the actual request using the predefined method
     * 
     * @param endPoint      URL to hit with a PUT
     * @param authorization including type and encoding for example: Basic
     *                      RURDVGVzdFFBQ1xBRU0uSW50ZWdyYXRpb246QDNtMXBAsssss
     * @param params        String of JSON to send as parameters
     * @return String of the response
     * @throws IOException
     */
    public static String jsonGet(String endPoint, Map<String, String> headers, boolean debug) {
        CloseableHttpClient client = RestClientV2Utils.createHttpClient();

        HttpGet httpGet = new HttpGet(endPoint);
        // Add any custom headers, authorization should be part of the headers
        httpGet = RestClientV2Utils.addCustomHeadersToGet(httpGet, headers);
        if (debug) {
            log.debug("RestGet jsonGet get request to {}", endPoint);
        }
        return RestClientV2Utils.getGetStringResponse(client, httpGet);
    }
}
