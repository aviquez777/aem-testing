package com.edc.edcweb.core.myedc.eloqua.services;

import com.edc.edcweb.core.exception.EDCEloquaSystemException;
import com.edc.edcweb.core.myedc.eloqua.pojo.EloquaUserProfileDO;
import com.edc.edcweb.core.service.EloquaService;

public interface EloquaDAOService {

    /**
     * Gets the EloquaUserProfileDO using unique id
     *
     * @param externalId from the header
     * @return EloquaUserProfileDO, empty if no record found
     */
    EloquaUserProfileDO getUserProfileByExternalId(String externalId) throws EDCEloquaSystemException;

    /**
     * Gets the EloquaProresiveProfiling using email and pre-populates the object with data if found
     * Only for new registration
     *
     * @param externalId from the header
     * @return Updated EloquaUserProfileDO with PP info, empty if no record found
     */

    /**
     * Convenience method to get the service instance,from within this class
     * @return EloquaConfigService
     */
    EloquaService getEloquaService();

}
