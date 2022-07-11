package com.edc.edcportal.core.lovapi.service.impl;

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
import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.lovapi.LovApiJsonDataBindingUtil;
import com.edc.edcportal.core.lovapi.helpers.LovApiConstants;
import com.edc.edcportal.core.lovapi.pojo.SearchCodesBysearchRequest;
import com.edc.edcportal.core.lovapi.pojo.SingleLovDO;
import com.edc.edcportal.core.lovapi.service.LovApiConfigService;
import com.edc.edcportal.core.lovapi.service.LovApiDAOService;
import com.edc.edcportal.core.restful.RestClientConstants;
import com.edc.edcportal.core.restful.RestClientV2;

/**
 * LOV API service implementation
 *
 */
@Component(immediate = true, service = LovApiDAOService.class)
public class LovApiDAOServiceImpl implements LovApiDAOService {

    private static final Logger logger = LoggerFactory.getLogger(LovApiDAOServiceImpl.class);

    @Reference
    private LovApiConfigService lovApiConfigService;

    @Override
    public String getLov(String codeTableName) {
        String endPoint = getEndPoint(codeTableName);
        Map<String, String> headers = getHeaders();
        return RestClientV2.doJsonGet(endPoint, headers, false);
    }

    @Override
    public String postLovSearchRequest(Integer codeSearchType, Integer codeTableId, String codeTableName, String code,
            String desc, String lang) {
        String name = StringUtils.isNotBlank(code) ? code : desc;
        String requestJsonString = null;
        String jsonString = null;
        // Prepare the POJO
        SearchCodesBysearchRequest searchRequest = new SearchCodesBysearchRequest();
        searchRequest.setCodeSearchType(codeSearchType);
        searchRequest.setCodeTableId(codeTableId);
        searchRequest.setCodeTableName(codeTableName);
        searchRequest.setCode(code);
        searchRequest.setDesc(desc);
        try {
            requestJsonString = LovApiJsonDataBindingUtil.pojoToJson(searchRequest);
            jsonString = postSearchCodes(requestJsonString);
            SingleLovDO singleLovDO = LovApiJsonDataBindingUtil.jsonToSingleLovDO(jsonString);
            if (singleLovDO.getResult() != null) {
                name = singleLovDO.getResult().getEnName();
                if (Constants.SupportedLanguages.FRENCH.getLanguageAbbreviation().equals(lang.toUpperCase())) {
                    name = singleLovDO.getResult().getFrName();
                }
            }
        } catch (IOException e) {
            logger.error("LovApiDAOServiceImpl getLovValueByCode finding request {} response {}", requestJsonString,
                    jsonString);
        }
        return name;
    }

    /**
     * getEndPoint
     * 
     * @param codeTableName
     * @return endpooint
     */
    private String getEndPoint(String codeTableName) {
        StringBuilder endPoint = new StringBuilder();
        endPoint.append(lovApiConfigService.getLovApiProxyUrl()).append(lovApiConfigService.getBaseEndpoint())
                .append(lovApiConfigService.getGenericEndpoint());
        // Add Table name if not null
        if (StringUtils.isNotBlank(codeTableName)) {
            endPoint.append(RestClientConstants.FORWARD_SLASH).append(codeTableName);
        }
        return endPoint.toString();
    }

    /**
     * getHeaders
     * 
     * @return Headers
     */
    private Map<String, String> getHeaders() {
        String accessToken = getToken(lovApiConfigService);
        String authorization = RestClientConstants.AuthMethods.BEARER_TOKEN.concat(accessToken);
        Map<String, String> headers = new HashMap<>();
        headers.put(RestClientConstants.AUTHORIZATION, authorization);
        headers.put(LovApiConstants.HeaderParams.OCP_APIM_SUB_KEY.getHeaderValue(),
                lovApiConfigService.getOCPLovApisubscriptionKey());
        return headers;
    }

    /**
     * Get Bearer Token from API
     * 
     * @param apimConfigService get the proper values
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

    public String postSearchCodes(String jsonString) {
        String endPoint = getEndPoint(null);
        String accessToken = getToken(lovApiConfigService);
        String authorization = RestClientConstants.AuthMethods.BEARER_TOKEN.concat(accessToken);
        Map<String, String> headers = new HashMap<>();
        headers.put(RestClientConstants.AUTHORIZATION, authorization);
        headers.put(LovApiConstants.HeaderParams.OCP_APIM_SUB_KEY.getHeaderValue(),
                lovApiConfigService.getOCPLovApisubscriptionKey());
        return RestClientV2.doJsonPost(endPoint, jsonString, headers, false);
    }

}
