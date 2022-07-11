package com.edc.edcportal.core.appauth.services;

/**
 * Interface to Implement the AppAuth Configuration Service Properties and values
 * required to successfully make API calls to AppAuth
 *
 */

public interface AppAuthConfigService {

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
     * Get the Resource credential to send on the Body Request to get the Token
     * 
     * @return String with Resource URL
     */
    String getResource();

    /**
     * Get the Environment dependent URL to use for the API requests
     * 
     * @return URL
     */
    String getAppAuthProxyUrl();

    /**
     * Get the AppAuthBaseEndpoint URL which is Environment dependent
     * 
     * @return String with the AppAuthBaseEndpoint
     */
    String getAppAuthBaseEndpoint();

    /**
     * Get the OCPAppAuthsubscriptionKey which needs to be sent every API request
     * 
     * @return String with OCPAppAuthsubscriptionKey
     */
    String getOCPAppAuthsubscriptionKey();
}
