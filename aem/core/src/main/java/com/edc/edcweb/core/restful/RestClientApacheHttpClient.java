package com.edc.edcweb.core.restful;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.sling.api.request.RequestParameter;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.helpers.Constants;

public class RestClientApacheHttpClient {

    private static final Logger log = LoggerFactory.getLogger(RestClientApacheHttpClient.class);

    private RestClientApacheHttpClient() {
        // Sonar Lint
    }

    public static JSONObject doMultipartPost(String endPoint, String authorization, Map<String, String> headers,
            Map<String, String> formParams, List<RequestParameter> attchmentList) throws IOException, JSONException {
        // Response JSON Backwards compatibility.
        JSONObject response = new JSONObject();
        RequestConfig requestConfig = RequestConfig.custom()
          .setConnectTimeout(Constants.CONNECT_TIMEOUT)
          .setSocketTimeout(Constants.CONNECT_TIMEOUT)
          .build();
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
        MultipartEntityBuilder multipartBuilder = MultipartEntityBuilder.create();
        multipartBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        // Iterate over regular fields
        Iterator<String> keys = formParams.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = formParams.get(key);
            multipartBuilder.addTextBody(key, value, ContentType.create("text/plain", StandardCharsets.UTF_8));
        }
        // Iterate over attachments
        if (attchmentList != null && !attchmentList.isEmpty()) {
            for (RequestParameter param : attchmentList) {
                if (StringUtils.isNotBlank(param.getFileName())) {
                    ContentType contentType = ContentType.parse(param.getContentType());
                    multipartBuilder.addBinaryBody(param.getName(), param.get(), contentType,
                            param.getFileName());
                }
            }
        }
        // Building a single entity using the parts
        HttpEntity mutiPartHttpEntity = multipartBuilder.build();
        // Building the RequestBuilder request object
        RequestBuilder reqbuilder = RequestBuilder.post(endPoint);
        // Set the entity object to the RequestBuilder
        reqbuilder.setEntity(mutiPartHttpEntity);
        // Building the request
        HttpUriRequest multipartRequest = reqbuilder.build();
        // add authorization header
        if (StringUtils.isNotBlank(authorization)) {
            multipartRequest.addHeader(RestClientConstants.AUTHORIZATION, authorization);
        }
        // add headers as if any
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                multipartRequest.addHeader(header.getKey(), header.getValue());
            }
        }
        // Executing the request
        CloseableHttpResponse httpResponse = httpclient.execute(multipartRequest);
        HttpEntity entity = httpResponse.getEntity();
        // Check for valid response
        int[] validRespnses = { 200, 201 };
        int responseCode = httpResponse.getStatusLine().getStatusCode();
        if (!ArrayUtils.contains(validRespnses, responseCode)) {
            response.put(RestClientConstants.ERROR_MSG, responseCode);
            String responseMessage = httpResponse.getStatusLine().getReasonPhrase();
            response.put(RestClientConstants.ERROR_TXT, responseMessage);
            log.error("RestClientApacheHttpClient submit error code: {} message: {}", responseCode, responseMessage);
        } else {
            try {
                log.debug("RestClientApacheHttpClient doMultipartPost status code {}", httpResponse.getStatusLine());
                response = RestClientUtils.convertStreamToJSONObject(entity.getContent());
            } finally {
                httpResponse.close();
            }
        }
        httpclient.close();
        ByteArrayOutputStream raw = new ByteArrayOutputStream();
        mutiPartHttpEntity.writeTo(raw);
        log.debug("RestClientApacheHttpClient Multipart PostData: {} ", raw);
        return response;
    }
}
