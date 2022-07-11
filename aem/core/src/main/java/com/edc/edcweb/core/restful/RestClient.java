package com.edc.edcweb.core.restful;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.request.RequestParameter;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.helpers.Constants;

public class RestClient {
    private static final Logger log = LoggerFactory.getLogger(RestClient.class);

    private RestClient() {
        // Sonar lint
    }

    /**
     * Does a GET
     * 
     * @param endPoint      URL to hit with a GET
     * @param authorization including type and encoding for example: Basic
     *                      RURDVGVzdFFBQ1xBRU0uSW50ZWdyYXRpb246QDNtMXBAsssss
     * @param headers       Map<String, String> value pair to be included on headers
     * @param jsonParams    String of JSON to send as parameters
     * @param formParams    "application/x-www-form-urlencoded" value pair sent on
     *                      Query string
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public static JSONObject doGet(String endPoint, String authorization, Map<String, String> headers,
            String jsonParams, Map<String, String> formParams) throws IOException, JSONException {
        return doRequest(RestClientConstants.Methods.METHOD_GET, endPoint, authorization, headers, jsonParams,
                formParams, null);
    }

    /**
     * Does a Post
     * 
     * @param endPoint      URL to hit with a POST
     * @param authorization including type and encoding for example: Basic
     *                      RURDVGVzdFFBQ1xBRU0uSW50ZWdyYXRpb246QDNtMXBAsssss
     * @param headers       Map<String, String> value pair to be included on headers
     * @param jsonParams    String of JSON to send as parameters
     * @param formParams    "application/x-www-form-urlencoded" value pair send on
     *                      quiery string
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public static JSONObject doPost(String endPoint, String authorization, Map<String, String> headers,
            String jsonParams, Map<String, String> formParams) throws IOException, JSONException {
        return doRequest(RestClientConstants.Methods.METHOD_POST, endPoint, authorization, headers, jsonParams,
                formParams, null);
    }

    /**
     * Does a Multipart Post
     * 
     * @param endPoint      URL to hit with a POST
     * @param authorization including type and encoding for example: Basic
     *                      RURDVGVzdFFBQ1xBRU0uSW50ZWdyYXRpb246QDNtMXBAsssss
     * @param headers       Map<String, String> value pair to be included on headers
     * @param formParams    "multipart/form-data" value pair send on post
     * @return
     * @throws IOException
     * @throws JSONException json param is set to a constant "multipart" to
     *                       differentiate processing on lines 151 & 167
     */
    public static JSONObject doMultiPartPost(String endPoint, String authorization, Map<String, String> headers,
            Map<String, String> formParams,  List<RequestParameter> attchmentList) throws IOException, JSONException {
        return doRequest(RestClientConstants.Methods.METHOD_POST, endPoint, authorization, headers,
                RestClientConstants.MULTIPART, formParams, attchmentList);
    }

    /**
     * Does a Put
     * 
     * @param endPoint      URL to hit with a PUT
     * @param authorization including type and encoding for example: Basic
     *                      RURDVGVzdFFBQ1xBRU0uSW50ZWdyYXRpb246QDNtMXBAsssss
     * @param headers       Map<String, String> value pair to be included on headers
     * @param jsonParams    String of JSON to send as parameters
     * @param formParams    "application/x-www-form-urlencoded" value pair sent on
     *                      quiery string
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public static JSONObject doPut(String endPoint, String authorization, Map<String, String> headers,
            String jsonParams, Map<String, String> formParams) throws IOException, JSONException {
        return doRequest(RestClientConstants.Methods.METHOD_PUT, endPoint, authorization, headers, jsonParams,
                formParams, null);
    }

    /**
     * Do the actual request using the predefined method
     * 
     * @param method        Only GET POST PUT defined on the class
     * @param endPoint      URL to hit with a PUT
     * @param authorization including type and encoding for example: Basic
     *                      RURDVGVzdFFBQ1xBRU0uSW50ZWdyYXRpb246QDNtMXBAsssss
     * @param params        String of JSON to send as parameters
     * @return JSONObject of the response
     * @throws IOException
     * @throws JSONException
     */
    private static JSONObject doRequest(String method, String endPoint, String authorization,
            Map<String, String> headers, String json, Map<String, String> formParams, List<RequestParameter> attchmentList)
            throws IOException, JSONException {

        JSONObject response = new JSONObject();

        HttpURLConnection conn = null;
        // add authorization header
        if (StringUtils.isNotBlank(authorization)) {
            conn = createConnection(endPoint, authorization);
            conn.setRequestProperty(RestClientConstants.AUTHORIZATION, authorization);
        } else {
            URL url = new URL(endPoint);
            conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(Constants.CONNECT_TIMEOUT);
			conn.setReadTimeout(Constants.CONNECT_TIMEOUT);
        }
        conn.setRequestMethod(method);

        // add headers as if any
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                conn.setRequestProperty(header.getKey(), header.getValue());
            }
        }

        if (!method.equals(RestClientConstants.Methods.METHOD_GET)
                && (json != null && !RestClientConstants.MULTIPART.equals(json))) {
            conn.setRequestProperty(RestClientConstants.CONTENT_TYPE, RestClientConstants.APPLICATION_JSON);
            conn.setDoOutput(true);
            byte[] postDataBytes = json.getBytes();
            conn.setRequestProperty(RestClientConstants.CHAR_SET_TEXT, RestClientConstants.UTF_8_CHAR_SET);
            conn.setRequestProperty(RestClientConstants.CONTENT_LEGTH, Integer.toString(postDataBytes.length));
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.write(postDataBytes);
            os.flush();
            os.close();
        }

        if (!method.equals(RestClientConstants.Methods.METHOD_GET) && formParams != null) {
            StringBuilder postData = new StringBuilder();
            String contentType = RestClientConstants.APPLICATION_FORM_URLENCODED;
            conn.setDoOutput(true);
            conn.setRequestProperty(RestClientConstants.CHAR_SET_TEXT, RestClientConstants.UTF_8_CHAR_SET);
            if (RestClientConstants.MULTIPART.equals(json)) {
                // multipart/form-data
                String twoHyphens = "--";
                String boundary = RestClientConstants.BOUNDARY_TEXT.concat(Long.toString(System.currentTimeMillis()));
                // Using System.lineSeparator(); does not work, as post expects both \r\n chars
                String lineEnd = "\r\n";
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setRequestProperty(RestClientConstants.USER_AGENT_KEY, RestClientConstants.USER_AGENT_VALUE);
                contentType = RestClientConstants.MULTIPART_FORM_DATA_BOUNDARY.concat(boundary);
                Iterator<String> keys = formParams.keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                    String value = formParams.get(key);
                    postData.append(twoHyphens.concat(boundary).concat(lineEnd));
                    postData.append(RestClientConstants.CONTENT_DISPOSITION.concat(RestClientConstants.COLON_SPACE)
                            .concat(RestClientConstants.MULTIPART_FORM_DATA_NAME).concat(key).concat("\"")
                            .concat(lineEnd));
                    postData.append(RestClientConstants.CONTENT_TYPE.concat(RestClientConstants.COLON_SPACE)
                            .concat(RestClientConstants.TEXT_PLAIN_CHARCTER_SET)
                            .concat(RestClientConstants.UTF_8_CHAR_SET).concat(lineEnd));
                    postData.append(lineEnd);
                    postData.append(value);
                    postData.append(lineEnd);
                }
                // Attachments
                if (attchmentList != null && !attchmentList.isEmpty()) {
                    for (RequestParameter param : attchmentList) {
                        if(StringUtils.isNotBlank(param.getFileName())) {
                        postData.append(twoHyphens.concat(boundary).concat(lineEnd));
                        postData.append(RestClientConstants.CONTENT_DISPOSITION.concat(RestClientConstants.COLON_SPACE)
                                .concat(RestClientConstants.MULTIPART_FORM_DATA_NAME).concat(param.getName())
                                .concat(RestClientConstants.MULTIPART_FORM_DATA_FILE_NAME).concat(param.getFileName()).concat("\"")
                                .concat(lineEnd));
                        postData.append(RestClientConstants.CONTENT_TYPE.concat(RestClientConstants.COLON_SPACE)
                                .concat(RestClientConstants.TEXT_PLAIN_CHARCTER_SET)
                                .concat(RestClientConstants.UTF_8_CHAR_SET).concat(lineEnd));
                        postData.append(lineEnd);
                        postData.append(param.get());
                        postData.append(lineEnd);
                        }
                    }
                }
                // training twoHyphens just on last line
                postData.append(twoHyphens.concat(boundary).concat(twoHyphens));
                log.info("Multipart PostData: {}", postData);
            } else {
                // application/x-www-form-urlencoded
                for (Map.Entry<String, String> param : formParams.entrySet()) {
                    if (postData.length() != 0) {
                        postData.append('&');
                    }
                    postData.append(URLEncoder.encode(param.getKey(), RestClientConstants.UTF_8_CHAR_SET));
                    postData.append('=');
                    postData.append(
                            URLEncoder.encode(String.valueOf(param.getValue()), RestClientConstants.UTF_8_CHAR_SET));
                }
            }
            conn.setRequestProperty(RestClientConstants.CONTENT_TYPE, contentType);

            byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);
            String contentLength = Integer.toString(postDataBytes.length);
            conn.setRequestProperty(RestClientConstants.CONTENT_LEGTH, contentLength);
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.write(postDataBytes);
            os.flush();
            os.close();
        }
        // Check for valid response
        int[] validRespnses = { 200, 201 };
        int responseCode = conn.getResponseCode();
        if (!ArrayUtils.contains(validRespnses, responseCode)) {
            response.put(RestClientConstants.ERROR_MSG, responseCode);
            String responseMessage = conn.getResponseMessage();
            response.put(RestClientConstants.ERROR_TXT, responseMessage);
            log.error("RestClient submit error code: {} message: {}", responseCode, responseMessage);
        } else {
            InputStream responseStream = conn.getInputStream();
            try {
                // Moved convertStreamToJSONObject to helper class
                response = RestClientUtils.convertStreamToJSONObject(responseStream);
            } finally {
                responseStream.close();
            }
        }
        conn.disconnect();
        return response;
    }

    /**
     * Helper to create the connection
     * 
     * @param endPoint
     * @param authorization
     * @return the HttpURLConnection object
     * @throws IOException
     */
    private static HttpURLConnection createConnection(String endPoint, String authorization) throws IOException {
        URL url = new URL(endPoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(Constants.CONNECT_TIMEOUT);
        conn.setReadTimeout(Constants.CONNECT_TIMEOUT);
        conn.setRequestProperty("Authorization", authorization);
        return conn;
    }

    /**
     * Simple HTTP POST, no authorization, returns error code
     * 
     * @param endPoint
     * @param formParams
     * @return Integer with Calls Result code, 500 if no error can be retrieved
     * @throws IOException
     */
    public static int doSimpleHTTPPost(String endPoint, Map<String, String> formParams) {

        int response = RestClientConstants.ERROR_STATUS;
        HttpURLConnection conn = null;
        StringBuilder postData = new StringBuilder();

        try {
            URL url = new URL(endPoint);
            conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(Constants.CONNECT_TIMEOUT);
			conn.setReadTimeout(Constants.CONNECT_TIMEOUT);
            conn.setRequestMethod(RestClientConstants.Methods.METHOD_POST);
            conn.setRequestProperty(RestClientConstants.USER_AGENT_KEY, RestClientConstants.USER_AGENT_VALUE);
            conn.setRequestProperty(RestClientConstants.CONTENT_TYPE, RestClientConstants.APPLICATION_FORM_URLENCODED);
            conn.setRequestProperty(RestClientConstants.CHAR_SET_TEXT, RestClientConstants.UTF_8_CHAR_SET);
            conn.setDoOutput(true);

            for (Map.Entry<String, String> param : formParams.entrySet()) {
                if (postData.length() != 0) {
                    postData.append('&');
                }
                postData.append(URLEncoder.encode(param.getKey(), RestClientConstants.UTF_8_CHAR_SET));
                postData.append('=');
                postData.append(
                        URLEncoder.encode(String.valueOf(param.getValue()), RestClientConstants.UTF_8_CHAR_SET));
            }

            byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);
            String contentLength = Integer.toString(postDataBytes.length);
            conn.setRequestProperty(RestClientConstants.CONTENT_LEGTH, contentLength);
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.write(postDataBytes);
            os.flush();
            os.close();

            response = conn.getResponseCode();

        } catch (IOException | IllegalArgumentException | SecurityException | IllegalStateException | NullPointerException e) {
            log.error("RestClient simpleHTTPPost message: {}", e.getStackTrace());
        }

        return response;
    }

}