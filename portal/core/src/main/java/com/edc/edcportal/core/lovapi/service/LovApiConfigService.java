package com.edc.edcportal.core.lovapi.service;

/**
 * Interface to Implement the LovApi Configuration Service Properties and values
 * required to successfully make API calls to LovApi
 *
 */

public interface LovApiConfigService {

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
    String getLovApiProxyUrl();

    /**
     * Get the OCPLovApisubscriptionKey which needs to be sent every API request
     * 
     * @return String with OCPLovApisubscriptionKey
     */
    String getOCPLovApisubscriptionKey();

    /**
     * Get the Base LOV end point URL which is Environment dependent
     * 
     * @return String with the getBaseEndpoint
     */
    String getBaseEndpoint();
    
    /** Configurations below are LOV dependent **/

    /**
     * Get the Generic LOV end point URL which is Environment / LOV dependent
     * 
     * @return String with the getGenericEndpoint
     */
    String getGenericEndpoint();

}
