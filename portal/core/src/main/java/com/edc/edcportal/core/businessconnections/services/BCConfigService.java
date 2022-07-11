package com.edc.edcportal.core.businessconnections.services;

public interface BCConfigService {

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
    String getBCTokenProxyUrl();

    /**
     * Get the BCTokenBaseEndpoint URL which is Environment dependent
     * 
     * @return String with the BCTokenBaseEndpoint
     */
    String getBCTokenBaseEndpoint();

    /**
     * Get the OCPBCTokensubscriptionKey which needs to be sent every API request
     * 
     * @return String with OCPBCTokensubscriptionKey
     */
    String getOCPBCTokensubscriptionKey();
    
    /**
     * Get the Default SPA End point to use if the author does not override it from the dialog
     * 
     * @return String with SPA End point
     */
    String getSPAEndpoint();
}
