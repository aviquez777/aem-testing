package com.edc.edcweb.core.apicommon.service.helpers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.edc.edcweb.core.apicommon.pojo.APICommonToken;
import com.edc.edcweb.core.apicommon.service.ApiCommonConstants;
import com.edc.edcweb.core.helpers.Cache;
import com.edc.edcweb.core.helpers.CacheManager;

public class ApiCommonTokenHelper {

    private ApiCommonTokenHelper() {
        // Sonar Lint
    }

    public static String getBearerToken(String cacheName, String tokenUrl, List<NameValuePair> formParams) {
        String bearerToken = null;
        // Get From Cache
        Cache cache = CacheManager.getCacheInfo(cacheName);
        if (cache != null) {
            bearerToken = (String) cache.getValue();
        } 
        // if blank get from API
        if (StringUtils.isBlank(bearerToken)) {
            APICommonToken apimToken = ApiCommonAPIHelper.postTokenRequest(tokenUrl, null, formParams);
            if (StringUtils.isNotBlank(apimToken.getError())) {
                bearerToken = ApiCommonConstants.TOKEN_ERROR_MSG;
            } else {
                bearerToken = apimToken.getAccessToken();
                int expires = Integer.parseInt(apimToken.getExpiresIn()) * 1000;
                CacheManager.putCacheInfo(cacheName, bearerToken, expires);
            }
        }
        return bearerToken;
    }

    public static String getBearerToken(String cacheName) {
        String bearerToken = null;
        // Get From Cache
        Cache cache = CacheManager.getCacheInfo(cacheName);
        if (cache != null) {
            bearerToken = (String) cache.getValue();
        }
        return bearerToken;
    }

    public static String getNewBearerToken(String cacheName, String tokenUrl, List<NameValuePair> formParams) {
        String bearerToken = null;
        // if blank get from API
        APICommonToken apimToken = ApiCommonAPIHelper.postTokenRequest(tokenUrl, null, formParams);
        if (StringUtils.isNotBlank(apimToken.getError())) {
            bearerToken = ApiCommonConstants.TOKEN_ERROR_MSG;
        } else {
            bearerToken = apimToken.getAccessToken();
            int expires = Integer.parseInt(apimToken.getExpiresIn()) * 1000;
            CacheManager.putCacheInfo(cacheName, bearerToken, expires);
        }
        return bearerToken;
    }

    public static List<NameValuePair> populateTokenFormParams(String clientId, String clientSecret, String resource) {
        List<NameValuePair> formParams = new ArrayList<>();
        formParams.add(new BasicNameValuePair(ApiCommonConstants.TokenFormParams.CLIENT_ID_KEY.getValue(), clientId));
        formParams.add(
                new BasicNameValuePair(ApiCommonConstants.TokenFormParams.CLIENT_SECRET_KEY.getValue(), clientSecret));
        formParams.add(new BasicNameValuePair(ApiCommonConstants.TokenFormParams.RESOURCE_KEY.getValue(), resource));
        formParams.add(new BasicNameValuePair(ApiCommonConstants.TokenFormParams.GRANT_TYPE_KEY.getValue(),
                ApiCommonConstants.TokenFormParams.GRANT_TYPE_VALUE.getValue()));
        return formParams;
    }
}
