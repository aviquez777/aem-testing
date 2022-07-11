package com.edc.edcportal.core.restful;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestClient {
    private static final Logger log = LoggerFactory.getLogger(RestClient.class);

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
                formParams);
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
                formParams);
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
            Map<String, String> formParams) throws IOException, JSONException {
        return doRequest(RestClientConstants.Methods.METHOD_POST, endPoint, authorization, headers,
                RestClientConstants.MULTIPART, formParams);
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
                formParams);
    }

    /**
     * Does a get But Returns a JSONArray Array instead of a JSONObject
     * 
     * @param endPoint
     * @param authorization
     * @param headers
     * @param jsonParams
     * @param formParams
     * @return
     * @throws IOException
     * @throws JSONException
     */

    public static JSONArray doGetArray(String endPoint, String authorization, Map<String, String> headers,
            String jsonParams, Map<String, String> formParams) throws IOException, JSONException {
        return doRequestArray(RestClientConstants.Methods.METHOD_GET, endPoint, authorization, headers, jsonParams,
                formParams);
    }

    /**
     * Does a request returning a JSONArray
     * 
     * @param method
     * @param endPoint
     * @param authorization
     * @param headers
     * @param json
     * @param formParams
     * @return
     * @throws IOException
     * @throws JSONException
     */
    private static JSONArray doRequestArray(String method, String endPoint, String authorization,
            Map<String, String> headers, String json, Map<String, String> formParams)
            throws IOException, JSONException {
        String response = sendRequest(method, endPoint, authorization, headers, json, formParams);
        return new JSONArray(response);
    }

    /**
     * Does a request returning a JSONObject
     * 
     * @param method
     * @param endPoint
     * @param authorization
     * @param headers
     * @param json
     * @param formParams
     * @return
     * @throws IOException
     * @throws JSONException
     */
    private static JSONObject doRequest(String method, String endPoint, String authorization,
            Map<String, String> headers, String json, Map<String, String> formParams)
            throws IOException, JSONException {
        String response = sendRequest(method, endPoint, authorization, headers, json, formParams);
        return new JSONObject(response);
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
    private static String sendRequest(String method, String endPoint, String authorization, Map<String, String> headers,
            String json, Map<String, String> formParams) throws IOException, JSONException {

        String response = "";

        HttpURLConnection conn = null;
        // add authorization header
        if (StringUtils.isNotBlank(authorization)) {
            conn = createConnection(endPoint, authorization);
            conn.setRequestProperty(RestClientConstants.AUTHORIZATION, authorization);
        } else {
            URL url = new URL(endPoint);
            conn = (HttpURLConnection) url.openConnection();
        }
        conn.setRequestMethod(method);
        // Task 221435 CQRules:ConnectionTimeoutMechanism
        conn.setConnectTimeout(RestClientConstants.CONNECT_TIMEOUT);
        conn.setReadTimeout(RestClientConstants.READ_TIMEOUT);

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
            byte[] postDataBytes = json.getBytes(StandardCharsets.UTF_8);
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
                // training twoHyphens just on last line
                postData.append(twoHyphens.concat(boundary).concat(twoHyphens));
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
            response = response.concat("{");
            response = response.concat("\"").concat(RestClientConstants.ERROR_MSG).concat("\":")
                    .concat(Integer.toString(responseCode));
            String responseMessage = conn.getResponseMessage();
            response = response.concat("\"").concat(RestClientConstants.ERROR_TXT).concat("\":").concat("\"")
                    .concat(responseMessage).concat("\"");
            response = response.concat("}");
            log.error("RestClient submit error code: {} message: {}", responseCode, responseMessage);
            // Handled the error code and throw the JsonExcetion so it is catched on the
            // service, and redirect to the system error page.
            // For better UI / UX we can create a small on page error message to "please try
            // again"
            throw new JSONException(responseMessage);
        } else {
            InputStream responseStream = conn.getInputStream();
            try {
                response = convertStreamToString(responseStream);
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
    protected static HttpURLConnection createConnection(String endPoint, String authorization) throws IOException {
        URL url = new URL(endPoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", authorization);
        // Task 221435 CQRules:ConnectionTimeoutMechanism
        conn.setConnectTimeout(RestClientConstants.CONNECT_TIMEOUT);
        conn.setReadTimeout(RestClientConstants.READ_TIMEOUT);
        return conn;
    }

    /**
     * Helper to parse and convert the response into a JSONObject
     * 
     * @param is InputStream
     * @return JSONObject of the response
     * @throws JSONException
     * @throws IOException
     */
    protected static String convertStreamToString(InputStream is) throws JSONException, IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();

        String line = null;

        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        String jsonString = sb.toString();
        return jsonString;
    }
}