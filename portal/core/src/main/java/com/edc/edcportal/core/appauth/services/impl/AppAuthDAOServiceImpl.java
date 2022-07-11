package com.edc.edcportal.core.appauth.services.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.apicommon.service.helpers.ApiCommonTokenHelper;
import com.edc.edcportal.core.appauth.AppAuthConstants;
import com.edc.edcportal.core.appauth.AppAuthJsonDataBindingUtil;
import com.edc.edcportal.core.appauth.pojo.AppAuthBodyRequest;
import com.edc.edcportal.core.appauth.pojo.AppAuthResponseDO;
import com.edc.edcportal.core.appauth.services.AppAuthConfigService;
import com.edc.edcportal.core.appauth.services.AppAuthDAOService;
import com.edc.edcportal.core.restful.RestClientConstants;
import com.edc.edcportal.core.restful.RestClientV2;

@Component(immediate = true, service = AppAuthDAOService.class)
public class AppAuthDAOServiceImpl implements AppAuthDAOService {

    private static final Logger logger = LoggerFactory.getLogger(AppAuthDAOServiceImpl.class);

    @Reference
    private AppAuthConfigService appAuthConfigService;

    // dummy methods
    @Override
    public boolean appAuthAccountProvisioned(String externalUserID) {
        AppAuthResponseDO appAuthResponseDO = new AppAuthResponseDO();
        try {
            String endPoint = appAuthConfigService.getAppAuthProxyUrl();
            endPoint = endPoint.concat(appAuthConfigService.getAppAuthBaseEndpoint())
                    .concat(AppAuthConstants.EndPoints.APPAUTH_ENDPOINT_EXTERNAL_ID_QUERY_PARAM.getEndPoint())
                    .concat(externalUserID)
                    .concat(AppAuthConstants.EndPoints.APPAUTH_ENDPOINT_APP_ID_QUERY_PARAM.getEndPoint())
                    .concat(externalUserID);
            String accessToken = getToken(appAuthConfigService);
            String authorization = RestClientConstants.AuthMethods.BEARER_TOKEN.concat(accessToken);
            Map<String, String> headers = new HashMap<>();
            headers.put(RestClientConstants.AUTHORIZATION, authorization);
            headers.put(AppAuthConstants.HeaderParams.OCP_APPAUTH_SUB_KEY.getHeaderValue(),
                    appAuthConfigService.getOCPAppAuthsubscriptionKey());
            String jsonString = RestClientV2.doJsonGet(endPoint, headers, false);
            appAuthResponseDO = AppAuthJsonDataBindingUtil.jsonToAppAuthResponseDO(jsonString);
        } catch (IOException e) {
            logger.error("AppAuthDAOServiceImpl appAuthAccountProvisioned: ", e);
        }
        return appAuthResponseDO.getResult().getIsCreated();
    }

    @Override
    public boolean createAppAuthAccount(String externalUserID) {
        AppAuthBodyRequest appAuthBodyRequest = new AppAuthBodyRequest(externalUserID, externalUserID);
        AppAuthResponseDO appAuthResponseDO = new AppAuthResponseDO();
        try {
            String endPoint = appAuthConfigService.getAppAuthProxyUrl();
            endPoint = endPoint.concat(appAuthConfigService.getAppAuthBaseEndpoint());
            String accessToken = getToken(appAuthConfigService);
            String authorization = RestClientConstants.AuthMethods.BEARER_TOKEN.concat(accessToken);
            Map<String, String> headers = new HashMap<>();
            headers.put(RestClientConstants.AUTHORIZATION, authorization);
            headers.put(AppAuthConstants.HeaderParams.OCP_APPAUTH_SUB_KEY.getHeaderValue(),
                    appAuthConfigService.getOCPAppAuthsubscriptionKey());
            // prepare the body Json String
            String body = AppAuthJsonDataBindingUtil.appAuthBodyRequestToJson(appAuthBodyRequest);
            String jsonString = RestClientV2.doJsonPost(endPoint, body, headers, false);
            appAuthResponseDO = AppAuthJsonDataBindingUtil.jsonToAppAuthResponseDO(jsonString);
        } catch (IOException e) {
            logger.error("AppAuthDAOServiceImpl createAppAuthAccount: ", e);
        }
        return appAuthResponseDO.getResult().getIsCreated();
    }

    /**
     * Get Bearer Token from API
     * 
     * @param apimConfigService get the proper values
     * @return Json As String
     */
    private String getToken(AppAuthConfigService appAuthConfigService) {
        String cacheName = AppAuthConstants.APPAUTH_TOKEN;
        String bearerToken = ApiCommonTokenHelper.getBearerToken(cacheName);
        // if blank get new token
        if (StringUtils.isBlank(bearerToken)) {
            String tokenUrl = appAuthConfigService.getTokenUrl();
            String clientId = appAuthConfigService.getClientId();
            String clientSecret = appAuthConfigService.getClientSecret();
            String resource = appAuthConfigService.getResource();
            List<NameValuePair> formParams = ApiCommonTokenHelper.populateTokenFormParams(clientId, clientSecret,
                    resource);
            bearerToken = ApiCommonTokenHelper.getNewBearerToken(cacheName, tokenUrl, formParams);
        }
        return bearerToken;
    }
}
