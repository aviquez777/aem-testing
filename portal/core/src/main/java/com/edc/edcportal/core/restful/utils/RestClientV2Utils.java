package com.edc.edcportal.core.restful.utils;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.Header;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.restful.RestClientConstants;

public class RestClientV2Utils {

    private static final Logger log = LoggerFactory.getLogger(RestClientV2Utils.class);

    private RestClientV2Utils() {
        // Sonar LInt
    }

    /**
     * createHttpClient Create CloseableHttpClient with recommended timeouts
     * 
     * @return CloseableHttpClient
     */
    public static CloseableHttpClient createHttpClient() {
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(Constants.CONNECT_TIMEOUT)
                .setConnectionRequestTimeout(Constants.CONNECT_TIMEOUT).setSocketTimeout(Constants.CONNECT_TIMEOUT)
                .build();
        return HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
    }

    /**
     * addCustomHeaders and Authorization headers to a post Request
     * 
     * @param httpPost
     * @param authorization
     * @param headers
     */
    public static HttpPost addCustomHeadersToPost(HttpPost httpPost, Map<String, String> headers) {
        // Always add user agent
        httpPost.setHeader(RestClientConstants.USER_AGENT_KEY, RestClientConstants.USER_AGENT_VALUE);
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }
        return httpPost;
    }

    /**
     * addCustomHeaders and Authorization headers to a Get request
     * 
     * @param httpGet
     * @param authorization
     * @param headers
     */
    public static HttpGet addCustomHeadersToGet(HttpGet httpGet, Map<String, String> headers) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpGet.setHeader(entry.getKey(), entry.getValue());
        }
        return httpGet;
    }

    /**
     * addCustomHeaders and Authorization headers to a put Request
     * 
     * @param httpPut
     * @param authorization
     * @param headers
     */
    public static HttpPut addCustomHeadersToPut(HttpPut httpPut, Map<String, String> headers) {
        // Always add user agent
        httpPut.setHeader(RestClientConstants.USER_AGENT_KEY, RestClientConstants.USER_AGENT_VALUE);
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPut.setHeader(entry.getKey(), entry.getValue());
            }
        }
        return httpPut;
    }

    /**
     * getGetStringResponse try to parse the client and return an string response
     * 
     * @param client
     * @param httpGet
     * @return string response (includes any error)
     */
    public static String getGetStringResponse(CloseableHttpClient client, HttpGet httpGet) {
        String responseStr = null;
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = client.execute(httpGet);
            responseStr = checkValidResponse(httpResponse);
            httpResponse.close();
        } catch (IOException | NullPointerException e) {
            log.error("RestClientV2Utils getGetStringResponse error executing full error ", e);
        }
        try {
            client.close();
        } catch (IOException e) {
            log.error("RestClientV2Utils getGetStringResponse error closing resource ", e);
        }
        return responseStr;
    }

    /**
     * getPostStringResponse try to parse the client and return an string response
     * 
     * @param client
     * @param httpPost
     * @return string response (includes any error)
     */
    public static String getPostStringResponse(CloseableHttpClient client, HttpPost httpPost) {
        String responseStr = null;
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = client.execute(httpPost);
            responseStr = checkValidResponse(httpResponse);
            httpResponse.close();
        } catch (IOException | NullPointerException e) {
            log.error("RestClientV2Utils getPostStringResponse error exectuting full error ", e);
        }
        try {
            client.close();
        } catch (IOException e) {
            log.error("RestClientV2Utils getPostStringResponse error closing resource ", e);
        }
        return responseStr;
    }

    /**
     * getRawPostedData
     * 
     * @param httpPost
     * @param entity
     * @return RawPostedData
     */
    public static String getRawPostedData(HttpPost httpPost, StringEntity entity) {
        StringBuilder postedData = new StringBuilder();
        Header[] headers = httpPost.getAllHeaders();
        String content = null;
        try {
            content = EntityUtils.toString(entity);
        } catch (ParseException | IOException e) {
            log.error("RestClientV2Utils getRawPostedData invalid content ", e);
        }
        postedData.append("Post:").append(httpPost.toString()).append(System.lineSeparator());
        postedData.append("Headers:").append(System.lineSeparator());
        for (Header header : headers) {
            postedData.append(header.getName()).append(":").append(header.getValue()).append(System.lineSeparator());
        }
        postedData.append("Content:").append(System.lineSeparator());
        postedData.append(content);
        return postedData.toString();
    }

    /**
     * getPutStringResponse try to parse the client and return an string response
     * 
     * @param client
     * @param httpPut
     * @return string response (includes any error)
     */
    public static String getPutStringResponse(CloseableHttpClient client, HttpPut httpPut) {
        String responseStr = null;
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = client.execute(httpPut);
            responseStr = checkValidResponse(httpResponse);
            httpResponse.close();
        } catch (IOException | NullPointerException e) {
            log.error("RestClientV2Utils getPutStringResponse error exectuting full error ", e);
        }
        try {
            client.close();
        } catch (IOException e) {
            log.error("RestClientV2Utils getPutStringResponse error closing resource ", e);
        }
        return responseStr;
    }

    /**
     * getRawPutData
     * 
     * @param httpPut
     * @param entity
     * @return RawPutData
     */
    public static String getRawPutData(HttpPut httpPut, StringEntity entity) {
        StringBuilder postedData = new StringBuilder();
        Header[] headers = httpPut.getAllHeaders();
        String content = null;
        try {
            content = EntityUtils.toString(entity);
        } catch (ParseException | IOException e) {
            log.error("RestClientV2Utils getRawPutData invalid content ", e);
        }
        postedData.append("Put:").append(httpPut.toString()).append(System.lineSeparator());
        postedData.append("Headers:").append(System.lineSeparator());
        for (Header header : headers) {
            postedData.append(header.getName()).append(":").append(header.getValue()).append(System.lineSeparator());
        }
        postedData.append("Content:").append(System.lineSeparator());
        postedData.append(content);
        return postedData.toString();
    }

    /**
     * checkValidResponse check response append errors if needed
     * 
     * @param client
     * @param responseStr
     * @param responseStrBuilder
     * @param httpResponse
     */
    private static String checkValidResponse(CloseableHttpResponse httpResponse)
            throws IOException, NullPointerException {
        // Check for valid response
        String responseStr = null;
        StringBuilder responseStrBuilder = new StringBuilder();
        int[] validRespnses = { 200, 201 };
        int responseCode = httpResponse.getStatusLine().getStatusCode();
        if (!ArrayUtils.contains(validRespnses, responseCode)) {
            responseStrBuilder.append(RestClientConstants.OPEN_BRACKET);
            responseStrBuilder.append(RestClientConstants.QUOTE).append(RestClientConstants.STATUS_CODE)
                    .append(RestClientConstants.QUOTE);
            responseStrBuilder.append(RestClientConstants.COLON_SPACE);
            responseStrBuilder.append(responseCode);
            responseStrBuilder.append(RestClientConstants.SPACE).append(RestClientConstants.COMMA).append(RestClientConstants.SPACE);
            responseStrBuilder.append(RestClientConstants.QUOTE).append(RestClientConstants.ERROR_MSG)
                    .append(RestClientConstants.QUOTE);
            responseStrBuilder.append(RestClientConstants.COLON_SPACE);
            responseStrBuilder.append(RestClientConstants.QUOTE).append(httpResponse.getStatusLine().getReasonPhrase())
                    .append(RestClientConstants.QUOTE);
            responseStrBuilder.append(RestClientConstants.CLOSE_BRACKET);
            responseStr = responseStrBuilder.toString();
            log.error("RestClientV2Utils checkValidResponse error code: {} message: {}", responseCode, responseStr);
        } else {
            responseStr = EntityUtils.toString(httpResponse.getEntity());
            log.debug("RestClientV2Utils checkValidResponse status code {}", httpResponse.getStatusLine());
        }
        return responseStr;
    }

}
