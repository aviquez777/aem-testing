package com.edc.edcportal.core.arkadin.services;

import com.edc.edcportal.core.arkadin.pojo.ArkadinRegistrationDO;
import com.edc.edcportal.core.arkadin.pojo.ArkadinUserActivityPO;

/**
 * Interface to Implement the Arkadin DAO Service Business Logic to process the
 * request received from AEM
 *
 */
public interface ArkadinDAOService {

    /**
     * registerUserToWebinar
     * 
     * @param externalUserID userID in the Arkadin user profile
     * @param showKey        specific webinar identifier
     * @return ShowRegistrationPO with the Status and user identifier for this
     *         registration
     */
    ArkadinRegistrationDO registerUserToWebinar(String externalUserID, String showKey);

    /**
     * getUserRegistrationStatus
     * 
     * @param externalUserID userID in the Arkadin user profile
     * @param showKey        specific webinar identifier
     * @return ShowRegistrationPO with user identifier for this registration
     */
    ArkadinRegistrationDO getUserRegistrationStatus(String externalUserID, String showKey);

    /**
     * getUserActivity retrieves the list of shows and registrations for the user
     * with the externalUserID
     * 
     * @param externalUserID userID in the Arkadin user profile
     * @return ShowRegistrationPO object with the data
     */
    ArkadinUserActivityPO getUserActivity(String externalUserID);

}
