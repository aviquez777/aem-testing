package com.edc.edcportal.core.eloqua;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.apicommon.ApiCommonJsonDataBindingUtil;
import com.edc.edcportal.core.apicommon.pojo.APICommonToken;
import com.edc.edcportal.core.eloqua.pojo.EloquaCDO;
import com.edc.edcportal.core.eloqua.pojo.EloquaEndPoint;
import com.edc.edcportal.core.eloqua.pojo.EloquaEndPointApis;
import com.edc.edcportal.core.eloqua.pojo.EloquaEndPointRest;
import com.edc.edcportal.core.eloqua.pojo.EloquaEndPointUrls;
import com.edc.edcportal.core.eloqua.pojo.EloquaRecordDO;
import com.edc.edcportal.core.eloqua.pojo.EloquaUserProfileDO;
import com.edc.edcportal.core.eloqua.services.EloquaConfigService;
import com.edc.edcportal.core.helpers.Cache;
import com.edc.edcportal.core.helpers.CacheManager;
import com.edc.edcportal.core.restful.RestClientConstants;
import com.edc.edcportal.core.restful.RestClientUtils;
import com.edc.edcportal.core.restful.RestClientV2;
import com.fasterxml.jackson.core.JsonProcessingException;

public class EloquaConnectionManager {

    private static final Logger logger = LoggerFactory.getLogger(EloquaConnectionManager.class);

    private EloquaConnectionManager() {
        // Sonar Lint
    }

    /**
     * Get the Eloqua endpoint, from cache, if not fetch it from the api
     * 
     * @return string with the endpoint
     * @throws JsonProcessingException
     */
    private static String getApiEndopoint(EloquaConfigService eloquaConfigService) throws JsonProcessingException {
        String jsonString;
        // return temporary url until able to get one
        String endPoint = eloquaConfigService.getEndPointFallBack().replace(
                EloquaConnectionManagerConstants.API_VERSION_VARIABLE, EloquaConnectionManagerConstants.API_VERSION);
        ;
        // get jsonString from cache or Api
        Cache cache = CacheManager.getCacheInfo(EloquaConnectionManagerConstants.CacheObjects.ELOQUA_ENDPOINT);
        if (cache != null) {
            jsonString = (String) cache.getValue();
        } else {
            String userName = eloquaConfigService.getSiteName().concat(RestClientConstants.BACK_SLASH)
                    .concat(eloquaConfigService.getUserName());
            String password = eloquaConfigService.getPassword();
            String toEncode = userName.concat(RestClientConstants.COLON).concat(password);
            String encodedString = RestClientUtils.base64Encode(toEncode);
            Map<String, String> headers = new HashMap<>();
            headers.put(RestClientConstants.AUTHORIZATION,
                    RestClientConstants.AuthMethods.BASIC_AUTH.concat(encodedString));
            jsonString = RestClientV2.doJsonGet(eloquaConfigService.getUrlToGetBaseUrl(), headers, false);
            if (jsonString == null || jsonString.contains(RestClientConstants.ERROR_MSG)) {
                // catch the error to force use the fallback
                logger.debug("EloquaConnectionManager error while getting ApiEndopoint, return fallback endpoint");
                return endPoint;
            } else {
                // Save it on cache until we know is good
                // cast long per Sonarqube
                Long endpointCache = (long) EloquaConnectionManagerConstants.ENDPOINT_CACHE_TIME * 1000;
                CacheManager.putCacheInfo(EloquaConnectionManagerConstants.CacheObjects.ELOQUA_ENDPOINT, jsonString,
                        endpointCache);
            }
        }
        // convert jsonString to DO to extract endpoint url
        try {
            EloquaEndPoint endPointDO = EloquaDataBindingUtil.jsonToEloquaEndPoint(jsonString);
            EloquaEndPointUrls urls = endPointDO.getUrls();
            EloquaEndPointApis apis = urls.getApis();
            EloquaEndPointRest rest = apis.getRest();
            String rawEndPoint = rest.getStandard();
            endPoint = rawEndPoint.replace(EloquaConnectionManagerConstants.API_VERSION_VARIABLE,
                    EloquaConnectionManagerConstants.API_VERSION);
        } catch (JsonProcessingException e) {
            logger.error("EloquaConnectionManager getApiEndopoint failed processing Json {}", jsonString, e);
        }
        return endPoint;
    }

    /**
     * Get the auth token, from cache, if not fetch it from the api
     * 
     * @return String with token
     * @throws JsonProcessingException
     */
    private static String getToken(EloquaConfigService eloquaConfigService) throws JsonProcessingException {
        String jsonString;
        String accessToken = null;
        Cache cache = CacheManager.getCacheInfo(EloquaConnectionManagerConstants.CacheObjects.ELOQUA_TOKEN);
        if (cache != null) {
            jsonString = (String) cache.getValue();
        } else {
            String userName = eloquaConfigService.getClientId();
            String password = eloquaConfigService.getClientSecret();
            String toEncode = userName.concat(RestClientConstants.COLON).concat(password);
            String encodedString = RestClientUtils.base64Encode(toEncode);
            String body = getJsonForGetTokenBody(eloquaConfigService);
            Map<String, String> headers = new HashMap<>();
            headers.put(RestClientConstants.AUTHORIZATION,
                    RestClientConstants.AuthMethods.BASIC_AUTH.concat(encodedString));
            jsonString = RestClientV2.doJsonPost(eloquaConfigService.getTokenUrl(), body, headers, false);
        }
        if (jsonString.contains(RestClientConstants.ERROR_MSG)) {
            accessToken = RestClientConstants.ERROR_MSG;
        } else {
            logger.debug("EloquaConnectionManager Requesting EloquaToken from API");
            logger.debug("Current Cache: {}", CacheManager.getCacheAllkey());
            try {
                APICommonToken eloquaTokenDO = ApiCommonJsonDataBindingUtil.jsonToApimToken(jsonString);
                if (cache == null) {
                    // get long per Sonarlint
                    Long expires = Long.parseLong(eloquaTokenDO.getExpiresIn()) * 1000;
                    CacheManager.putCacheInfo(EloquaConnectionManagerConstants.CacheObjects.ELOQUA_TOKEN, jsonString,
                            expires);
                }
                accessToken = eloquaTokenDO.getAccessToken();
            } catch (JsonProcessingException e) {
                logger.error("EloquaConnectionManager getToken failed processing Json {}", jsonString, e);
            }
        }
        return accessToken;
    }

    /**
     * Get the CDO to map values and prepare requests, same expiration as the
     * endpoint Get it from cache, if not fetch it from the api
     * 
     * @return String with CDO json
     * @throws JsonProcessingException
     */
    public static EloquaCDO getCDO(EloquaConfigService eloquaConfigService) throws JsonProcessingException {
        String jsonString;
        String endPoint = getApiEndopoint(eloquaConfigService);
        endPoint = endPoint.concat(EloquaConnectionManagerConstants.EndPoints.CDO_ENDPOINT_ASSET)
                .concat(EloquaConnectionManagerConstants.EndPoints.CDO_ENDPOINT)
                .concat(eloquaConfigService.getMyEDCProfileCDOId());
        String accessToken = getToken(eloquaConfigService);
        String authorization = RestClientConstants.AuthMethods.BEARER_TOKEN.concat(accessToken);

        Cache cache = CacheManager.getCacheInfo(EloquaConnectionManagerConstants.CacheObjects.ELOQUA_CDO);
        if (cache != null) {
            jsonString = (String) cache.getValue();
        } else {
            Map<String, String> headers = new HashMap<>();
            headers.put(RestClientConstants.AUTHORIZATION, authorization);
            jsonString = RestClientV2.doJsonGet(endPoint, headers, false);
        }
        // cast long per Sonarlint
        Long expires = (long) EloquaConnectionManagerConstants.ENDPOINT_CACHE_TIME * 1000;
        CacheManager.putCacheInfo(EloquaConnectionManagerConstants.CacheObjects.ELOQUA_CDO, jsonString, expires);
        // convert jsonString to EloquaCDO
        return EloquaDataBindingUtil.jsonToEloquaCDO(jsonString);
    }

    /**
     * Get a record based on the unique id
     * 
     * @param externalId
     * @return Json record by external Id as a String
     * @throws JsonProcessingException
     */
    public static String getJsonRecordByExternalId(String externalId, EloquaConfigService eloquaConfigService)
            throws JsonProcessingException {
        String baseEndPoint = getApiEndopoint(eloquaConfigService);
        String endPoint = getInstancesEndpoint(baseEndPoint, eloquaConfigService);
        endPoint = endPoint
                .concat(EloquaConnectionManagerConstants.EndPoints.INSTANCES_ENDPOINT_UNIQUE_CODE_QUERY_STRING)
                .concat(externalId).concat("'");
        String accessToken = getToken(eloquaConfigService);
        String authorization = RestClientConstants.AuthMethods.BEARER_TOKEN.concat(accessToken);
        Map<String, String> headers = new HashMap<>();
        headers.put(RestClientConstants.AUTHORIZATION, authorization);
        return RestClientV2.doJsonGet(endPoint, headers, false);
    }

    /**
     * Get a record based on the email
     * 
     * @param email
     * @return
     * @throws JsonProcessingException
     */
    public static String getJsonRecordByEmail(String externalId, EloquaConfigService eloquaConfigService)
            throws JsonProcessingException {
        String baseEndPoint = getApiEndopoint(eloquaConfigService);
        String endPoint = getInstancesEndpoint(baseEndPoint, eloquaConfigService);
        endPoint = endPoint.concat(EloquaConnectionManagerConstants.EndPoints.INSTANCES_ENDPOINT_EMAIL_QUERY_STRING)
                .concat(externalId).concat("'");
        String accessToken = getToken(eloquaConfigService);
        String authorization = RestClientConstants.AuthMethods.BEARER_TOKEN.concat(accessToken);
        Map<String, String> headers = new HashMap<>();
        headers.put(RestClientConstants.AUTHORIZATION, authorization);
        return RestClientV2.doJsonGet(endPoint, headers, false);
    }

    /**
     * Convenience Method
     * 
     * @param externalId
     * @param eloquaConfigService
     * @return
     * @throws JsonProcessingException
     * @throws JSONException
     */
    public static EloquaUserProfileDO getRecordByExternalId(String externalId, EloquaConfigService eloquaConfigService)
            throws JsonProcessingException, JSONException {
        String jsonString = getJsonRecordByExternalId(externalId, eloquaConfigService);
        EloquaRecordDO eloquaRecordDO = EloquaDataBindingUtil.jsonToEloquaRecordDO(jsonString);
        return EloquaConnectionManagerUtil.cdoSearchToDO(eloquaRecordDO);
    }

    /**
     * Get a record based on the email
     * 
     * @param email
     * @return
     * @throws JsonProcessingException post should go to
     *                                 {base}/api/REST/2.0/data/customObject/{ProgessiveProfiingCDOId}/instances?search='UniqueCode="email"'
     */

    public static EloquaRecordDO getJsonPPRecordByEmail(String email, EloquaConfigService eloquaConfigService)
            throws JsonProcessingException {
        String baseEndPoint = getApiEndopoint(eloquaConfigService);
        String endPoint = getPPInstancesEndpoint(baseEndPoint, eloquaConfigService);
        endPoint = endPoint
                .concat(EloquaConnectionManagerConstants.EndPoints.INSTANCES_ENDPOINT_UNIQUE_CODE_QUERY_STRING)
                .concat(email).concat("'");
        String accessToken = getToken(eloquaConfigService);
        String authorization = RestClientConstants.AuthMethods.BEARER_TOKEN.concat(accessToken);
        Map<String, String> headers = new HashMap<>();
        headers.put(RestClientConstants.AUTHORIZATION, authorization);
        String jsonString = RestClientV2.doJsonGet(endPoint, headers, false);
        return EloquaDataBindingUtil.jsonToEloquaRecordDO(jsonString);
    }

    public static EloquaRecordDO getPPRecordByEmail(String email, EloquaConfigService eloquaConfigService)
            throws JsonProcessingException {
        return getJsonPPRecordByEmail(email, eloquaConfigService);
    }

    /**
     * post should go to
     * {base}/api/REST/2.0/data/customObject/{parentId}/instance/{id}
     * 
     * @param values
     * @param currentValues
     * @param cdoRecordId
     * @param cdoContactId
     * @param eloquaConfigService
     * @return
     * @throws JSONException
     * @throws IOException
     */
    public static Boolean updateRecord(EloquaUserProfileDO eloquaUserProfileDO, EloquaConfigService eloquaConfigService)
            throws IOException {
        String endPoint = getApiEndopoint(eloquaConfigService);
        String accessToken = getToken(eloquaConfigService);
        String cdoRecordId = eloquaUserProfileDO.getCdoRecordId();
        endPoint = endPoint.concat(EloquaConnectionManagerConstants.EndPoints.CDO_ENDPOINT_DATA
                .concat(EloquaConnectionManagerConstants.EndPoints.CDO_ENDPOINT)
                .concat(eloquaConfigService.getMyEDCProfileCDOId())
                .concat(EloquaConnectionManagerConstants.EndPoints.INSTANCE_ENDPOINT).concat("/").concat(cdoRecordId));
        String authorization = RestClientConstants.AuthMethods.BEARER_TOKEN.concat(accessToken);
        // prepare the body JSon String
        String body = EloquaConnectionManagerJsonUtil.profileDOToJsonPut(eloquaUserProfileDO);
        // let's go for it
        Map<String, String> headers = new HashMap<>();
        headers.put(RestClientConstants.AUTHORIZATION, authorization);
        String jsonString = RestClientV2.doJsonPut(endPoint, body, headers, false);
        Boolean updateSucessfull = !jsonString.contains(RestClientConstants.ERROR_MSG);
        eloquaUserProfileDO.setUpdateSucessfull(updateSucessfull);
        return updateSucessfull;
    }

    /**
     * post should go to /{base}/api/REST/2.0/data/customObject/{parentId}/instance¨
     * 
     * @param values
     * @param eloquaService
     * @return
     * @throws JSONException
     * @throws IOException
     */
    public static EloquaUserProfileDO createRecordWithHeaders(EloquaUserProfileDO eloquaUserProfileDO,
            EloquaConfigService eloquaConfigService) throws JSONException, IOException {
        String endPoint = getApiEndopoint(eloquaConfigService);
        String accessToken = getToken(eloquaConfigService);
        endPoint = endPoint.concat(EloquaConnectionManagerConstants.EndPoints.CDO_ENDPOINT_DATA
                .concat(EloquaConnectionManagerConstants.EndPoints.CDO_ENDPOINT)
                .concat(eloquaConfigService.getMyEDCProfileCDOId())
                .concat(EloquaConnectionManagerConstants.EndPoints.INSTANCE_ENDPOINT));
        String authorization = RestClientConstants.AuthMethods.BEARER_TOKEN.concat(accessToken);
        // prepare the body JSon String
        String body = EloquaConnectionManagerJsonUtil.profileDOToJsonPost(eloquaUserProfileDO);
        // let's go for it
        Map<String, String> headers = new HashMap<>();
        headers.put(RestClientConstants.AUTHORIZATION, authorization);
        String jsonString = RestClientV2.doJsonPost(endPoint, body, headers, false);
        EloquaCDO eloquaCDO = EloquaDataBindingUtil.jsonToEloquaCDO(jsonString);
        return EloquaConnectionManagerUtil.cdoNewToDO(eloquaUserProfileDO, eloquaCDO);
    }

    /**
     * Concatenates SiteID and UserName
     * 
     * @param eloquaConfigService to get the SiteID and UserName
     * @return SiteID and UserName
     */
    private static String getUserName(EloquaConfigService eloquaConfigService) {
        return eloquaConfigService.getSiteName().concat(RestClientConstants.DOUBLE_BACK_SLASH)
                .concat(eloquaConfigService.getUserName());
    }

    /**
     * The POST request should include a JSON body with the following parameters:
     * "grant_type":"password", "scope":"full", "username":"<username>",
     * "password":"<password>"
     * 
     * Since this json is not used anywhere else, we use String.format to replace
     * the 2 only variable values on a Constant String
     * 
     * @param eloquaConfigService get the required values
     * @return JSON body string
     */
    private static String getJsonForGetTokenBody(EloquaConfigService eloquaConfigService) {
        String userName = getUserName(eloquaConfigService);
        String password = eloquaConfigService.getPassword();
        return String.format(EloquaConnectionManagerConstants.BodyRequests.GET_TOKEN_JSON, userName, password);
    }

    /**
     * Convenience method to get the full Endpoint
     * 
     * @param eloquaConfigService
     * @return
     */
    private static String getInstancesEndpoint(String baseEndPoint, EloquaConfigService eloquaConfigService) {
        return baseEndPoint.concat(EloquaConnectionManagerConstants.EndPoints.CDO_ENDPOINT_DATA)
                .concat(EloquaConnectionManagerConstants.EndPoints.CDO_ENDPOINT)
                .concat(eloquaConfigService.getMyEDCProfileCDOId())
                .concat(EloquaConnectionManagerConstants.EndPoints.INSTANCES_ENDPOINT);
    }

    /**
     * Convenience method to get the full PP Endpoint
     * 
     * @param eloquaConfigService
     * @return
     */
    private static String getPPInstancesEndpoint(String baseEndPoint, EloquaConfigService eloquaConfigService) {
        return baseEndPoint.concat(EloquaConnectionManagerConstants.EndPoints.CDO_ENDPOINT_DATA)
                .concat(EloquaConnectionManagerConstants.EndPoints.CDO_ENDPOINT)
                .concat(eloquaConfigService.getProgressiveProfileCDOId())
                .concat(EloquaConnectionManagerConstants.EndPoints.INSTANCES_ENDPOINT);
    }

    /**
     * Convenience method to get the full Transaction History Endpoint
     * 
     * @param eloquaConfigService
     * @return
     */
    private static String getTransactionInstancesEndpoint(String baseEndPoint,
            EloquaConfigService eloquaConfigService) {
        return baseEndPoint.concat(EloquaConnectionManagerConstants.EndPoints.CDO_ENDPOINT_DATA)
                .concat(EloquaConnectionManagerConstants.EndPoints.CDO_ENDPOINT)
                .concat(eloquaConfigService.getDocumentHistoryCDOId())
                .concat(EloquaConnectionManagerConstants.EndPoints.INSTANCES_ENDPOINT);
    }

    /**
     * getTransactionHistoryByExternalId Get All the history for this particular
     * customer
     * 
     * @param externalId
     * @param eloquaConfigService
     * @return
     * @throws JsonProcessingException
     */
    public static EloquaRecordDO getTransactionHistoryByExternalId(String externalId,
            EloquaConfigService eloquaConfigService) throws JsonProcessingException {
        String baseEndPoint = getApiEndopoint(eloquaConfigService);
        String endPoint = getTransactionInstancesEndpoint(baseEndPoint, eloquaConfigService);
        // On trasaction CDO Unique Code is the "name" search param
        endPoint = endPoint.concat(EloquaConnectionManagerConstants.EndPoints.INSTANCES_ENDPOINT_EMAIL_QUERY_STRING)
                .concat(externalId)
                .concat(EloquaConnectionManagerConstants.EndPoints.INSTANCES_ENDPOINT_ORDER_BY_CREATED_BY_DESC);
        String accessToken = getToken(eloquaConfigService);
        String authorization = RestClientConstants.AuthMethods.BEARER_TOKEN.concat(accessToken);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put(RestClientConstants.AUTHORIZATION, authorization);
        String jsonString = RestClientV2.doJsonGet(endPoint, headers, false);
        return EloquaDataBindingUtil.jsonToEloquaRecordDO(jsonString);
    }

    /**
     * getTransactionHistoryRecordJson
     * @param transactionID
     * @param eloquaConfigService
     * @return JSON String
     * @throws JsonProcessingException
     */
    public static String getTransactionHistoryRecordJson(String transactionID,
            EloquaConfigService eloquaConfigService) throws JsonProcessingException {
        String endPoint = getApiEndopoint(eloquaConfigService);
        String accessToken = getToken(eloquaConfigService);
        // Bug 378038 URL Encode the PIPE
        try {
            transactionID = URLEncoder.encode(transactionID, RestClientConstants.UTF_8_CHAR_SET);
        } catch (UnsupportedEncodingException e) {
            logger.error("EloquaConnectionManager getTransactionHistoryRecordJson Could not encode transactionID {}", transactionID, e);
        }
        endPoint = endPoint.concat(EloquaConnectionManagerConstants.EndPoints.CDO_ENDPOINT_DATA)
                .concat(EloquaConnectionManagerConstants.EndPoints.CDO_ENDPOINT)
                .concat(eloquaConfigService.getDocumentHistoryCDOId())
                .concat(EloquaConnectionManagerConstants.EndPoints.INSTANCES_ENDPOINT)
                .concat(EloquaConnectionManagerConstants.EndPoints.INSTANCES_ENDPOINT_TRANSACTION_UNIQUE_CODE_QUERY_STRING)
                .concat(transactionID).concat("'");
        String authorization = RestClientConstants.AuthMethods.BEARER_TOKEN.concat(accessToken);
        // let's go for it
        Map<String, String> headers = new HashMap<>();
        headers.put(RestClientConstants.AUTHORIZATION, authorization);
        return  RestClientV2.doJsonGet(endPoint, headers, false);
    }

    /**
     * getTransactionHistoryRecord there's a key restriction on unique User ID and
     * Path, must check if record exists to add or update
     * 
     * @param transactionID
     * @param eloquaConfigService
     * @return EloquaRecordDO
     * @throws JsonProcessingException
     */
    public static EloquaRecordDO getTransactionHistoryRecord(String transactionID,
            EloquaConfigService eloquaConfigService) throws JsonProcessingException {
        String jsonString = getTransactionHistoryRecordJson(transactionID, eloquaConfigService);
        return EloquaDataBindingUtil.jsonToEloquaRecordDO(jsonString);
    }

    /**
     * addTransactionHistoryRecord add new record
     * 
     * @param body
     * @param eloquaConfigService
     * @return
     * @throws JsonProcessingException
     */
    public static EloquaCDO addTransactionHistoryRecord(String body, EloquaConfigService eloquaConfigService)
            throws JsonProcessingException {
        String endPoint = getApiEndopoint(eloquaConfigService);
        String accessToken = getToken(eloquaConfigService);
        endPoint = endPoint.concat(EloquaConnectionManagerConstants.EndPoints.CDO_ENDPOINT_DATA
                .concat(EloquaConnectionManagerConstants.EndPoints.CDO_ENDPOINT)
                .concat(eloquaConfigService.getDocumentHistoryCDOId())
                .concat(EloquaConnectionManagerConstants.EndPoints.INSTANCE_ENDPOINT));
        String authorization = RestClientConstants.AuthMethods.BEARER_TOKEN.concat(accessToken);
        // let's go for it
        Map<String, String> headers = new HashMap<>();
        headers.put(RestClientConstants.AUTHORIZATION, authorization);
        String jsonString = RestClientV2.doJsonPost(endPoint, body, headers, false);
        return EloquaDataBindingUtil.jsonToEloquaCDO(jsonString);
    }

    /**
     * updateTransactionHistoryRecord Update Transaction Record
     * 
     * @param recordId
     * @param body
     * @param eloquaConfigService
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public static EloquaCDO updateTransactionHistoryRecord(String recordId, String body,
            EloquaConfigService eloquaConfigService) throws IOException, JSONException {
        String endPoint = getApiEndopoint(eloquaConfigService);
        String accessToken = getToken(eloquaConfigService);
        endPoint = endPoint.concat(EloquaConnectionManagerConstants.EndPoints.CDO_ENDPOINT_DATA
                .concat(EloquaConnectionManagerConstants.EndPoints.CDO_ENDPOINT)
                .concat(eloquaConfigService.getDocumentHistoryCDOId())
                .concat(EloquaConnectionManagerConstants.EndPoints.INSTANCE_ENDPOINT)
                .concat(RestClientConstants.FORWARD_SLASH).concat(recordId));
        String authorization = RestClientConstants.AuthMethods.BEARER_TOKEN.concat(accessToken);
        // let's go for it
        Map<String, String> headers = new HashMap<>();
        headers.put(RestClientConstants.AUTHORIZATION, authorization);
        String jsonString = RestClientV2.doJsonPost(endPoint, body, headers, false);
        return EloquaDataBindingUtil.jsonToEloquaCDO(jsonString);
    }

    public static EloquaRecordDO getConsentRecord(String transactionID, String partnersCode,
            EloquaConfigService eloquaConfigService) throws IOException {
        String baseEndPoint = getApiEndopoint(eloquaConfigService);
        String endPoint = getTransactionInstancesEndpoint(baseEndPoint, eloquaConfigService);
        // On trasaction CDO Unique Code is the "name" search param
        endPoint = endPoint.concat(
                EloquaConnectionManagerConstants.EndPoints.INSTANCES_ENDPOINT_TRANSACTION_UNIQUE_CODE_QUERY_STRING)
                .concat("\"").concat(transactionID).concat("\"'")
                .concat(EloquaConnectionManagerConstants.EndPoints.WEBINAR_PARTNER_1_QUERY_STRING).concat("\"")
                .concat(partnersCode).concat("\"'");
        String accessToken = getToken(eloquaConfigService);
        String authorization = RestClientConstants.AuthMethods.BEARER_TOKEN.concat(accessToken);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put(RestClientConstants.AUTHORIZATION, authorization);
        String jsonString = RestClientV2.doJsonGet(endPoint, headers, false);
        return EloquaDataBindingUtil.jsonToEloquaRecordDO(jsonString);
    }
}
