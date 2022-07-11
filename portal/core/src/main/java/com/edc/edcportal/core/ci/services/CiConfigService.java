package com.edc.edcportal.core.ci.services;

public interface CiConfigService {
    /**
     *  get the page's node with no language or URL
     * @return String with Node to build the URL
     */
    String getLandingSearchPageNode();

    /**
     *  get the page's node with no language or URL
     * @return String with Node to build the URL
     */
    String getSearchResultsPageNode();

    /**
     *  get the page's node with no language or URL
     * @return String with Node to build the URL
     */
    String getCompanyProfilePageNode();
    
    /**
     *  get the JSON Keys to build the Google Maps Link
     * @return String with the list
     */
    String getMapKeys();

    /**
     *  get the Google Maps Link to create the query
     * @return String with the list
     */
    String getMapBaseUrl();

    /**
     *  Get the Load more "page" count
     * @return integer with the value
     */
    int getPageCount();

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
    String getCIProxyUrl();

    /**
     * Get the CIBaseEndpoint URL which is Environment dependent
     * 
     * @return String with the CIBaseEndpoint
     */
    String getCIBaseEndpoint();

    /**
     * Get the OCPCIsubscriptionKey which needs to be sent every API request
     * 
     * @return String with OCPCIsubscriptionKey
     */
    String getOCPCIsubscriptionKey();

}
