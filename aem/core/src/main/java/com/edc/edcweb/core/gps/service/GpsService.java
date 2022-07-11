package com.edc.edcweb.core.gps.service;

/**
 * Interface to Implement the Gps Configuration Service Properties and values
 * required to successfully make API calls to Gps
 *
 */

public interface GpsService {

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
    String getGpsProxyUrl();

    /**
     * Get the GpsBaseEndpoint URL which is Environment dependent
     * 
     * @return String with the GpsBaseEndpoint
     */
    String getGpsBaseEndpoint();

    /**
     * Get the OCPGpssubscriptionKey which needs to be sent every API request
     * 
     * @return String with OCPGpssubscriptionKey
     */
    String getOCPGpsSubKey();
}
