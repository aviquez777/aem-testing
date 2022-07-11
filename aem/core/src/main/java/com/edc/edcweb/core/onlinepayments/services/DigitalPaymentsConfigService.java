package com.edc.edcweb.core.onlinepayments.services;

public interface DigitalPaymentsConfigService {

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
     * Get the OCPsubscriptionKey which needs to be sent every API request
     * 
     * @return String with OCPsubscriptionKey
     */
    String getOCPSubscriptionKey();

    /**
     * Get the Resource credential to send on the Body Request to get the Token
     * 
     * @return String with Resource URL
     */
    String getDPResource();

    /**
     * Get the Environment dependent URL to use for the API requests
     * 
     * @return URL
     */
    String getDPProxyUrl();

    /**
     * Get the BaseEndpoint URL which is Environment dependent
     * 
     * @return String with the BaseEndpoint
     */
    String getDPBaseEndpoint();
}
