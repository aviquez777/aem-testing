package com.edc.edcportal.core.businessconnections.services.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.apicommon.service.helpers.ApiCommonTokenHelper;
import com.edc.edcportal.core.businessconnections.BCJsonDataBindingUtil;
import com.edc.edcportal.core.businessconnections.helpers.BCConstants;
import com.edc.edcportal.core.businessconnections.helpers.BCDataObjectTransformationHelper;
import com.edc.edcportal.core.businessconnections.pojo.BCRecordDO;
import com.edc.edcportal.core.businessconnections.pojo.BCTokenResponse;
import com.edc.edcportal.core.businessconnections.services.BCConfigService;
import com.edc.edcportal.core.businessconnections.services.BCDAOService;
import com.edc.edcportal.core.restful.RestClientConstants;
import com.edc.edcportal.core.restful.RestClientV2;
import com.fasterxml.jackson.core.JsonProcessingException;

@Component(immediate = true, service = BCDAOService.class)
public class BCDAOServiceImpl implements BCDAOService {

    private static final Logger log = LoggerFactory.getLogger(BCDAOServiceImpl.class);

    @Reference
    private BCConfigService bcConfigService;

    @Override
    public BCTokenResponse getBCToken(SlingHttpServletRequest request) {
        BCTokenResponse bcTokenResponse = new BCTokenResponse();
        BCRecordDO bcRecordDO = BCDataObjectTransformationHelper.populateBCRecordDO(request);

        String endPoint = bcConfigService.getBCTokenProxyUrl();
        endPoint = endPoint.concat(bcConfigService.getBCTokenBaseEndpoint());

        String accessToken = getToken(bcConfigService);
        String authorization = RestClientConstants.AuthMethods.BEARER_TOKEN.concat(accessToken);
        Map<String, String> headers = new HashMap<>();
        headers.put(RestClientConstants.AUTHORIZATION, authorization);
        headers.put(BCConstants.OCP_BC_SUB_KEY, bcConfigService.getOCPBCTokensubscriptionKey());
        // prepare the body Json String
        String body = null;
        try {
            body = BCJsonDataBindingUtil.bcRecordDOToJson(bcRecordDO);
        } catch (JsonProcessingException e) {
            log.error("getBCToken bcRecordDOToJson ", e);
        }
        // only lower environments
        log.debug("BC Token Service REQUEST externalid: {} request data {}", bcRecordDO.getObjectId(), body);
        String jsonString = RestClientV2.doJsonPost(endPoint, body, headers, false);
        try {
            bcTokenResponse = BCJsonDataBindingUtil.jsonToTokenResponse(jsonString);
        } catch (IOException e) {
            log.error("getBCToken jsonToTokenResponse jsonString: {}", jsonString, e);
        }

        return bcTokenResponse;
    }

    /**
     * Get Bearer Token from API
     * 
     * @param BCConfigService get the proper values
     * @return Json As String
     */
    private String getToken(BCConfigService bcConfigService) {
        String cacheName = BCConstants.BC_TOKEN;
        String bearerToken = ApiCommonTokenHelper.getBearerToken(cacheName);
        // if blank get new token
        if (StringUtils.isBlank(bearerToken)) {
            String tokenUrl = bcConfigService.getTokenUrl();
            String clientId = bcConfigService.getClientId();
            String clientSecret = bcConfigService.getClientSecret();
            String resource = bcConfigService.getResource();
            List<NameValuePair> formParams = ApiCommonTokenHelper.populateTokenFormParams(clientId, clientSecret,
                    resource);
            bearerToken = ApiCommonTokenHelper.getNewBearerToken(cacheName, tokenUrl, formParams);
        }
        return bearerToken;
    }
}
