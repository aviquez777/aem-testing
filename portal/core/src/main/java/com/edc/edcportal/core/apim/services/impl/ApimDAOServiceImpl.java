package com.edc.edcportal.core.apim.services.impl;

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
import com.edc.edcportal.core.apim.APIMConstants;
import com.edc.edcportal.core.apim.APIMJsonDataBindingUtil;
import com.edc.edcportal.core.apim.pojo.InfoPaymentDO;
import com.edc.edcportal.core.apim.pojo.InfoPaymentUpdateDO;
import com.edc.edcportal.core.apim.pojo.InfoPaymentStatusDO;
import com.edc.edcportal.core.apim.services.ApimConfigService;
import com.edc.edcportal.core.apim.services.ApimDAOService;
import com.edc.edcportal.core.exception.EDCAPIMSystemException;
import com.edc.edcportal.core.restful.RestClientConstants;
import com.edc.edcportal.core.restful.RestClientV2;

@Component(immediate = true, service = ApimDAOService.class)
public class ApimDAOServiceImpl implements ApimDAOService {

    private static final Logger logger = LoggerFactory.getLogger(ApimDAOServiceImpl.class);

    @Reference
    private ApimConfigService apimConfigService;

    @Override
    public InfoPaymentStatusDO getInfoPaymentStatus(String externalId) throws EDCAPIMSystemException {
        InfoPaymentStatusDO infoPaymentStatusDO = new InfoPaymentStatusDO();
        try {
            String endPoint = apimConfigService.getAPIMProxyUrl();
            endPoint = endPoint.concat(apimConfigService.getAPIMBaseEndpoint())
                    .concat(APIMConstants.EndPoints.APIM_ENDPOINT_INFOPAYMMENT.getEndPoint())
                    .concat(APIMConstants.EndPoints.APIM_ENDPOINT_EXTERNAL_ID_QUERY_PARAM.getEndPoint())
                    .concat(externalId);
            String accessToken = getToken(apimConfigService);
            String authorization = RestClientConstants.AuthMethods.BEARER_TOKEN.concat(accessToken);
            Map<String, String> headers = new HashMap<>();
            headers.put(RestClientConstants.AUTHORIZATION, authorization);
            headers.put(APIMConstants.HeaderParams.OCP_APIM_SUB_KEY.getHeaderValue(),
                    apimConfigService.getOCPAPIMsubscriptionKey());
            String jsonString = RestClientV2.doJsonGet(endPoint, headers, false);
            infoPaymentStatusDO = APIMJsonDataBindingUtil.jsonToInfoPaymentStatusDO(jsonString);
        } catch (IOException e) {
            throw new EDCAPIMSystemException("externalId: " + externalId, this.getClass().getName(),
                    "getInfoPaymentStatus", e.toString());
        }
        return infoPaymentStatusDO;
    }

    @Override
    public Boolean submitInfoPayment(InfoPaymentDO infoPaymentDO) throws EDCAPIMSystemException {
        InfoPaymentUpdateDO infoPaymentUpdateDO = new InfoPaymentUpdateDO();
        boolean result = false;
        String body = null;
        String jsonString = "";
        String errorMessage = "";
        try {
            String endPoint = apimConfigService.getAPIMProxyUrl();
            endPoint = endPoint.concat(apimConfigService.getAPIMBaseEndpoint())
                    .concat(APIMConstants.EndPoints.APIM_ENDPOINT_INFOPAYMMENT.getEndPoint());
            String accessToken = getToken(apimConfigService);
            String authorization = RestClientConstants.AuthMethods.BEARER_TOKEN.concat(accessToken);
            Map<String, String> headers = new HashMap<>();
            headers.put(RestClientConstants.AUTHORIZATION, authorization);
            headers.put(APIMConstants.HeaderParams.OCP_APIM_SUB_KEY.getHeaderValue(),
                    apimConfigService.getOCPAPIMsubscriptionKey());
            // prepare the body Json String
            body = APIMJsonDataBindingUtil.infoPaymentDOToJson(infoPaymentDO);
            jsonString = RestClientV2.doJsonPost(endPoint, body, headers, true);
            infoPaymentUpdateDO = APIMJsonDataBindingUtil.jsonToInfoPaymentUpdateDO(jsonString);
            // no news are good news
            errorMessage = infoPaymentUpdateDO.getErrorMessage();
            result = StringUtils.isBlank(errorMessage);
        } catch (IOException e) {
            throw new EDCAPIMSystemException("Form data: " + body + "Response" + jsonString, this.getClass().getName(),
                    "submitInfoPayment", e.toString());
        }
        if (!result) {
            logger.error("submitInfoPayment Form data: {} Response {}", body, jsonString);
        }
        return result;
    }

    /**
     * Get Bearer Token from API
     * 
     * @param apimConfigService get the proper values
     * @return Json As String
     */
    private String getToken(ApimConfigService apimConfigService) {
        String cacheName = APIMConstants.APIM_TOKEN;
        String bearerToken = ApiCommonTokenHelper.getBearerToken(cacheName);
        // if blank get new token
        if (StringUtils.isBlank(bearerToken)) {
            String tokenUrl = apimConfigService.getTokenUrl();
            String clientId = apimConfigService.getClientId();
            String clientSecret = apimConfigService.getClientSecret();
            String resource = apimConfigService.getResource();
            List<NameValuePair> formParams = ApiCommonTokenHelper.populateTokenFormParams(clientId, clientSecret,
                    resource);
            bearerToken = ApiCommonTokenHelper.getNewBearerToken(cacheName, tokenUrl, formParams);
        }
        return bearerToken;
    }
}
