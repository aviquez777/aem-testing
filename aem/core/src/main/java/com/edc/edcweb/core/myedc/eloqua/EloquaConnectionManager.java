package com.edc.edcweb.core.myedc.eloqua;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.helpers.Cache;
import com.edc.edcweb.core.helpers.CacheManager;
import com.edc.edcweb.core.helpers.EloquaToken;
import com.edc.edcweb.core.myedc.eloqua.pojo.EloquaUserProfileDO;
import com.edc.edcweb.core.restful.RestClient;
import com.edc.edcweb.core.restful.RestClientConstants;
import com.edc.edcweb.core.restful.RestClientUtils;
import com.edc.edcweb.core.service.EloquaService;
import com.edc.edcweb.core.serviceImpl.EloquaServiceImpl;

public class EloquaConnectionManager {

    private static final Logger logger = LoggerFactory.getLogger(EloquaConnectionManager.class);

    private EloquaConnectionManager() {
        // Sonar Lint
    }

    /**
     * Get the Eloqua endpoint, from cache, if not fetch it from the api
     *
     * @param eloquaService
     * @return
     * @throws JSONException
     * @throws IOException
     */
    private static String getApiEndopoint(EloquaService eloquaService) throws JSONException, IOException {
        JSONObject jsonObject;
        Cache cache = CacheManager.getCacheInfo(EloquaConnectionManagerConstants.CacheObjects.ELOQUA_ENDPOINT);
        if (cache != null) {
            jsonObject = (JSONObject) cache.getValue();
        } else {
            String userName = eloquaService.getProgressProfilingServiceIDCompanyName()
                    .concat(RestClientConstants.BACK_SLASH)
                    .concat(eloquaService.getProgressProfilingServiceIDUserName());
            String password = eloquaService.getProgressProfilingServiceIDPassword();
            String toEncode = userName.concat(RestClientConstants.COLON).concat(password);
            String encodedString = RestClientUtils.base64Encode(toEncode);
            jsonObject = new JSONObject();
            try {
                jsonObject = RestClient.doGet(eloquaService.getUrlToGetBaseUrl(),
                        RestClientConstants.AuthMethods.BASIC_AUTH.concat(encodedString), null, null, null);
            } catch (JSONException e) {
                // catch the error to force use the fallback
                jsonObject.put(RestClientConstants.ERROR_MSG, e.getMessage());
            }
            if (jsonObject.has(RestClientConstants.ERROR_MSG)) {
                logger.debug("EloquaConnectionManager error while getting ApiEndopoint {}", jsonObject);
                // return temporary url untill able to get one
                return eloquaService.getEndPointFallBack().replace(
                        EloquaConnectionManagerConstants.API_VERSION_VARIABLE,
                        EloquaConnectionManagerConstants.API_VERSION);
            } else {
                // Save it on cache until we know is good
                // cast long per Sonarqube
                Long endpointCache = (long) EloquaConnectionManagerConstants.ENDPOINT_CACHE_TIME * 1000;
                CacheManager.putCacheInfo(EloquaConnectionManagerConstants.CacheObjects.ELOQUA_ENDPOINT, jsonObject,
                        endpointCache);
            }
        }

        JSONObject urls = jsonObject.getJSONObject(EloquaConnectionManagerConstants.JsonKeys.BASE_URL_URLS);
        JSONObject apis = urls.getJSONObject(EloquaConnectionManagerConstants.JsonKeys.BASE_URL_APIS);
        JSONObject rest = apis.getJSONObject(EloquaConnectionManagerConstants.JsonKeys.BASE_URL_REST);
        String endpoint = rest.getString(EloquaConnectionManagerConstants.JsonKeys.BASE_URL_STANDARD);
        return endpoint.replace(EloquaConnectionManagerConstants.API_VERSION_VARIABLE,
                EloquaConnectionManagerConstants.API_VERSION);
    }

    /**
     * Get the auth token, from cache, if not fetch it from the api
     *
     * @param eloquaService
     * @return String with token
     * @throws JSONException
     * @throws IOException
     */
    private static String getToken(EloquaService eloquaService) throws JSONException, IOException {
        JSONObject jsonObject = new JSONObject();
        String accessToken = null;
        // Check if we have the other token already
        Cache tokenObject = CacheManager.getCacheInfo(EloquaConnectionManagerConstants.CacheObjects.ELOQUA_OBJECT_TOKEN);
        if (tokenObject != null) {
            logger.debug("No JsonToken Found");
            EloquaToken eloquaTokenObj = (EloquaToken) tokenObject.getValue();
            accessToken = eloquaTokenObj.getAccessToken();
            int expires = eloquaTokenObj.getExpiresIn();
            jsonObject.put(EloquaConnectionManagerConstants.JsonKeys.ACCESS_TOKEN, accessToken);
            jsonObject.put(EloquaConnectionManagerConstants.JsonKeys.ACCESS_TOKEN_EXPIRES, expires);
            CacheManager.putCacheInfo(EloquaConnectionManagerConstants.CacheObjects.ELOQUA_TOKEN, jsonObject, expires);
            logger.debug("EloquaToken Found on eloqua_token");
        } else {
            // did not find the other token, go fetch it and save it
            Cache cache = CacheManager.getCacheInfo(EloquaConnectionManagerConstants.CacheObjects.ELOQUA_TOKEN);
            if (cache != null) {
                jsonObject = (JSONObject) cache.getValue();
                accessToken = jsonObject.getString(EloquaConnectionManagerConstants.JsonKeys.ACCESS_TOKEN);
            } else {
                String userName = eloquaService.getOath2ClientID();
                String password = eloquaService.getOath2ClientSecurity();
                String toEncode = userName.concat(RestClientConstants.COLON).concat(password);
                String encodedString = RestClientUtils.base64Encode(toEncode);
                String body = getJsonForGetTokenBody(eloquaService);
                logger.debug("EloquaConnectionManager Requesting EloquaToken from API");
                logger.debug("Current Cache: {}",CacheManager.getCacheAllkey());
                jsonObject = RestClient.doPost(eloquaService.getTokenUrl(),
                        RestClientConstants.AuthMethods.BASIC_AUTH.concat(encodedString), null, body, null);
                if (jsonObject.has(RestClientConstants.ERROR_MSG)) {
                    accessToken = RestClientConstants.ERROR_MSG;
                } else {
                    // get long per Sonarlint
                    Long expires = jsonObject.getLong(EloquaConnectionManagerConstants.JsonKeys.ACCESS_TOKEN_EXPIRES)
                            * 1000;
                    CacheManager.putCacheInfo(EloquaConnectionManagerConstants.CacheObjects.ELOQUA_TOKEN, jsonObject,
                            expires);
                    accessToken = jsonObject.getString(EloquaConnectionManagerConstants.JsonKeys.ACCESS_TOKEN);
                }
            }
        }
        // If the other token is null, add it to prevent the 429 Multiple request error
        // (and reuse token)
        if (tokenObject == null) {
            EloquaToken eloquaToken = EloquaServiceImpl.parseEloquaToken(jsonObject.toString());
            int expireSeconds = eloquaToken.getExpiresIn();
            CacheManager.putCacheInfo(EloquaConnectionManagerConstants.CacheObjects.ELOQUA_OBJECT_TOKEN, eloquaToken, (expireSeconds * 1000));
            logger.debug("getToken added EloquaTokem");
        }
        return accessToken;
    }

    /**
     * Get a record based on the unique id
     *
     * @param externalId
     * @param eloquaService
     * @return
     * @throws JSONException
     * @throws IOException
     */
    public static JSONObject getJsonRecordByExternalId(String externalId, EloquaService eloquaService)
            throws JSONException, IOException {
        String baseEndPoint = getApiEndopoint(eloquaService);
        String endPoint = getInstancesEndpoint(baseEndPoint, eloquaService);
        endPoint = endPoint
                .concat(EloquaConnectionManagerConstants.EndPoints.INSTANCES_ENDPOINT_UNIQUE_CODE_QUERY_STRING)
                .concat(externalId).concat("'");
        String accessToken = getToken(eloquaService);
        String authorization = RestClientConstants.AuthMethods.BEARER_TOKEN.concat(accessToken);
        return RestClient.doGet(endPoint, authorization, null, null, null);
    }

    /**
     * Get a record based on the email
     *
     * @param externalId
     * @param eloquaService
     * @return
     * @throws JSONException
     * @throws IOException
     */
    public static JSONObject getJsonRecordByEmail(String externalId, EloquaService eloquaService)
            throws JSONException, IOException {
        String baseEndPoint = getApiEndopoint(eloquaService);
        String endPoint = getInstancesEndpoint(baseEndPoint, eloquaService);
        endPoint = endPoint.concat(EloquaConnectionManagerConstants.EndPoints.INSTANCES_ENDPOINT_EMAIL_QUERY_STRING)
                .concat(externalId).concat("'");
        String accessToken = getToken(eloquaService);
        String authorization = RestClientConstants.AuthMethods.BEARER_TOKEN.concat(accessToken);
        return RestClient.doGet(endPoint, authorization, null, null, null);
    }

    /**
     * Convenience Method
     *
     * @param externalId
     * @param eloquaService
     * @return
     * @throws JSONException
     * @throws IOException
     */
    public static EloquaUserProfileDO getRecordByExternalId(String externalId, EloquaService eloquaService)
            throws JSONException, IOException {
        JSONObject jsonObject = getJsonRecordByExternalId(externalId, eloquaService);
        return EloquaConnectionManagerUtil.cdoSearchToDO(jsonObject);
    }

    /**
     * Get a record based on the email
     *
     * @param email
     * @param eloquaService
     * @return
     * @throws JSONException
     * @throws IOException   /** post should go to
     *                       {base}/api/REST/2.0/data/customObject/{ProgessiveProfiingCDOId}/instances?search='UniqueCode="email"'
     */

    public static JSONObject getJsonPPRecordByEmail(String email, EloquaService eloquaService)
            throws JSONException, IOException {
        String baseEndPoint = getApiEndopoint(eloquaService);
        String endPoint = getPPInstancesEndpoint(baseEndPoint, eloquaService);
        endPoint = endPoint
                .concat(EloquaConnectionManagerConstants.EndPoints.INSTANCES_ENDPOINT_UNIQUE_CODE_QUERY_STRING)
                .concat(email).concat("'");
        String accessToken = getToken(eloquaService);
        String authorization = RestClientConstants.AuthMethods.BEARER_TOKEN.concat(accessToken);
        return RestClient.doGet(endPoint, authorization, null, null, null);
    }

    public static JSONObject getPPRecordByEmail(String email, EloquaService eloquaService)
            throws JSONException, IOException {
        return getJsonPPRecordByEmail(email, eloquaService);
    }

    /**
     * Concatenates SiteID and UserName
     *
     * @param eloquaService to get the SiteID and UserName
     * @return SiteID and UserName
     */
    private static String getUserName(EloquaService eloquaService) {
        return eloquaService.getProgressProfilingServiceIDCompanyName().concat(RestClientConstants.DOUBLE_BACK_SLASH)
                .concat(eloquaService.getProgressProfilingServiceIDUserName());
    }

    /**
     * The POST request should include a JSON body with the following parameters:
     * "grant_type":"password", "scope":"full", "username":"<username>",
     * "password":"<password>"
     *
     * Since this json is not used anywhere else, we use String.format to replace
     * the 2 only variable values on a Constant String
     *
     * @param eloquaService get the required values
     * @return JSON body string
     */
    private static String getJsonForGetTokenBody(EloquaService eloquaService) {
        String userName = getUserName(eloquaService);
        String password = eloquaService.getProgressProfilingServiceIDPassword();
        return String.format(EloquaConnectionManagerConstants.BodyRequests.GET_TOKEN_JSON, userName, password);
    }

    /**
     * Convenience method to get the full Endpoint
     *
     * @param eloquaService
     * @return
     */
    private static String getInstancesEndpoint(String baseEndPoint, EloquaService eloquaService) {
        return baseEndPoint.concat(EloquaConnectionManagerConstants.EndPoints.CDO_ENDPOINT_DATA)
                .concat(EloquaConnectionManagerConstants.EndPoints.CDO_ENDPOINT)
                .concat(eloquaService.getMyEDCProfileCDOId())
                .concat(EloquaConnectionManagerConstants.EndPoints.INSTANCES_ENDPOINT);
    }

    /**
     * Convenience method to get the full PP Endpoint
     *
     * @param eloquaService
     * @return
     */
    private static String getPPInstancesEndpoint(String baseEndPoint, EloquaService eloquaService) {
        return baseEndPoint.concat(EloquaConnectionManagerConstants.EndPoints.CDO_ENDPOINT_DATA)
                .concat(EloquaConnectionManagerConstants.EndPoints.CDO_ENDPOINT).concat(eloquaService.getCDOUserInfo())
                .concat(EloquaConnectionManagerConstants.EndPoints.INSTANCES_ENDPOINT);
    }

    /**
     * Convenience method to get the full Transaction History Endpoint
     *
     * @param eloquaService
     * @return
     */
    private static String getTransactionInstancesEndpoint(String baseEndPoint, EloquaService eloquaService) {
        return baseEndPoint.concat(EloquaConnectionManagerConstants.EndPoints.CDO_ENDPOINT_DATA)
                .concat(EloquaConnectionManagerConstants.EndPoints.CDO_ENDPOINT)
                .concat(eloquaService.getMyEDCTransForm())
                .concat(EloquaConnectionManagerConstants.EndPoints.INSTANCES_ENDPOINT);
    }

    public static JSONObject getTransactionHistoryByExternalId(String externalId, EloquaService eloquaService)
            throws IOException, JSONException {
        String baseEndPoint = getApiEndopoint(eloquaService);
        String endPoint = getTransactionInstancesEndpoint(baseEndPoint, eloquaService);
        // On trasaction CDO Unique Code is the "name" search param
        endPoint = endPoint.concat(EloquaConnectionManagerConstants.EndPoints.INSTANCES_ENDPOINT_EMAIL_QUERY_STRING)
                .concat(externalId).concat("'")
                .concat(EloquaConnectionManagerConstants.EndPoints.INSTANCES_ENDPOINT_ORDER_BY_CREATED_BY_DESC);
        String accessToken = getToken(eloquaService);
        String authorization = RestClientConstants.AuthMethods.BEARER_TOKEN.concat(accessToken);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return RestClient.doGet(endPoint, authorization, headers, null, null);
    }
}
