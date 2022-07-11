package com.edc.edcportal.core.appauth.services;

/**
 * Interface to Implement the AppAuth DAO Service Business Logic to process the
 * request received from AEM
 *
 */
public interface AppAuthDAOService {

    /**
     * appAuthAccountProvisioned
     * 
     * @param externalUserID userID in the AppAuth user profile
     * @return AppAuth account exists
     */
    boolean appAuthAccountProvisioned(String externalUserID);

    /**
     * createAppAuthAccount
     * 
     * @param externalUserID userID in the AppAuth user profile
     * @return AppAuth register account success status
     */
    boolean createAppAuthAccount(String externalUserID);

}
