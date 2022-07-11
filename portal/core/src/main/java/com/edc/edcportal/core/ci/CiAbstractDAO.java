package com.edc.edcportal.core.ci;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.edc.edcportal.core.ci.services.CiConfigService;
import com.edc.edcportal.core.helpers.Cache;
import com.edc.edcportal.core.helpers.CacheManager;
import com.edc.edcportal.core.restful.RestClient;
import com.edc.edcportal.core.restful.RestClientConstants;

/**
 * Abstract class to provide the get token service
 *
 */
public abstract class CiAbstractDAO {

    public enum CacheObjects {

        CI_ENDPOINT("ci_endpoint"), CI_TOKEN("ci_token");

        private String objectName;

        private CacheObjects(String objectName) {
            this.objectName = objectName;
        }

        public String getObjectName() {
            return objectName;
        }
    }

    /**
     * 
     * Token Form Parameter Values
     *
     */
    private enum TokenFormParams {

        CLIENT_ID_KEY("client_id"), CLIENT_SECRET_KEY("client_secret"), GRANT_TYPE_KEY("grant_type"),
        RESOURCE_KEY("resource");

        private String tokenValue;

        TokenFormParams(String tokenValue) {
            this.tokenValue = tokenValue;
        }

        public String getTokenValue() {
            return this.tokenValue;
        }
    }

    /**
     * 
     * Json Key Names
     *
     */
    private enum JsonKeys {

        ACCESS_TOKEN("access_token"), ACCESS_TOKEN_EXPIRES("expires_in");

        private String key;

        JsonKeys(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

    }

    /**
     * Get Bearer Token from API
     * 
     * @param CiConfigService get the proper values
     * @return Json As String
     * @throws JSONException
     * @throws IOException
     */
    public static String getToken(CiConfigService ciConfigService) throws JSONException, IOException {
        JSONObject jsonObject;
        String accessToken = null;
        Cache cache = CacheManager.getCacheInfo(CacheObjects.CI_TOKEN.getObjectName());
        if (cache != null) {
            jsonObject = (JSONObject) cache.getValue();
            accessToken = jsonObject.getString(JsonKeys.ACCESS_TOKEN.getKey());
        } else {
            // Create the application/x-www-form-urlencoded keys
            Map<String, String> formParams = new HashMap<>();
            formParams.put(TokenFormParams.CLIENT_ID_KEY.getTokenValue(), ciConfigService.getClientId());
            formParams.put(TokenFormParams.CLIENT_SECRET_KEY.getTokenValue(), ciConfigService.getClientSecret());
            formParams.put(TokenFormParams.GRANT_TYPE_KEY.getTokenValue(), CiConstants.GRANT_TYPE);
            formParams.put(TokenFormParams.RESOURCE_KEY.getTokenValue(), ciConfigService.getResource());
            jsonObject = RestClient.doPost(ciConfigService.getTokenUrl(), null, null, null, formParams);
            if (jsonObject.has(RestClientConstants.ERROR_MSG)) {
                accessToken = RestClientConstants.ERROR_MSG;
            } else {
                // get long per Sonarlint
                Long expires = jsonObject.getLong(JsonKeys.ACCESS_TOKEN_EXPIRES.getKey()) * 1000;
                CacheManager.putCacheInfo(CacheObjects.CI_TOKEN.getObjectName(), jsonObject, expires);
                accessToken = jsonObject.getString(JsonKeys.ACCESS_TOKEN.getKey());
            }
        }
        return accessToken;
    }
}
