package com.edc.edcweb.core.apim.services.impl;

import com.edc.edcweb.core.apim.ConstantsAPIM;
import com.edc.edcweb.core.apim.pojo.APIMTokenDO;
import com.edc.edcweb.core.apim.services.APIMDAOService;
import com.edc.edcweb.core.apim.services.APIMDAOServiceConfiguration;
import com.edc.edcweb.core.helpers.Cache;
import com.edc.edcweb.core.helpers.CacheManager;
import com.edc.edcweb.core.helpers.HTTPRequestUtil;
import com.edc.edcweb.core.helpers.HTTPResponseMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

@Component(immediate = true, service = APIMDAOService.class, configurationPid = "com.edc.edcweb.core.apim.services.impl.APIMDAOServiceImpl")

@Designate(ocd = APIMDAOServiceConfiguration.class)
public class APIMDAOServiceImpl implements APIMDAOService {

    private static final Logger log = LoggerFactory.getLogger(APIMDAOServiceImpl.class);
    APIMDAOServiceConfiguration configuration;
    private String config_accessTokenURL;
    private String config_clientId;
    private String config_clientSecret;
    private String config_grandType;
    private String config_resource;
    private String config_proxyURL;
    private String config_subscriptionKey;
    private String config_servicePointSupplierFilter;
    private String config_servicePointSupplierProfile;

    @Activate
    @Modified
    public void activate(APIMDAOServiceConfiguration config) {

        this.configuration = config;
        this.config_accessTokenURL = config.accessTokenURL();
        this.config_clientId = config.clientId();
        this.config_clientSecret = config.clientSecret();
        this.config_grandType = config.grandType();
        this.config_resource = config.resource();
        this.config_proxyURL = config.proxyURL();
        this.config_subscriptionKey = config.subscriptionKey();
        this.config_servicePointSupplierFilter = config.servicePointInListSupplierFilter();
        this.config_servicePointSupplierProfile = config.servicePointInListSupplierProfile();
    }

    /**
     * Get APIM Access Token object
     * 
     * @param boolean forceRefresh If true get token from APIM, otherwise retrieve
     *                from Cache first
     * @return APIMToken APIMToken Object
     */
    @Override
    public APIMTokenDO getAccessToken(boolean forceRefresh) {

        log.info("APIMDAOServiceImpl Method Name getAccessToken() [IN]");

        String strResponse = "";
        boolean cacheExpired = false;
        String access_token = "";

        // get token info from cache
        APIMTokenDO apimAccessToken = getAPIMTokenFromCache();
        if (apimAccessToken != null) {
            access_token = apimAccessToken.getAccessToken();
        }
        if (forceRefresh || apimAccessToken == null || access_token == null || access_token.isEmpty()) {
            cacheExpired = true;
        }

        // if cache expired or not exist, retrieve again, and update cache
        if (cacheExpired) {
            log.info("Current apim cached token expired. ");
            HashMap<String, String> reqParameters = new HashMap<>();
            apimAccessToken = null;

            // HEader Parameter
            reqParameters.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

            // Request Arguments
            HashMap<String, String> reqArguments = new HashMap<>();
            reqArguments.put("client_id", config_clientId);
            reqArguments.put("client_secret", config_clientSecret);
            reqArguments.put("grant_type", config_grandType);
            reqArguments.put("resource", config_resource);

            HTTPResponseMessage respMessage = HTTPRequestUtil.doHTTPPost(config_accessTokenURL, reqParameters,
                    reqArguments);
            if (respMessage.getResponseCode() != 200) {
                log.error("Fail to get APIM token. Error Code: {}. Message: {}", respMessage.getResponseCode(),
                        respMessage.getResponseMessage());
            } else {
                strResponse = respMessage.getResponseBody();
                apimAccessToken = parseAPIMToken(strResponse);
                access_token = apimAccessToken.getAccessToken();

                // update cache, reduced a minute to expire cache first.
                long expireSeconds = apimAccessToken.getExpiresIn();
                CacheManager.putCacheInfo("apim_token", apimAccessToken, (expireSeconds * 1000));
                String tokenSubst = access_token.substring(0, 10);
                log.info("New token cached. Token value = '{}***** '", tokenSubst);
            }
        } else {
            log.info("Reusing apim cached token");
        }

        log.info("APIMDAOServiceImpl Method Name getAccessToken() [OUT]");
        return apimAccessToken;
    }

    @Override
    public String getConfigProxyURL() {
        return config_proxyURL;
    }

    @Override
    public String getConfigSubscriptionKey() {
        return config_subscriptionKey;
    }

    /**
     * Parse JSON response and save to APIMToken Object
     * 
     * @param String respone from APIM service
     * @return APIMToken APIMToken Object
     */
    private APIMTokenDO parseAPIMToken(String jsonResponse) {

        APIMTokenDO apimToken = new APIMTokenDO();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(jsonResponse);

            JsonNode tokenNode = jsonNode.findValue(ConstantsAPIM.APIMProperties.APIM_JSON_PROP_ACCESSTOKEN);
            String access_Token = tokenNode.textValue();
            apimToken.setAccessToken(access_Token);

            JsonNode tokenType = jsonNode.findValue(ConstantsAPIM.APIMProperties.APIM_JSON_PROP_TOKENTYPE);
            String token_type = tokenType.textValue();
            apimToken.setTokenType(token_type);

            JsonNode expireIn = jsonNode.findValue(ConstantsAPIM.APIMProperties.APIM_JSON_PROP_EXPIN);
            int expires_in = Integer.parseInt(expireIn.textValue());
            apimToken.setExpiresIn(expires_in);

            JsonNode extExpireIn = jsonNode.findValue(ConstantsAPIM.APIMProperties.APIM_JSON_PROP_EXTEXPIN);
            int ext_expires_in = Integer.parseInt(extExpireIn.textValue());
            apimToken.setExtExpiresIn(ext_expires_in);

            JsonNode expireOn = jsonNode.findValue(ConstantsAPIM.APIMProperties.APIM_JSON_PROP_EXPON);
            int expires_on = Integer.parseInt(expireOn.textValue());
            apimToken.setExpiresOn(expires_on);

            JsonNode notBefore = jsonNode.findValue(ConstantsAPIM.APIMProperties.APIM_JSON_PROP_NOTBEFORE);
            int not_before = Integer.parseInt(notBefore.textValue());
            apimToken.setNotBefore(not_before);

            JsonNode resourceNode = jsonNode.findValue(ConstantsAPIM.APIMProperties.APIM_JSON_PROP_RESOURCE);
            String resource = resourceNode.textValue();
            apimToken.setResource(resource);

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return apimToken;

    }

    private APIMTokenDO getAPIMTokenFromCache() {

        return (APIMTokenDO) getCacheObject("apim_token");
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

    @Override
    public String getAccessTokenURL() {
        return configuration.accessTokenURL();
    }

    @Override
    public String getClientId() {
        return configuration.clientId();
    }

    @Override
    public String getClientSecret() {
        return configuration.clientSecret();
    }

    @Override
    public String getGrandType() {
        return configuration.grandType();
    }

    @Override
    public String getResource() {
        return configuration.resource();
    }

    @Override
    public String getProxyURL() {
        return configuration.proxyURL();
    }

    @Override
    public String getSubscriptionKey() {
        return configuration.subscriptionKey();
    }

    @Override
    public String getServicePointInListSupplierFilter() {
        return configuration.servicePointInListSupplierFilter();
    }

    @Override
    public String getServicePointInListSupplierProfile() {
        return configuration.servicePointInListSupplierProfile();
    }

    public String getConfig_servicePointSupplierProfile() {
        return config_servicePointSupplierProfile;
    }

    public String getConfig_servicePointSupplierFilter() {
        return config_servicePointSupplierFilter;
    }

}
