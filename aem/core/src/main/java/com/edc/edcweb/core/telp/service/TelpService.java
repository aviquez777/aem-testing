package com.edc.edcweb.core.telp.service;

/**
 * Interface to Implement the Telp Configuration Service Properties and values
 * required to successfully make API calls to Telp
 *
 */

public interface TelpService {

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
    String getTelpProxyUrl();

    /**
     * Get the TelpBaseEndpoint URL which is Environment dependent
     * 
     * @return String with the TelpBaseEndpoint
     */
    String getTelpBaseEndpoint();

    /**
     * Get the OCPTelpsubscriptionKey which needs to be sent every API request
     * 
     * @return String with OCPTelpsubscriptionKey
     */
    String getOCPTelpsubscriptionKey();
}
