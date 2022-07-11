package com.edc.edcportal.core.eloqua.services;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.json.JSONException;

import com.edc.edcportal.core.eloqua.pojo.EloquaUserProfileDO;
import com.edc.edcportal.core.exception.EDCEloquaSystemException;
import com.edc.edcportal.core.services.FieldMappingConfigService;
import com.edc.edcportal.core.transactionhistory.services.THConfigService;

public interface EloquaDAOService {

    /**
     * create the record just with the headers from Shibboleth
     * 
     * @param eloquaUserProfileDO
     * @param Shibboleth          headersForEloqua (mapped to field Id)
     * @return
     */
    EloquaUserProfileDO createUserProfile(EloquaUserProfileDO eloquaUserProfileDO, Map<String, String> headersForEloqua)
            throws EDCEloquaSystemException;

    /**
     * Gets the EloquaUserProfileDO using unique id
     * 
     * @param externalId from the header
     * @return EloquaUserProfileDO, empty if no record found
     */
    EloquaUserProfileDO getUserProfileByExternalId(String externalId) throws EDCEloquaSystemException;

    /**
     * Gets the EloquaProresiveProfiling using email and pre-populates the object
     * with data if found Only for new registration
     * 
     * @param externalId from the header
     * @return Updated EloquaUserProfileDO with PP info, empty if no record found
     * @throws JSONException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    Map<String, String> prePopulatePPDataByEMailId(Map<String, String> formFields, String email,
            FieldMappingConfigService fieldMappingConfigService) throws EDCEloquaSystemException;

    /**
     * Updates the DAO Object with the selected profile & returns true if successful
     * 
     * @param eloquaUserProfileDO
     * @param profileType
     * @return true if successful, false otherwise
     */
    Boolean updateUserProfile(EloquaUserProfileDO eloquaUserProfileDO) throws EDCEloquaSystemException;

    /**
     * Gets the getTransactionHistoryByExternalId using unique id
     * 
     * @param externalId from the header
     * @return Set<String> with the paths duplicates removed, empty if no record
     *         found
     */
    Map<String, String> getTransactionHistoryByExternalId(String externalId) throws EDCEloquaSystemException;

    /**
     * Convenience method to get the service instance,from within this class
     * 
     * @return EloquaConfigService
     */
    EloquaConfigService getEloquaConfigService();
    
    

    /**
     * checkConsentRequestGating check if there's a record with the provided data
     * 
     * @param externalId
     * @param pagePath
     * @param partnersCode
     * @return true if record found, false otherwise
     * @throws EDCEloquaSystemException
     */
    boolean checkConsentRequestGating(String externalId, String pagePath, String partnersCode, THConfigService thConfigService)
            throws EDCEloquaSystemException;

}
