package com.edc.edcweb.core.apicommon.service;

/**
 * Interface to Implement the API Centralized Configuration Service Properties
 * and values required to successfully make API calls to LovApi
 *
 */

public interface ApiCommonConfigService {

    /**
     * Get Token URL to make the request
     * 
     * @return String with URL
     */
    String getTokenUrl();

    /**
     * Get the clientID credential to send on the Body Request to get the Token
     * 
     * @return String with clientId
     */
    String getClientId();

    /**
     * Get the ClientSecret credential to send on the Body Request to get the Token
     * 
     * @return String with ClientSecret
     */
    String getClientSecret();

    /**
     * Get the API Proxy server url (base server) the actual endpoint is specific
     * per API
     *
     * @return
     */
    String getProxyUrl();

    /**
     * Get the OCPAPIMsubscriptionKey this might be different dependning on
     * implementation
     *
     * @return
     */

    String getOCPSubscriptionKey();
}
