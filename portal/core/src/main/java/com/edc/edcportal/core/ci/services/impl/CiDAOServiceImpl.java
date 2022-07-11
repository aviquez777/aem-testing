package com.edc.edcportal.core.ci.services.impl;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.ci.CiAbstractDAO;
import com.edc.edcportal.core.ci.CiConstants;
import com.edc.edcportal.core.ci.helpers.CiJsonDataBindingUtil;
import com.edc.edcportal.core.ci.pojo.CiCompanySearchDO;
import com.edc.edcportal.core.ci.services.CiConfigService;
import com.edc.edcportal.core.ci.services.CiDAOService;
import com.edc.edcportal.core.exception.EDCAPIMSystemException;
import com.edc.edcportal.core.restful.RestClient;
import com.edc.edcportal.core.restful.RestClientConstants;

@Component(immediate = true, service = CiDAOService.class)
public class CiDAOServiceImpl extends CiAbstractDAO implements CiDAOService {

    private static final Logger log = LoggerFactory.getLogger(CiDAOServiceImpl.class);

    @Reference
    private CiConfigService ciConfigService;

    @Override
    public String getCountries() throws EDCAPIMSystemException {
        String jsonString = null;
        try {
            String endPoint = ciConfigService.getCIProxyUrl();
            endPoint = endPoint.concat(ciConfigService.getCIBaseEndpoint())
                    .concat(CiConstants.EndPoints.CI_ENDPOINT_GET_COUNTRIES.getEndPoint());
            String accessToken = getToken(ciConfigService);
            String authorization = RestClientConstants.AuthMethods.BEARER_TOKEN.concat(accessToken);
            Map<String, String> headers = new HashMap<>();
            headers.put(CiConstants.HeaderParams.OCP_CI_SUB_KEY.getHeaderValue(),
                    ciConfigService.getOCPCIsubscriptionKey());
            // get the country's JSONArray
            JSONArray jsonArray = RestClient.doGetArray(endPoint, authorization, headers, null, null);
            jsonString = jsonArray.toString();
        } catch (IOException | JSONException e) {
            log.error("CiDAOServiceImpl getCountries error {}", e.getMessage());
        }
        return jsonString;
    }

    @Override
    public CiCompanySearchDO searchCompanyByName(String companyName, String edcCountryCode, String language)
            throws EDCAPIMSystemException, IOException {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(CiConstants.RESPONSE_STATUS_JSON_KEY, CiConstants.APIErrors.NO_RESULTS.getError());
        } catch (JSONException e1) {
            log.error("CiDAOServiceImpl adding default error key error {}", e1.getMessage());
        }
        try {
            String endPoint = ciConfigService.getCIProxyUrl();
            endPoint = endPoint.concat(ciConfigService.getCIBaseEndpoint())
                    .concat(CiConstants.EndPoints.CI_ENDPOINT_SEARCH_COMPANY_BY_NAME.getEndPoint())
                    .concat(CiConstants.QUESTION_MARK)
                    .concat(CiConstants.EndPoints.CI_ENDPOINT_SEARCH_COMPANY_BY_NAME_COMPANY_NAME_QUERY_PARAM
                            .getEndPoint())
                    .concat(URLEncoder.encode(companyName, StandardCharsets.UTF_8.toString())).concat(CiConstants.AMPERSAND)
                    .concat(CiConstants.EndPoints.CI_ENDPOINT_SEARCH_COMPANY_BY_NAME_COUNTRY_QUERY_PARAM.getEndPoint())
                    .concat(edcCountryCode).concat(CiConstants.AMPERSAND)
                    .concat(CiConstants.EndPoints.CI_ENDPOINT_SEARCH_COMPANY_BY_NAME_LANGUAGE_QUERY_PARAM.getEndPoint())
                    .concat(language);
            String accessToken = getToken(ciConfigService);
            String authorization = RestClientConstants.AuthMethods.BEARER_TOKEN.concat(accessToken);
            Map<String, String> headers = new HashMap<>();
            headers.put(CiConstants.HeaderParams.OCP_CI_SUB_KEY.getHeaderValue(),
                    ciConfigService.getOCPCIsubscriptionKey());
            jsonObject = RestClient.doGet(endPoint, authorization, headers, null, null);
        } catch (JSONException e) {
            log.error("CiDAOServiceImpl searchCompanyByName error {}", e.getMessage());
        }
        return CiJsonDataBindingUtil.jsonToCiCompanySearchDO(jsonObject);
    }

    @Override
    public JSONObject getCompanyDetailsById(String companyId, String language) throws EDCAPIMSystemException, IOException {
        JSONObject jsonObject = new JSONObject();
        try {
            String endPoint = ciConfigService.getCIProxyUrl();
            endPoint = endPoint.concat(ciConfigService.getCIBaseEndpoint())
                    .concat(CiConstants.EndPoints.CI_ENDPOINT_GET_COMPANY_DETAILS_BY_ID.getEndPoint())
                    .concat(companyId)
                    .concat(CiConstants.QUESTION_MARK)
                    .concat(CiConstants.EndPoints.CI_ENDPOINT_SEARCH_COMPANY_BY_NAME_LANGUAGE_QUERY_PARAM.getEndPoint())
                    .concat(language);
            String accessToken = getToken(ciConfigService);
            String authorization = RestClientConstants.AuthMethods.BEARER_TOKEN.concat(accessToken);
            Map<String, String> headers = new HashMap<>();
            headers.put(CiConstants.HeaderParams.OCP_CI_SUB_KEY.getHeaderValue(),
                    ciConfigService.getOCPCIsubscriptionKey());
            jsonObject = RestClient.doGet(endPoint, authorization, headers, null, null);
        } catch (JSONException e) {
            log.error("CiDAOServiceImpl getCompanyDetailsById error {}", e.getMessage());
        }
        return jsonObject;
    }

}
