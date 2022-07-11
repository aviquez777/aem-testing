package com.edc.edcweb.core.lovapi.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.edc.edcweb.core.apicommon.service.helpers.ApiCommonTokenHelper;
import com.edc.edcweb.core.lovapi.helpers.LovApiConstants;
import com.edc.edcweb.core.lovapi.service.LovApiConfigService;
import com.edc.edcweb.core.lovapi.service.LovApiDAOService;
import com.edc.edcweb.core.restful.RestClientConstants;
import com.edc.edcweb.core.restful.RestClientV2;
import com.edc.edcweb.core.telp.helpers.TelpConstants;

/**
 * LOV API service implementation
 *
 */
@Component(immediate = true, service = LovApiDAOService.class)
public class LovApiDAOServiceImpl implements LovApiDAOService {

    @Reference
    private LovApiConfigService lovApiConfigService;

    @Reference
    private LovApiConfigService lovApiService;

    public String getLov(String lovType) {
        return getResponse(lovApiConfigService.getGenericEndpoint(), lovType, lovApiConfigService.getOCPLovApisubscriptionKey());
    }

    public String getFiLov(String formType) {
        String response;
        String formEndPoint = null;
        // BCAP form type is COVIDR-E ** BCAP-Extension US 331242 use the BCAP banks
        if (formType.startsWith(TelpConstants.FORM_VALUE_BCAP) || formType.startsWith(TelpConstants.FORM_VALUE_BCAP_EXT)) {
            formEndPoint =lovApiService.getBcapFormFiEndPoint();
        } else if (formType.equals(TelpConstants.FORM_VALUE_MMG)) {
            formEndPoint = lovApiService.getMmgFormFiEndPoint();
        } else if (formType.equals(TelpConstants.DEFAULT_FORM_TYPE)) {
            formEndPoint = lovApiService.getTelpFormFiEndPont();
        }
        // No valid endpoint no call to API
        if (formEndPoint != null) {
            response = getResponse(lovApiService.getFiEndpoint(), formEndPoint, lovApiService.getOCPLovApisubscriptionKey());
        } else {
            response = "Invalid form type: " + formType;
        }

        return response;
    }

    private String getResponse (String apiEndPoint, String formEndPoint, String ocpSubscriptionKey){
        if (!formEndPoint.startsWith(RestClientConstants.FORWARD_SLASH)) {
            formEndPoint = RestClientConstants.FORWARD_SLASH + formEndPoint;
        }
        String endPoint = new StringBuilder(lovApiConfigService.getLovApiProxyUrl()).append(lovApiConfigService.getBaseEndpoint()).append(apiEndPoint).append(formEndPoint).toString();
        String accessToken = getToken(lovApiConfigService);
        String authorization = RestClientConstants.AuthMethods.BEARER_TOKEN.concat(accessToken);
        Map<String, String> headers = new HashMap<>();
        headers.put(RestClientConstants.AUTHORIZATION, authorization);
        headers.put(LovApiConstants.HeaderParams.OCP_APIM_SUB_KEY.getHeaderValue(), ocpSubscriptionKey);
        return RestClientV2.doJsonGet(endPoint, headers, false);
    }

    /**
     * Get Bearer Token from API
     *
     * @param lovApiConfigService get the proper values
     * @return Json As String
     */
    private String getToken(LovApiConfigService lovApiConfigService) {
        String cacheName = LovApiConstants.LOV_API_TOKEN;
        String bearerToken = ApiCommonTokenHelper.getBearerToken(cacheName);
        // if blank get new token
        if (StringUtils.isBlank(bearerToken)) {
            String tokenUrl = lovApiConfigService.getTokenUrl();
            String clientId = lovApiConfigService.getClientId();
            String clientSecret = lovApiConfigService.getClientSecret();
            String resource = lovApiConfigService.getResource();
            List<NameValuePair> formParams = ApiCommonTokenHelper.populateTokenFormParams(clientId, clientSecret,
                    resource);
            bearerToken = ApiCommonTokenHelper.getNewBearerToken(cacheName, tokenUrl, formParams);
        }
        return bearerToken;
    }
}
