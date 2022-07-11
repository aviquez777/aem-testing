package com.edc.edcweb.core.gps;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.edc.edcweb.core.gps.service.GpsService;
import com.edc.edcweb.core.helpers.Cache;
import com.edc.edcweb.core.helpers.CacheManager;
import com.edc.edcweb.core.restful.RestClient;
import com.edc.edcweb.core.restful.RestClientConstants;
import com.edc.edcweb.core.telp.helpers.TelpConstants;


/**
 * Abstract class to provide the get token service
 *
 */
public abstract class GpsAbstractDAO {

    public enum CacheObjects {

        GPS_ENDPOINT("gps_endpoint"), GPS_TOKEN("gps_token");

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
     * @param gpsConfigService get the proper values
     * @return Json As String
     * @throws JSONException
     * @throws IOException
     */
    public static String getToken(GpsService gpsService) throws JSONException, IOException {
        JSONObject jsonObject;
        String accessToken = null;
        Cache cache = CacheManager.getCacheInfo(CacheObjects.GPS_TOKEN.getObjectName());
        if (cache != null) {
            jsonObject = (JSONObject) cache.getValue();
            accessToken = jsonObject.getString(JsonKeys.ACCESS_TOKEN.getKey());
        } else {
            // Create the application/x-www-form-urlencoded keys
            Map<String, String> formParams = new HashMap<>();
            formParams.put(TokenFormParams.CLIENT_ID_KEY.getTokenValue(), gpsService.getClientId());
            formParams.put(TokenFormParams.CLIENT_SECRET_KEY.getTokenValue(), gpsService.getClientSecret());
            formParams.put(TokenFormParams.GRANT_TYPE_KEY.getTokenValue(), TelpConstants.GRANT_TYPE);
            formParams.put(TokenFormParams.RESOURCE_KEY.getTokenValue(), gpsService.getResource());
            jsonObject = RestClient.doPost(gpsService.getTokenUrl(), null, null, null, formParams);
            if (jsonObject.has(RestClientConstants.ERROR_MSG)) {
                accessToken = RestClientConstants.ERROR_MSG;
            } else {
                // get long per Sonarlint
                Long expires = jsonObject.getLong(JsonKeys.ACCESS_TOKEN_EXPIRES.getKey()) * 1000;
                CacheManager.putCacheInfo(CacheObjects.GPS_TOKEN.getObjectName(), jsonObject, expires);
                accessToken = jsonObject.getString(JsonKeys.ACCESS_TOKEN.getKey());
            }
        }
        return accessToken;
    }
}
