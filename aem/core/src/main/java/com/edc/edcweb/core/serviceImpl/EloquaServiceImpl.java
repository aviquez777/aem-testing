package com.edc.edcweb.core.serviceImpl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.servlets.HttpConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.exception.EDCEloquaSystemException;
import com.edc.edcweb.core.helpers.Cache;
import com.edc.edcweb.core.helpers.CacheManager;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.EloquaToken;
import com.edc.edcweb.core.helpers.FieldMapUtils;
import com.edc.edcweb.core.myedc.eloqua.EloquaConnectionManagerConstants;
import com.edc.edcweb.core.service.EloquaService;
import com.edc.edcweb.core.service.EloquaServiceConfiguration;
import com.edc.edcweb.core.service.FieldMappingConfigService;
import com.edc.edcweb.core.serviceImpl.pojo.EloquaContact;
import com.edc.edcweb.core.serviceImpl.pojo.EloquaDocHistory;
import com.edc.edcweb.core.serviceImpl.pojo.MyEDCTransaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component(immediate = true, service = EloquaService.class, configurationPid = "com.edc.edcweb.core.serviceImpl.EloquaServiceImpl")

@Designate(ocd = EloquaServiceConfiguration.class)
public class EloquaServiceImpl implements EloquaService {

    private EloquaServiceConfiguration config;
    private static final Logger log = LoggerFactory.getLogger(EloquaServiceImpl.class);
    private static final String CDO_ENDPOINT = "api/REST/2.0/data/customObject/";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BASIC = "Basic ";
    private static final String CHARSET = "charset";
    private static final String UTF_8 = "UTF-8";
    private static final String TOTAL = "total";
    private static final String ELEMENTS = "elements";
    private static final String FIELD_VALUES = "fieldValues";
    private static final String VALUE = "value";
    private static final String ELOQUA_TOKEN = "eloqua_token";

    @Reference
    private FieldMappingConfigService fieldMappingConfigService;

    protected String configSubmitURL;
    protected String configUrlToGetBaseUrl;
    protected String configTokenUrl;
    protected String configCompanyName;
    protected String configServiceId;
    protected String configServicePwd;
    protected String configSiteId;
    protected String configPrograssiveFormName;
    protected String configProgressiveFormId;
    protected String configCDOIDUserInfo;
    protected String configCDOIDDocHistory;
    protected String configOath2ClientID;
    protected String configOath2ClientSecurity;
    protected String configNewsLetterFormName;
    protected String configExportHelpRequestFormName;
    protected String configExportHelpRequestFormId;
    protected String configKnowledgeProductFormName;
    protected String configEhFormName;
    protected String configEhFormId;
    protected String configEhLookupIdVisitor;
    protected String configEhLookupIdPrimary;

    protected String configCDOIDMyEDCTransactions;

    private String tokenType = "Bearer";

    @Activate
    @Modified
    public void activate(EloquaServiceConfiguration config) {
        this.config = config;
        this.configSubmitURL = config.formSubmitURL();
        this.configUrlToGetBaseUrl = config.urlToGetBaseUrl();
        this.configTokenUrl = config.tokenUrl();
        this.configPrograssiveFormName = config.progressProfilingFormName();
        this.configProgressiveFormId = config.progressProfilingFormId();
        this.configCompanyName = config.progressProfilingServiceIDCompanyName();
        this.configServiceId = config.progressProfilingServiceIDUserName();
        this.configServicePwd = config.progressProfilingServiceIDPassword();
        this.configSiteId = config.siteId();
        this.configCDOIDUserInfo = config.CDOUserInfo();
        this.configCDOIDDocHistory = config.CDODocHistory();
        this.configOath2ClientID = config.oath2ClientID();
        this.configOath2ClientSecurity = config.oath2ClientSecurity();
        this.configNewsLetterFormName = config.newsletterFormName();
        this.configExportHelpRequestFormName = config.exportHelpRequestFormName();
        this.configExportHelpRequestFormId = config.exportHelpRequestFormId();
        this.configKnowledgeProductFormName = config.knowledgeCostumerFormName();
        this.configEhLookupIdVisitor = config.ehLookupIdVisitor();
        this.configEhLookupIdPrimary = config.ehLookupIdPrimary();
        this.configCDOIDMyEDCTransactions = config.MyEDCTransForm();
    }

    /**
     * Get EloquaContact object by given email address
     * 
     * @param email
     * @return EloquaContact object
     */
    @Override
    public EloquaContact getEloquaContact(String email) {
        log.info("EloquaServiceImpl Method Name getEloquaContact() [IN]");
        // Get Eloqua access_token
        String accessToken = getAccessToken(configOath2ClientID, configOath2ClientSecurity, false);
        // Get base URL
        String baseURL = getEloquaBaseURL(false);

        // Get User Info
        EloquaContact eloquaContact;

        String jsonResponse = retrieveContactInfo(baseURL, email, accessToken);
        if (jsonResponse == null || jsonResponse.isEmpty() || jsonResponse.equalsIgnoreCase("401")) {
            // if return empty or 401, means token or baseURL is expired, we need to refresh
            // the token first.
            // if still get error, we refresh the baseURL

            // Try to refresh the token
            accessToken = getAccessToken(configOath2ClientID, configOath2ClientSecurity, true);
            jsonResponse = retrieveContactInfo(baseURL, email, accessToken);

            if (jsonResponse == null || jsonResponse.isEmpty() || jsonResponse.equalsIgnoreCase("401")) {
                // Try to refresh the baseURL, and do again
                baseURL = getEloquaBaseURL(true);
                jsonResponse = retrieveContactInfo(baseURL, email, accessToken);
            }
        }

        log.debug(" getEloquaContact： {}  ", jsonResponse);
        eloquaContact = jsonToEloquaContact(jsonResponse);

        log.info("EloquaServiceImpl Method Name getEloquaContact() [OUT]");
        return eloquaContact;
    }

    /**
     * Get document access history by given email address and doc id
     * 
     * @param email
     * @param docId
     * @return EloquaDocHistory object
     */
    @Override
    public ArrayList<EloquaDocHistory> getEloquaDocHistory(String email, String docId) {

        log.info("EloquaServiceImpl Method Name getEloquaDocHistory() [IN]");

        // Get Eloqua access_token
        String accessToken = getAccessToken(configOath2ClientID, configOath2ClientSecurity, false);
        // Get base URL
        String baseURL = getEloquaBaseURL(false);

        // Get USer Info
        ArrayList<EloquaDocHistory> eloquaDocHistory;
        String uniqueCode = email + "|" + docId;
        String jsonResponse = retrieveDocHistory(baseURL, uniqueCode, accessToken);
        if (jsonResponse == null || jsonResponse.isEmpty() || jsonResponse.equalsIgnoreCase("401")) {
            // if return empty or 401, means token or baseURL is expired, we need to refresh
            // the token first.
            // if still get error, we refresh the baseURL
            accessToken = getAccessToken(configOath2ClientID, configOath2ClientSecurity, true);
            baseURL = getEloquaBaseURL(true);
            jsonResponse = retrieveDocHistory(baseURL, uniqueCode, accessToken);

            if (jsonResponse == null || jsonResponse.isEmpty() || jsonResponse.equalsIgnoreCase("401")) {
                // Try to refresh the baseURL, and do again
                baseURL = getEloquaBaseURL(true);
                jsonResponse = retrieveDocHistory(baseURL, uniqueCode, accessToken);
            }
        }

        log.debug(" getEloquaDocHistory： {}  ", jsonResponse);
        eloquaDocHistory = jsonToEloquaDocHistory(jsonResponse);

        log.info(" eloquaDocHistory: JSON= {} ", jsonResponse);
        log.info("EloquaServiceImpl Method Name getEloquaDocHistory() [OUT]");
        return eloquaDocHistory;
    }

    /**
     * Update Eloqua user information by given EloquaContact object
     * 
     * @param contactBean
     * @return String of request response
     */
    @Override
    public String updateEloquaContact(EloquaContact contactBean, boolean addToNewsletter, boolean updateDocHistory,  // NOSONAR
            boolean isExportHelp, boolean isKnowledgeCostumerForm, boolean needUpdatePAUDate) {

        log.info("EloquaServiceImpl Method Name updateEloquaContact() [IN]");
        log.debug("Tracking Contact Bean : {}", contactBean);
        Map<String, String> arguments = new HashMap<>();
        Map<String, String> properties = contactBean.getProperties();
        String formName;

        if (isExportHelp) {
            formName = configExportHelpRequestFormName;
        } else if (isKnowledgeCostumerForm) {
            formName = configKnowledgeProductFormName;
            if (needUpdatePAUDate) {
                arguments.put("PremiumAccessUserDate", getCurrentTimeStamp());

                // extra field eloqua required for web data lookup for Pau form
                if (updateDocHistory && contactBean.getDocID() != null && contactBean.getDocID().trim().length() > 0) {
                    arguments.put("emaildocID", contactBean.getEmailAddress() + "|" + contactBean.getDocID());
                }
            }
        } else {
            formName = configPrograssiveFormName;
        }

        arguments.put("elqFormName", formName);
        arguments.put("elqSiteID", configSiteId);

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (entry.getValue() != null && entry.getValue().trim().length() > 0) {
                arguments.put(entry.getKey(), entry.getValue());
            }
        }
        // extra field eloqua required for web data lookup (except PAU form)
        if (!isKnowledgeCostumerForm && updateDocHistory && contactBean.getDocID() != null
                && contactBean.getDocID().trim().length() > 0) {
            arguments.put("emaildocID", contactBean.getEmailAddress() + "|" + contactBean.getDocID());
        }

        // Extra Hidden fields which not included in Contact Bean:
        arguments.put("kw", "");
        arguments.put("il", "");
        arguments.put("formID", isExportHelp ? configExportHelpRequestFormId : configProgressiveFormId);

        if (addToNewsletter) {
            arguments.put("caslConsent", "yes");
        }
        log.debug(" arguments = {}", arguments);

        String strResponds = doHTTPPost(arguments);

        log.debug("strResponds = {}", strResponds);
        log.info("EloquaServiceImpl Method Name updateEloquaContact() [OUT]");
        return strResponds;
    }

    private String retrieveContactInfo(String baseURL, String email, String accessToken) {
        log.info("EloquaServiceImpl Method Name retrieveContactInfo() [IN]");
        String strResponse = "";

        if (baseURL != null && !baseURL.endsWith("/")) {
            baseURL += "/";
        }
        String url = baseURL + CDO_ENDPOINT + configCDOIDUserInfo + "/instances?search='UniqueCode=" + email + "'";
        log.debug("Eloqua Request URL {}", url);

        strResponse = doRESTfulGet(url, accessToken);
        log.debug("strResponds = {}", strResponse);
        log.info("EloquaServiceImpl Method Name retrieveContactInfo() [OUT]");
        return strResponse;
    }

    private String retrieveDocHistory(String baseURL, String uniqueCode, String accessToken) {

        log.info("EloquaServiceImpl Method Name retrieveDocHistory() [IN]");

        String strResponse = "";
        if (baseURL != null && !baseURL.endsWith("/")) {
            baseURL += "/";
        }
        String url = baseURL + CDO_ENDPOINT + configCDOIDDocHistory + "/instances?search='UniqueCode=" + uniqueCode
                + "'";
        log.debug("Eloqua Request URL {}", url);

        strResponse = doRESTfulGet(url, accessToken);

        log.debug("strResponse = {}", strResponse);
        log.info("EloquaServiceImpl Method Name retrieveDocHistory() [OUT]");
        return strResponse;
    }

    private String doRESTfulGet(String strURL, String accessToken) {

        StringBuilder sbResponse = new StringBuilder();
        String authString = null;
        if (this.tokenType != null) {
            authString = StringUtils.capitalize(tokenType) + " " + accessToken;
        }

        try {

            URL url = new URL(strURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(Constants.CONNECT_TIMEOUT);
			conn.setReadTimeout(Constants.CONNECT_TIMEOUT);
            conn.setRequestMethod(HttpConstants.METHOD_GET);
            conn.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
            conn.setRequestProperty(AUTHORIZATION, authString);
            // Check response code
            int responeseCode = conn.getResponseCode();
            if (responeseCode != 200) {
                log.debug("Unauthorized! responeseCode = {}", responeseCode);
                conn.disconnect();
                return String.valueOf(responeseCode);
            }
            // Get response value
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                sbResponse.append(line);
            }
            reader.close();

        } catch (IOException | IllegalArgumentException | SecurityException | IllegalStateException | NullPointerException e) {
            log.error(e.getMessage());
        }

        return sbResponse.toString();
    }

    private String doHTTPPost(Map<String, String> arguments) {

        String strResponds = null;

        URL url;
        URLConnection urlConnection;
        HttpURLConnection httpURLConnection;

        try {

            url = new URL(configSubmitURL);
            urlConnection = url.openConnection();
            httpURLConnection = (HttpURLConnection) urlConnection;
			httpURLConnection.setConnectTimeout(Constants.CONNECT_TIMEOUT);
			httpURLConnection.setReadTimeout(Constants.CONNECT_TIMEOUT);
            httpURLConnection.setRequestMethod(HttpConstants.METHOD_POST);
            httpURLConnection.setDoOutput(true);

            StringJoiner stringJoiner = new StringJoiner("&");
            for (Map.Entry<String, String> entry : arguments.entrySet()) {
                stringJoiner.add(
                        URLEncoder.encode(entry.getKey(), UTF_8) + "=" + URLEncoder.encode(entry.getValue(), UTF_8));
            }

            byte[] out = stringJoiner.toString().getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            httpURLConnection.setFixedLengthStreamingMode(length);
            httpURLConnection.setRequestProperty(CONTENT_TYPE, "application/x-www-form-urlencoded; charset=" + UTF_8);
            httpURLConnection.connect();

            OutputStream os = httpURLConnection.getOutputStream();
            os.write(out);

            // Get response code
            int respCode = httpURLConnection.getResponseCode();
            strResponds = String.valueOf(respCode);
            log.info("Eloqua response message {}", httpURLConnection.getResponseMessage());

        } catch (IOException | IllegalArgumentException | SecurityException | IllegalStateException | NullPointerException e) {
            log.error(e.getMessage());
            strResponds = "500";
        }

        return strResponds;
    }

    private String base64Encode(String strInput) {
        String strOutput = null;

        byte[] bytesEncoded = Base64.getEncoder().encode(strInput.getBytes());
        strOutput = new String(bytesEncoded);
        return strOutput;
    }

    private String getAccessToken(String clientId, String clientSecret, boolean forceRefresh) { // NOSONAR

        log.info("EloquaServiceImpl Method Name getAccessToken() [IN]");

        String txtLine = "";
        int intRespCode = 0;
        String strResponse = "";
        boolean cacheExpired = false;
        String accessToken = "";

        // get token info from cache
        EloquaToken eloquaToken = getEloquaTokenFromCache();
        if (eloquaToken != null) {
            accessToken = eloquaToken.getAccessToken();
            this.tokenType = eloquaToken.getTokenType();
        }
        if (forceRefresh || eloquaToken == null || accessToken == null || accessToken.isEmpty()) {
            cacheExpired = true;
        }

        // For debug, check if token cache working correctly:
        if (cacheExpired) {
            log.info("Current cached token expired. ");
        } else {
            log.info("Reusing cached token");
        }

        // if cache expired or not exist, retrieve from Eloqua, and update cache
        if (cacheExpired) {
            if (clientId != null && !clientId.isEmpty() && clientSecret != null && !clientSecret.isEmpty()) {

                String authString = base64Encode(clientId + ":" + clientSecret);
                authString = BASIC + authString;
                String tokenURL = configTokenUrl;
                String input = "{\"grant_type\":\"password\",\"scope\":\"full\",\"username\":\"" + configCompanyName
                        + "\\\\" + configServiceId + "\",\"password\":\"" + configServicePwd + "\"}";

                log.debug("input = {}", input);

                try {
                    URL url = new URL(tokenURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(Constants.CONNECT_TIMEOUT);
                    conn.setReadTimeout(Constants.CONNECT_TIMEOUT);
                    conn.setDoOutput(true);
                    conn.setRequestMethod(HttpConstants.METHOD_POST);
                    conn.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
                    StringBuilder sb = new StringBuilder();

                    if (authString != null) {

                        conn.setRequestProperty(AUTHORIZATION, authString);

                        byte[] postData = input.getBytes();
                        OutputStream os = conn.getOutputStream();
                        os.write(postData);
                        os.flush();
                        os.close();

                        intRespCode = conn.getResponseCode();
                        log.debug("EloquaServiceImpl Requesting EloquaToken from API");
                        log.debug("Current Cache: {}", CacheManager.getCacheAllkey());

                        if (intRespCode != 200) {
                            throw new EDCEloquaSystemException("Failed : HTTP response code: " + intRespCode
                                    + ". Response Message: " + conn.getResponseMessage());
                        }

                        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

                        while ((txtLine = br.readLine()) != null) {
                            strResponse = strResponse.concat(txtLine);
                            sb.append(txtLine + "\n");
                        }

                        conn.disconnect();
                    }
                    log.debug("strResponse =  {} ", strResponse);
                    eloquaToken = parseEloquaToken(strResponse);
                    accessToken = eloquaToken.getAccessToken();
                    // check the other token and add it to prevent the 429 Multiple request error
                    // (and reuse token)
                    Cache jsonCache = CacheManager
                            .getCacheInfo(EloquaConnectionManagerConstants.CacheObjects.ELOQUA_TOKEN);
                    if (jsonCache == null) {
                        int expires = eloquaToken.getExpiresIn();
                        CacheManager.putCacheInfo(EloquaConnectionManagerConstants.CacheObjects.ELOQUA_TOKEN,
                                new JSONObject(strResponse), expires);
                        log.debug("JsonToken Added by Eloqua Service");
                    }

                } catch (IOException | IllegalArgumentException | SecurityException | IllegalStateException | NullPointerException | EDCEloquaSystemException | JSONException e) {
                    log.error("Eloqua authentication failed.");
                    log.error(e.getMessage());

                }
            }
            if (eloquaToken != null) {
                String tempTokenType = eloquaToken.getTokenType();
                if (tempTokenType.equalsIgnoreCase("bearer")) {
                    this.tokenType = "Bearer";
                }
                // update cache
                int expireSeconds = eloquaToken.getExpiresIn();
                CacheManager.putCacheInfo(ELOQUA_TOKEN, eloquaToken, (long) expireSeconds * 1000);
                String accessTokenSubStr = null;
                if (accessToken != null) {
                    accessTokenSubStr = accessToken.substring(0, 10);
                }
                log.info("New token cached. Token value = '***** {} *****'", accessTokenSubStr);
            }
        }

        log.info("EloquaServiceImpl Method Name getAccessToken() [OUT]");
        return accessToken;
    }

    // public to reuse the function
    public static EloquaToken parseEloquaToken(String jsonResponse) {

        EloquaToken eloquaToken = new EloquaToken();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(jsonResponse);

            JsonNode tokenNode = jsonNode.findValue("access_token");
            String accessTokenVal = tokenNode.textValue();
            eloquaToken.setAccessToken(accessTokenVal);

            JsonNode tokenType = jsonNode.findValue("token_type");
            String tokenTypeVal = tokenType.textValue();
            eloquaToken.setTokenType(tokenTypeVal);

            JsonNode expireIn = jsonNode.findValue("expires_in");
            int expiresInVal = expireIn.intValue();
            eloquaToken.setExpiresIn(expiresInVal);

            JsonNode refreshToken = jsonNode.findValue("refresh_token");
            String refreshTokenVal = refreshToken.textValue();
            eloquaToken.setRefreshToken(refreshTokenVal);

        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return eloquaToken;

    }

    private String getEloquaBaseURL(boolean forceRefresh, boolean rest) {  // NOSONAR

        log.info("EloquaServiceImpl Method Name getEloquaBaseURL() [IN]");

        boolean cacheExpired = false;
        String baseURL = "";
        String strURL = this.configUrlToGetBaseUrl;
        String authString = this.configCompanyName + "\\" + this.configServiceId + ":" + configServicePwd;
        String authStringBase64 = base64Encode(authString);
        StringBuilder sbResponse = new StringBuilder();

        // get baseURL info from cache
        String cacheBaseURL = null;

        if (rest) {
            cacheBaseURL = getEloquaBaseRESTURLFromCache();
        } else {
            cacheBaseURL = getEloquaBaseURLFromCache();
        }

        if (cacheBaseURL != null) {
            baseURL = cacheBaseURL;
        }
        if (forceRefresh || cacheBaseURL == null || cacheBaseURL.isEmpty()) {
            cacheExpired = true;
        }

        // For debug, check if token cache working correctly:
        if (cacheExpired) {
            log.info("Current cached Eloqua baseURL expired. ");
        } else {
            log.info("Reusing cached Eloqua baseURL");
        }

        // if cache expired or not exist, retrieve from Eloqua, and update cache
        if (cacheExpired) {
            try {

                URL url = new URL(strURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(Constants.CONNECT_TIMEOUT);
                conn.setReadTimeout(Constants.CONNECT_TIMEOUT);
                conn.setRequestMethod(HttpConstants.METHOD_GET);
                conn.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
                conn.setRequestProperty(AUTHORIZATION, BASIC + authStringBase64);  // NOSONAR
                // Check response code
                int responeseCode = conn.getResponseCode();
                if (responeseCode != 200) {
                    log.debug("Unauthorized! responeseCode= {}", responeseCode);
                    return String.valueOf(responeseCode);
                }
                // Get response value
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    sbResponse.append(line);
                }
                reader.close();

            } catch (IOException | IllegalArgumentException | SecurityException | IllegalStateException | NullPointerException e) {
                log.error(e.getMessage());
            }

            String strResponse = sbResponse.toString();
            // Never expire
            int expireSeconds = -1;

            if (rest) {
                baseURL = parseEloquaRESTURL(strResponse);
                CacheManager.putCacheInfo("eloqua_baseRESTURL", baseURL, expireSeconds);
            } else {
                baseURL = parseEloquaBaseURL(strResponse);
                CacheManager.putCacheInfo("eloqua_baseURL", baseURL, expireSeconds);
            }

        }

        log.info("EloquaServiceImpl Method Name getEloquaBaseURL() [OUT]");

        return baseURL;
    }

    // Overloaded getEloquaBaseURL method to make sure existing code doesn't break.
    private String getEloquaBaseURL(boolean forceRefresh) {
        return getEloquaBaseURL(forceRefresh, false);
    }

    private String parseEloquaBaseURL(String jsonResponse) {
        String baseURL = "";

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(jsonResponse);

            // Get Eloqua baseURL
            JsonNode urlsNode = jsonNode.findValue("urls");
            JsonNode baseNode = urlsNode.findValue("base");
            baseURL = baseNode.textValue();

        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return baseURL;
    }

    private String parseEloquaRESTURL(String jsonResponse) {
        String restURL = "";

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(jsonResponse);

            // Get Eloqua baseURL
            JsonNode urlsNode = jsonNode.findValue("urls");
            JsonNode restNode = urlsNode.findValue("rest");
            JsonNode stdNode = restNode.findValue("standard");
            restURL = stdNode.textValue();

            restURL = restURL.replace("{version}", Constants.Paths.ELOQUA_MYEDC_CDO_VERSION);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return restURL;

    }

    private EloquaContact jsonToEloquaContact(String json) {  // NOSONAR

        EloquaContact contact = new EloquaContact();

        try {

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(json);
            HashMap<String, String> fieldsValuesMap = new HashMap<>();

            JsonNode total = jsonNode.findValue(TOTAL);

            if (total != null) {

                if (total.asInt() == 0) {
                    // user does not exist
                } else if (total.asInt() == 1) {
                    JsonNode elements = jsonNode.findValue(ELEMENTS);

                    if (elements != null) {
                        JsonNode fieldValues = elements.findValue(FIELD_VALUES);

                        if (fieldValues != null) {
                            Iterator<JsonNode> fieldValueIterator = fieldValues.elements();

                            while (fieldValueIterator.hasNext()) {
                                JsonNode aProperty = fieldValueIterator.next();
                                String id = aProperty.findValue("id").asText();
                                String value = aProperty.findValue(VALUE).asText();
                                fieldsValuesMap.put(id, value);
                            }

                            contact = populateContactBean(fieldsValuesMap);

                        }
                    }
                }
            }
        } catch (IOException | NoSuchElementException e) {
            log.error(e.getMessage());
        }

        return contact;
    }

    private MyEDCTransaction jsonToMyEDCTransaction(String json) {  // NOSONAR

        MyEDCTransaction myEDCTransaction = new MyEDCTransaction();

        try {

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(json);
            HashMap<String, String> fieldsValuesMap = new HashMap<>();

            JsonNode total = jsonNode.findValue(TOTAL);

            if (total != null) {

                if (total.asInt() == 0) {
                    // user does not exist
                } else if (total.asInt() == 1) {
                    JsonNode elements = jsonNode.findValue(ELEMENTS);

                    if (elements != null) {
                        JsonNode idValue = elements.findValue("id");
                        JsonNode fieldValues = elements.findValue(FIELD_VALUES);

                        if (fieldValues != null) {
                            Iterator<JsonNode> fieldValueIterator = fieldValues.elements();

                            while (fieldValueIterator.hasNext()) {
                                JsonNode aProperty = fieldValueIterator.next();
                                String id = aProperty.findValue("id").asText();
                                String value = aProperty.findValue(VALUE).asText();
                                fieldsValuesMap.put(id, value);
                            }

                            myEDCTransaction = populateMyEDCTransactionBean(fieldsValuesMap);
                            myEDCTransaction.setID(idValue.textValue());

                        }
                    }
                }
            }
        } catch (IOException | NoSuchElementException e) {
            log.error(e.getMessage());
        }

        return myEDCTransaction;
    }

    private ArrayList<EloquaDocHistory> jsonToEloquaDocHistory(String json) {
        // We need to return ArrayList because MEA-* may return multiple histories for
        // different MEA countries
        ArrayList<EloquaDocHistory> docHistoryList = new ArrayList<>();

        try {

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(json);
            HashMap<String, String> fieldsValuesMap = new HashMap<>();
            JsonNode total = jsonNode.findValue(TOTAL);

            if (total != null && total.asInt() > 0) {
                JsonNode elements = jsonNode.findValue(ELEMENTS);
                if (elements != null) {
                    List<JsonNode> fieldValuesList = elements.findValues(FIELD_VALUES);
                    Iterator<JsonNode> listItor = fieldValuesList.iterator();
                    while (listItor.hasNext()) {
                        JsonNode fieldValues = listItor.next();
                        Iterator<JsonNode> fieldValueIterator = fieldValues.elements();
                        EloquaDocHistory docHistory = new EloquaDocHistory();
                        while (fieldValueIterator.hasNext()) {
                            JsonNode aProperty = fieldValueIterator.next();
                            String id = aProperty.findValue("id").asText();
                            String value = aProperty.findValue(VALUE).asText();
                            fieldsValuesMap.put(id, value);

                            docHistory = populateDocHistoryBean(fieldsValuesMap);
                        }

                        docHistoryList.add(docHistory);
                    }
                }
            }
        } catch (IOException | NoSuchElementException e) {
            log.error(e.getMessage());
        }
        return docHistoryList;
    }

    // MYEDCTransaction
    @Override
    public MyEDCTransaction getMyEDCTransaction(String uniqueCode) {
        log.info("EloquaServiceImpl Method Name getMyEDCTransaction() [IN]");
        // Get Eloqua access_token
        String accessToken = getAccessToken(configOath2ClientID, configOath2ClientSecurity, false);
        // Get base URL
        String baseURL = getEloquaBaseURL(false);

        // Get User Info
        MyEDCTransaction myEDCTransaction;

        String jsonResponse = retrieveMyEDCTransaction(baseURL, uniqueCode, accessToken);
        if (jsonResponse == null || jsonResponse.isEmpty() || jsonResponse.equalsIgnoreCase("401")) {
            // if return empty or 401, means token or baseURL is expired, we need to refresh
            // the token first.
            // if still get error, we refresh the baseURL

            // Try to refresh the token
            accessToken = getAccessToken(configOath2ClientID, configOath2ClientSecurity, true);
            jsonResponse = retrieveMyEDCTransaction(baseURL, uniqueCode, accessToken);

            if (jsonResponse == null || jsonResponse.isEmpty() || jsonResponse.equalsIgnoreCase("401")) {
                // Try to refresh the baseURL, and do again
                baseURL = getEloquaBaseURL(true);
                jsonResponse = retrieveMyEDCTransaction(baseURL, uniqueCode, accessToken);
            }
        }

        log.debug("getMyEDCTransaction： {}  ", jsonResponse);
        myEDCTransaction = jsonToMyEDCTransaction(jsonResponse);

        log.info("EloquaServiceImpl Method Name getMyEDCTransaction() [OUT]");
        return myEDCTransaction;
    }

    private String retrieveMyEDCTransaction(String baseURL, String code, String accessToken) {
        log.info("EloquaServiceImpl Method Name retrieveMyEDCTransaction() [IN]");
        String strResponse = "";

        if (baseURL != null && !baseURL.endsWith("/")) {
            baseURL += "/";
        }
        String url = baseURL + CDO_ENDPOINT + configCDOIDMyEDCTransactions + "/instances?search='"
                + Constants.Paths.ELOQUA_MYEDC_CDO_UNIQUE_CODE_INTERNAL_NAME + "=" + code + "'";
        log.debug("Eloqua Request URL {} ", url);

        strResponse = doRESTfulGet(url, accessToken);
        log.debug(" strResponds= {}", strResponse);
        log.info("EloquaServiceImpl Method Name retrieveMyEDCTransaction() [OUT]");
        return strResponse;
    }

    @Override
    public String updateMyEDCTransaction(MyEDCTransaction myEDCTransaction, boolean update) {

        log.info("EloquaServiceImpl Method Name updateMyEDCTransaction() [IN]");
        log.debug("Tracking Contact Bean : {}", myEDCTransaction);

        // Get Eloqua access_token
        String accessToken = getAccessToken(configOath2ClientID, configOath2ClientSecurity, false);
        // Get base URL
        String baseRESTURL = getEloquaBaseURL(false, true);

        if (update) {
            baseRESTURL = baseRESTURL.concat("data/customObject/").concat(configCDOIDMyEDCTransactions)
                    .concat("/instance/" + myEDCTransaction.getID());
        } else {
            baseRESTURL = baseRESTURL.concat("data/customObject/").concat(configCDOIDMyEDCTransactions)
                    .concat("/instance");
        }

        String body = "";
        try {
            body = myEDCTransactionBeanToJSON(myEDCTransaction);
        } catch (JSONException e) {
            log.error("EloquaServiceImpl error {}", e.getMessage());
        }

        String responseCode = doPOSTPUT(baseRESTURL, accessToken, body, update);

        log.info("EloquaServiceImpl Method Name updateEloquaContact() [OUT]");
        return responseCode;
    }

    private String doPOSTPUT(String strURL, String accessToken, String body, boolean put) {

        String strResponds = null;
        String authString = null;
        if (this.tokenType != null) {
            authString = StringUtils.capitalize(tokenType) + " " + accessToken;
        }

        try {

            URL url = new URL(strURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(Constants.CONNECT_TIMEOUT);
			conn.setReadTimeout(Constants.CONNECT_TIMEOUT);
            conn.setRequestMethod(HttpConstants.METHOD_POST);
            if (put) {
                conn.setRequestMethod(HttpConstants.METHOD_PUT);
            }
            conn.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
            conn.setRequestProperty(AUTHORIZATION, authString);
            conn.setRequestProperty(CHARSET, UTF_8);

            byte[] postDataBytes = body.getBytes();
            conn.setRequestProperty("Content-Length", Integer.toString(postDataBytes.length));
            conn.setDoOutput(true);

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.write(postDataBytes);
            os.flush();
            os.close();

            // Check response code
            int responeseCode = conn.getResponseCode();
            if (responeseCode != 200) {
                log.debug("Unauthorized! responeseCode = {}", responeseCode);
                return String.valueOf(responeseCode);
            }
            strResponds = Integer.toString(responeseCode);
            // Get response value

            conn.disconnect();

        } catch (IOException | IllegalArgumentException | SecurityException | IllegalStateException | NullPointerException e) {
            log.error(e.getMessage());
        }

        return strResponds;

    }

    private String myEDCTransactionBeanToJSON(MyEDCTransaction transaction) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONArray fieldValues = new JSONArray();

        Map<String, String> formFieldsValue = transaction.getProperties();
        // Loop over the values on the object to populate the JsonArray with the
        // JSONObject
        for (Map.Entry<String, String> item : formFieldsValue.entrySet()) {
            // JSONObject which contains type=FieldValue, id=<eloqua fieldId>,
            // value=<updated value>
            JSONObject fieldDefinition = new JSONObject();

            switch (item.getKey()) {
            case "uniqueCode":
                fieldDefinition.put("id", this.config.myEDCTransUniqueCode());
                fieldDefinition.put(VALUE, item.getValue());
                break;
            case "externalID":
                fieldDefinition.put("id", this.config.myEDCTransExternalID());
                fieldDefinition.put(VALUE, item.getValue());
                break;
            case "timeStamp":
                fieldDefinition.put("id", this.config.myEDCTransTimeStamp());
                fieldDefinition.put(VALUE, item.getValue());
                break;
            case "aemPath":
                fieldDefinition.put("id", this.config.myEDCTransAemPath());
                fieldDefinition.put(VALUE, item.getValue());
                break;
            case "counter":
                fieldDefinition.put("id", this.config.myEDCTransCounter());
                fieldDefinition.put(VALUE, item.getValue());
                break;
            case "email":
                fieldDefinition.put("id", this.config.myEDCEmail());
                fieldDefinition.put(VALUE, item.getValue());
                break;
            case "trafficSrc":
                fieldDefinition.put("id", this.config.myEDCTrafficSrc());
                fieldDefinition.put(VALUE, item.getValue());
                break;
            case "partnersCASL":
                fieldDefinition.put("id", this.config.myEDCPartnersCASL());
                fieldDefinition.put(VALUE, item.getValue());
                break;
            default:
                break;
            }

            if (fieldDefinition.length() > 0) {
                fieldValues.put(fieldDefinition);
            }
        }

        jsonObject.put(FIELD_VALUES, fieldValues);
        jsonObject.put("type", "CustomObjectData");

        return jsonObject.toString();
    }

    private MyEDCTransaction populateMyEDCTransactionBean(Map<String, String> transactionProps) {

        MyEDCTransaction transaction = new MyEDCTransaction();

        String configMyEDCTransUniqueCode = this.config.myEDCTransUniqueCode();
        String configMyEDCTransExternalID = this.config.myEDCTransExternalID();
        String configMyEDCTransTimeStamp = this.config.myEDCTransTimeStamp();
        String configMyEDCTransAemPath = this.config.myEDCTransAemPath();
        String configMyEDCTransCounter = this.config.myEDCTransCounter();
        String configMyEDCEmail = this.config.myEDCEmail();
        String configMyEDCTrafficSrc = this.config.myEDCTrafficSrc();
        String configMyEDCPartnersCASL = this.config.myEDCPartnersCASL();

        if (configMyEDCTransUniqueCode != null) {
            transaction.setUniqueCode(transactionProps.get(configMyEDCTransUniqueCode));
        }

        if (configMyEDCTransExternalID != null) {
            transaction.setExternalID(transactionProps.get(configMyEDCTransExternalID));
        }

        if (configMyEDCTransTimeStamp != null) {
            transaction.setTimeStamp(transactionProps.get(configMyEDCTransTimeStamp));
        }

        if (configMyEDCTransAemPath != null) {
            transaction.setAemPath(transactionProps.get(configMyEDCTransAemPath));
        }

        if (configMyEDCTransCounter != null) {
            transaction.setCounter(transactionProps.get(configMyEDCTransCounter));
        }
        if (configMyEDCEmail != null) {
            transaction.setEmail(transactionProps.get(configMyEDCEmail));
        }
        if (configMyEDCTrafficSrc != null) {
            transaction.setTrafficSrc(transactionProps.get(configMyEDCTrafficSrc));
        }
        if (configMyEDCPartnersCASL != null) {
            transaction.setPartnersCASL(transactionProps.get(configMyEDCPartnersCASL));
        }
        return transaction;

    }

    private EloquaContact populateContactBean(Map<String, String> contactProps) { // NOSONAR

        EloquaContact contact = new EloquaContact();

        String configCompanyNameC = this.config.companyName();
        String configEmailAddress = this.config.emailAddress();
        String configFirstName = this.config.firstName();
        String configLanguage = this.config.language();
        String configLastName = this.config.lastName();
        String configTitle = this.config.title();
        String configTradeStatus = this.config.tradeStatus();
        String configCompanyProvince = this.config.companyProvince();
        String configPainPoint = this.config.painPoint();
        String configIndustry = this.config.industry();
        String configCompanyCountry = this.config.companyCountry();
        String configAnnualSales = this.config.annualSales();
        String configGUID = this.config.GUID();
        String configCompanyAddress1 = this.config.companyAddress1();
        String configCompanyAddress2 = this.config.companyAddress2();
        String configCompanyCity = this.config.companyCity();
        String configMainPhone = this.config.mainPhone();
        String configEmployees = this.config.employees();
        String configCompanyPostal = this.config.companyPostal();
        String configMarketsInt = this.config.marketsInt();
        String configKnowledgeCustomerStage = this.config.knowledgeCustomerStage();
        String configPAUFlag = this.config.PAUFlag();
        String configPAUDate = this.config.PAUDate();
        String configCoreCustomer = this.config.coreCustomer();
        // Gated Lead Gen Form for Campaigns
        String coMainPhoneExt = this.config.coMainPhoneExt();
        String configFiLoan = this.config.fiLoan();
        String configCovidWC = this.config.covidWC();
        String configSalesTiming = this.config.salesTiming();
        String configNonCDNCurrencyExperience = this.config.nonCDNCurrencyExperience();
        String configFxContractExperience = this.config.fxContractExperience();
        String configFiName = this.config.fiName();
        String configFiContactRole = this.config.fiContactRole();
        String configFiClientAnnualSales = this.config.fiClientAnnualSales();
        String configCustomerField1 = this.config.customerField1();
        String configCustomerField2 = this.config.customerField2();
        String configCustomerField3 = this.config.customerField3();
        String configCustomerField4 = this.config.customerField4();
        String configCustomerField5 = this.config.customerField5();
        String configCustomerField6 = this.config.customerField6();
        String configCustomerField7 = this.config.customerField7();
        String configCustomerField8 = this.config.customerField8();
        String configCustomerField9 = this.config.customerField9();
        String configCustomerField10 = this.config.customerField10();
        String configITMValue = this.config.itmValue();
        String configITMOther = this.config.itmOther();

        if (configCompanyNameC != null) {
            contact.setCompanyName(contactProps.get(configCompanyNameC));
        }

        if (configEmailAddress != null) {
            contact.setEmailAddress(contactProps.get(configEmailAddress));
        }

        if (configFirstName != null) {
            contact.setFirstName(contactProps.get(configFirstName));
        }

        if (configLanguage != null) {
            contact.setLanguage(contactProps.get(configLanguage));
        }

        if (configLastName != null) {
            contact.setLastName(contactProps.get(configLastName));
        }

        if (configTitle != null) {
            contact.setTitle(contactProps.get(configTitle));
        }

        if (configTradeStatus != null) {
            contact.setTradeStatus(contactProps.get(configTradeStatus));
        }

        if (configCompanyProvince != null) {
            contact.setCompanyProvince(contactProps.get(configCompanyProvince));
        }

        if (configPainPoint != null) {
            contact.setPainPoint(contactProps.get(configPainPoint));
        }

        if (configIndustry != null) {
            contact.setIndustry(contactProps.get(configIndustry));
        }

        if (configCompanyCountry != null) {
            contact.setCompanyCountry(contactProps.get(configCompanyCountry));
        }

        if (configAnnualSales != null) {
            contact.setAnnualSales(contactProps.get(configAnnualSales));
        }

        if (configGUID != null) {
            contact.setGUID(contactProps.get(configGUID));
        }

        if (configCompanyAddress1 != null) {
            contact.setCompanyAddress1(contactProps.get(configCompanyAddress1));
        }

        if (configCompanyAddress2 != null) {
            contact.setCompanyAddress2(contactProps.get(configCompanyAddress2));
        }

        if (configCompanyCity != null) {
            contact.setCompanyCity(contactProps.get(configCompanyCity));
        }

        if (configMainPhone != null) {
            contact.setMainPhone(contactProps.get(configMainPhone));
        }

        if (configEmployees != null) {
            contact.setEmployees(contactProps.get(configEmployees));
            // FORM 3330 uses employees1 as field name
            contact.setEmployees1(contactProps.get(configEmployees));
        }

        if (configCompanyPostal != null) {
            contact.setCompanyPostal(contactProps.get(configCompanyPostal));
        }

        if (configMarketsInt != null) {
            contact.setMarketsInt(contactProps.get(configMarketsInt));
            // FORM 3330 uses marketsInt-large as field name
            // Must remove the "-" so we used it as valid method name
            contact.setMarketsIntlarge(contactProps.get(configMarketsInt));
        }

        if (configKnowledgeCustomerStage != null) {
            contact.setKnowledgeCustomerStage(contactProps.get(configKnowledgeCustomerStage));
        }

        if (configPAUFlag != null) {
            contact.setPAUFlag(contactProps.get(configPAUFlag));
        }

        if (configPAUDate != null) {
            contact.setPAUDate(contactProps.get(configPAUDate));
        }

        if (configCoreCustomer != null) {
            contact.setCoreCustomer(contactProps.get(configCoreCustomer));
        }

        // Gated Lead Gen Form for Campaigns
        if (coMainPhoneExt != null) {
            contact.setCoMainPhoneExt(contactProps.get(coMainPhoneExt));
        }

        if (configFiLoan != null) {
            contact.setFiLoan(contactProps.get(configFiLoan));
        }
        
        if (configCovidWC != null) {
            contact.setCovidWC(contactProps.get(configCovidWC));
        }

        if (configSalesTiming != null) {
            contact.setSalesTiming(contactProps.get(configSalesTiming));
        }

        if (configNonCDNCurrencyExperience != null) {
            contact.setNonCDNCurrencyExperience(contactProps.get(configNonCDNCurrencyExperience));
        }

        if (configFxContractExperience != null) {
            contact.setFxContractExperience(contactProps.get(configFxContractExperience));
        }

        if (configFiName != null) {
            contact.setFiName(contactProps.get(configFiName));
        }

        if (configFiContactRole != null) {
            contact.setFiContactRole(contactProps.get(configFiContactRole));
        }

        if (configFiClientAnnualSales != null) {
            contact.setFiClientAnnualSales(contactProps.get(configFiClientAnnualSales));
        }

        if (configCustomerField1 != null) {
            contact.setCustomerField1(contactProps.get(configCustomerField1));
        }

        if (configCustomerField2 != null) {
            contact.setCustomerField2(contactProps.get(configCustomerField2));
        }

        if (configCustomerField3 != null) {
            contact.setCustomerField3(contactProps.get(configCustomerField3));
        }

        if (configCustomerField4 != null) {
            contact.setCustomerField4(contactProps.get(configCustomerField4));
        }

        if (configCustomerField5 != null) {
            contact.setCustomerField5(contactProps.get(configCustomerField5));
        }

        if (configCustomerField6 != null) {
            contact.setCustomerField6(contactProps.get(configCustomerField6));
        }

        if (configCustomerField7 != null) {
            contact.setCustomerField7(contactProps.get(configCustomerField7));
        }

        if (configCustomerField8 != null) {
            contact.setCustomerField8(contactProps.get(configCustomerField8));
        }

        if (configCustomerField9 != null) {
            contact.setCustomerField9(contactProps.get(configCustomerField9));
        }

        if (configCustomerField10 != null) {
            contact.setCustomerField10(contactProps.get(configCustomerField10));
        }

        if (configITMValue != null) {
            contact.setITMValue(contactProps.get(configITMValue));
        }

        if (configITMOther != null) {
            contact.setITMOther(contactProps.get(configITMOther));
        }

        return contact;

    }

    private EloquaDocHistory populateDocHistoryBean(Map<String, String> contactProps) {

        EloquaDocHistory docHistory = new EloquaDocHistory();

        String configDocHistoryEmailAddressWithDocID = this.config.docHistoryEmailAddressWithDocID();
        String configDocHistoryEmailAddress = this.config.docHistoryEmailAddress();
        String configDocHistoryDocID = this.config.docHistoryDocID();
        String configDocHistoryTimesAccessed = this.config.docHistoryTimesAccessed();

        if (configDocHistoryEmailAddressWithDocID != null) {
            docHistory.setEmailAddressWithDocID(contactProps.get(configDocHistoryEmailAddressWithDocID));
        }
        if (configDocHistoryEmailAddress != null) {
            docHistory.setEmailAddress(contactProps.get(configDocHistoryEmailAddress));
        }
        if (configDocHistoryDocID != null) {
            docHistory.setDocID(contactProps.get(configDocHistoryDocID));
        }
        if (configDocHistoryTimesAccessed != null) {
            docHistory.setTimesAccessed(contactProps.get(configDocHistoryTimesAccessed));
        }

        return docHistory;
    }

    private String getEloquaBaseURLFromCache() {

        return (String) getCacheObject("eloqua_baseURL");
    }

    private String getEloquaBaseRESTURLFromCache() {

        return (String) getCacheObject("eloqua_baseRESTURL");
    }

    private EloquaToken getEloquaTokenFromCache() {
        EloquaToken eloquaToken = (EloquaToken) getCacheObject(ELOQUA_TOKEN);
        if (eloquaToken == null) {
            log.debug("getEloquaTokenFromCache no token on cache");
            Cache jsonCache = CacheManager.getCacheInfo(EloquaConnectionManagerConstants.CacheObjects.ELOQUA_TOKEN);
            if (jsonCache != null) {
                eloquaToken = EloquaServiceImpl.parseEloquaToken(jsonCache.toString());
                int expireSeconds = eloquaToken.getExpiresIn();
                CacheManager.putCacheInfo(ELOQUA_TOKEN, eloquaToken, (expireSeconds * 1000));
                log.debug("JsonToken Found on JsonCache");
            }
        }
        return eloquaToken;
    }

    private Object getCacheObject(String key) {

        Object result = null;
        if (key != null && !key.isEmpty()) {
            Cache cache = CacheManager.getCacheInfo(key);
            if (cache != null) {
                result = cache.getValue();
            }
        }

        return result;
    }

    public String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date now = new Date();
        return sdfDate.format(now);
    }

    @Override
    public String getNewsletterFormName() {
        return config.newsletterFormName();
    }

    @Override
    public String getSiteId() {
        return config.siteId();
    }

    @Override
    public String getFormSubmitURL() {
        return config.formSubmitURL();
    }

    @Override
    public String getUrlToGetBaseUrl() {
        return config.urlToGetBaseUrl();
    }

    @Override
    public String getEndPointFallBack() {
        return config.endPointFallBack();
    }

    @Override
    public String getTokenUrl() {
        return config.tokenUrl();
    }

    // ----Get methods for Schedule A Call configurations----

    @Override
    public String getScheduleCallFormName() {
        return config.scheduleCallFormName();
    }

    @Override
    public String getScheduleCallFormId() {
        return config.scheduleCallFormId();
    }

    // ----Get methods for Contact Us configurations----

    @Override
    public String getContactUsFormName() {
        return config.contactUsFormName();
    }

    @Override
    public String getContactUsFormId() {
        return config.contactUsFormId();
    }

    @Override
    public String getMSTLFormName() {
        return config.mstlFormName();
    }

    @Override
    public String getMSTLFormId() {
        return config.mstlFormId();
    }

    @Override
    public String getProductInquiryFormName() {
        return config.productInquiryFormName();
    }

    // ----Get methods for Matchmaking configurations----

    @Override
    public String getMatchmakingFormName() {
        return config.matchmakingFormName();
    }

    @Override
    public String getSubscriptionsFormName() {
        return config.subscriptionsFormName();
    }

    @Override
    public String getSubscriptionsFormId() {
        return config.subscriptionsFormId();
    }

    // ----Get methods for Export Help Request configurations----

    @Override
    public String getExportHelpRequestFormName() {
        return config.exportHelpRequestFormName();
    }

    @Override
    public String getExportHelpRequestFormId() {
        return config.exportHelpRequestFormId();
    }

    // ----Get methods for Export Help Request configurations----

    @Override
    public String getAPSGFormName() {
        return config.APSGFormName();
    }

    @Override
    public String getAPSGFormId() {
        return config.APSGFormId();
    }

    @Override
    public String getTAPFormName() {
        return config.TAPFormName();
    }

    @Override
    public String getTAPFormId() {
        return config.TAPFormId();
    }

    // ----Get methods for Broker Registration Form Request configurations----

    @Override
    public String getBrokerRegistrationFormName() {
        return config.BrokerRegistrationFormName();
    }

    @Override
    public String getBrokerRegistrationFormId() {
        return config.BrokerRegistrationFormId();
    }

    // ----Get methods for Knowledge Costumer configurations----
    @Override
    public String getKnowledgeCostumerFormName() {
        return config.knowledgeCostumerFormName();
    }

    // ----Get methods for inList Service Provider Intake form configurations----
    @Override
    public String getInListServiceProviderIntakeFormName() {
        return config.inListServiceProviderIntakeFormName();
    }

    @Override
    public String getInListServiceProviderIntakeFormId() {
        return config.inListServiceProviderIntakeFormId();
    }

    // ----Get methods for EH form configurations----
    @Override
    public String getEhFormName() {
        return config.ehFormName();
    }

    @Override
    public String getEhFormId() {
        return config.ehFormId();
    }

    @Override
    public String getEhLookupIdVisitor() {
        return config.ehLookupIdVisitor();
    }

    @Override
    public String getEhLookupIdPrimary() {
        return config.ehLookupIdPrimary();
    }

    // ----Get methods for Propressive Profiling configurations----

    @Override
    public String getProgressProfilingFormName() {
        return config.progressProfilingFormName();
    }

    @Override
    public String getProgressProfilingFormId() {
        return config.progressProfilingFormId();
    }

    @Override
    public String getProgressProfilingServiceIDCompanyName() {
        return config.progressProfilingServiceIDCompanyName();
    }

    @Override
    public String getProgressProfilingServiceIDUserName() {
        return config.progressProfilingServiceIDUserName();
    }

    @Override
    public String getProgressProfilingServiceIDPassword() {
        return config.progressProfilingServiceIDPassword();
    }

    @Override
    public String getOath2ClientID() {
        return config.oath2ClientID();
    }

    @Override
    public String getOath2ClientSecurity() {
        return config.oath2ClientSecurity();
    }

    @Override
    public String getCDOUserInfo() {
        return config.CDOUserInfo();
    }

    @Override
    public String getCDODocHistory() {
        return config.CDODocHistory();
    }

    @Override
    public String getCompanyName() {
        return config.companyName();
    }

    @Override
    public String getEmailAddress() {
        return config.emailAddress();
    }

    @Override
    public String getFirstName() {
        return config.firstName();
    }

    @Override
    public String getLanguage() {
        return config.language();
    }

    @Override
    public String getLastName() {
        return config.lastName();
    }

    @Override
    public String getTitle() {
        return config.title();
    }

    @Override
    public String getTradeStatus() {
        return config.tradeStatus();
    }

    @Override
    public String getCompanyProvince() {
        return config.companyProvince();
    }

    @Override
    public String getPainPoint() {
        return config.painPoint();
    }

    @Override
    public String getIndustry() {
        return config.industry();
    }

    @Override
    public String getCompanyCountry() {
        return config.companyCountry();
    }

    @Override
    public String getAnnualSales() {
        return config.annualSales();
    }

    @Override
    public String getGUID() {
        return config.GUID();
    }

    @Override
    public String getCompanyAddress1() {
        return config.companyAddress1();
    }

    @Override
    public String getCompanyAddress2() {
        return config.companyAddress2();
    }

    @Override
    public String getCompanyCity() {
        return config.companyCity();
    }

    @Override
    public String getMainPhone() {
        return config.mainPhone();
    }

    @Override
    public String getEmployees() {
        return config.employees();
    }

    @Override
    public String getCompanyPostal() {
        return config.companyPostal();
    }

    @Override
    public String getMarketsInt() {
        return config.marketsInt();
    }

    @Override
    public String getKnowledgeCustomerStage() {
        return config.knowledgeCustomerStage();
    }

    @Override
    public String getDocHistoryTimesAccessed() {
        return config.docHistoryTimesAccessed();
    }

    @Override
    public String getDocHistoryEmailAddressWithDocID() {
        return config.docHistoryEmailAddressWithDocID();
    }

    @Override
    public String getDocHistoryEmailAddress() {
        return config.docHistoryEmailAddress();
    }

    @Override
    public String getDocHistoryDocID() {
        return config.docHistoryDocID();
    }

    @Override
    public String getPAUFlag() {
        return config.PAUFlag();
    }

    @Override
    public String getPAUDate() {
        return config.PAUDate();
    }

    @Override
    public String getCoreCustomer() {
        return config.coreCustomer();
    }

    // Simple Check if record exists on MyEDC
    @Override
    public Boolean checkMyEDCAccountExistsByEMailId(String emailId) {
        // Get base URL
        String baseURL = getEloquaBaseURL(false);
        // Get Eloqua access_token
        String accessToken = getAccessToken(configOath2ClientID, configOath2ClientSecurity, false);
        if (baseURL != null && !baseURL.endsWith("/")) {
            baseURL += "/";
        }
        String url = baseURL + CDO_ENDPOINT + config.getMyEDCProfileCDOId() + "/instances?search='name=" + emailId
                + "'";
        // Get the response
        String jsonString = doRESTfulGet(url, accessToken);
        // Default no records
        int total = 0;
        try {
            JSONObject json = new JSONObject(jsonString);
            if (json.has(TOTAL)) {
                total = json.getInt(TOTAL);
            }
        } catch (JSONException e) {
            log.debug("Unable to parse json {} Full error {}", jsonString, e.getMessage());
        }
        // Should not, but sometimes there's more than 1 record.
        return total > 0;
    }

    public String getMyEDCTransForm() {
        return config.MyEDCTransForm();
    }

    @Override
    public String getMyEDCTransFormName() {
        return config.myEDCTransFormName();
    }

    @Override
    public String getMyEDCTransUniqueCode() {
        return config.myEDCTransUniqueCode();
    }

    @Override
    public String getMyEDCTransExternalID() {
        return config.myEDCTransExternalID();
    }

    @Override
    public String getMyEDCTransTimeStamp() {
        return config.myEDCTransTimeStamp();
    }

    @Override
    public String getMyEDCTransAemPath() {
        return config.myEDCTransAemPath();
    }

    @Override
    public String getMyEDCTransCounter() {
        return config.myEDCTransCounter();
    }

    @Override
    public String getMyEDCProfileCDOId() {
        return config.getMyEDCProfileCDOId();
    }

    @Override
    public String getMyEDCEmail() {
        return config.myEDCEmail();
    }

    @Override
    public String getMyEDCTrafficSrc() {
        return config.myEDCTrafficSrc();
    }

    @Override
    public String getFirstNameFieldId() {
        String[] arr = fieldMappingConfigService.getFirstNameMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getLastNameFieldId() {
        String[] arr = fieldMappingConfigService.getLastNameMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getJobTitleFieldId() {
        String[] arr = fieldMappingConfigService.getTitleMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getTradeStatusFieldId() {
        String[] arr = fieldMappingConfigService.getTradeStatusMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getCompanyIdFieldId() {
        String[] arr = fieldMappingConfigService.getCompanyNameMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getMainPhoneFieldId() {
        String[] arr = fieldMappingConfigService.getMainPhoneMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getCompanyAddress1FieldId() {
        String[] arr = fieldMappingConfigService.getCompanyAddress1Map();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getCompanyCityFieldId() {
        String[] arr = fieldMappingConfigService.getCompanyCityMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getCompanyProvinceFieldId() {
        String[] arr = fieldMappingConfigService.getCompanyProvinceMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getCompanyPostalFieldId() {
        String[] arr = fieldMappingConfigService.getCompanyPostalMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getCompanyCountryFieldId() {
        String[] arr = fieldMappingConfigService.getCompanyCountryMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getAnnualSalesFieldId() {
        String[] arr = fieldMappingConfigService.getAnnualSalesMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getEmployeesFieldId() {
        String[] arr = fieldMappingConfigService.getEmployeesMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getIndustryFieldId() {
        String[] arr = fieldMappingConfigService.getIndustryMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getMarketsIntFieldId() {
        String[] arr = fieldMappingConfigService.getMarketsIntMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getPainPointFieldId() {
        String[] arr = fieldMappingConfigService.getPainPointMap();
        return FieldMapUtils.getEloquaId(arr);
    }
}