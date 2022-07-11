package com.edc.edcportal.core.arkadin.services;

/**
 * Interface to Implement the Arkadin Configuration Service Properties and
 * values required to successfully make API calls to Arkadin
 *
 */

public interface ArkadinConfigService {

    /**
     * Get Token URL to make the request
     * 
     * @return String with URL
     */
    String getAPIUrl();

    /**
     * Get LASCmd value, for "regular calls"
     * 
     * @return
     */
    String getLASCmd();

    /**
     * Get getStudioShowRegistrationLASCmd value, for "registation calls"
     * 
     * @return
     */
    String getStudioShowRegistrationLASCmd();

    /**
     * Get the clientID credential to send on the Body Request to get the Token
     * 
     * @return String with clientId
     */
    String getAPIUserAuthCode();

    /**
     * Get the ClientSecret credential to send on the Body Request to get the Token
     * 
     * @return String with ClientSecret
     */
    String getAPIUserCredentials();

    /**
     * Get the ShowBaseUrl as it varies per environment
     * 
     * @return String with ShowBaseUrl
     */
    String getShowBaseUrl();
}