package com.edc.edcweb.core.restful.calls;

import com.edc.edcweb.core.restful.utils.RestClientV2Utils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class RestGetV2 {

    private static final Logger log = LoggerFactory.getLogger(RestGetV2.class);

    private RestGetV2() {
        // Sonar Lint
    }

    /**
     * Do the actual request using the predefined method
     * 
     * @param endPoint      URL to hit with a PUT
     * @param headers including type and encoding for example: Basic
     *                      RURDVGVzdFFBQ1xBRU0uSW50ZWdyYXRpb246QDNtMXBAsssss
     * @param debug        String of JSON to send as parameters
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
