package com.edc.edcportal.core.apim.services;

import com.edc.edcportal.core.apim.pojo.InfoPaymentDO;
import com.edc.edcportal.core.apim.pojo.InfoPaymentStatusDO;
import com.edc.edcportal.core.exception.EDCAPIMSystemException;

/**
 * Interface to Implement the APIM DAO Service Business Logic to process the
 * request received from AEM
 *
 */
public interface ApimDAOService {

    /**
     * getInfoPaymentStatus F
     * 
     * @param externalId related to the record
     * @return the Status as String
     */
    InfoPaymentStatusDO getInfoPaymentStatus(String externalId) throws EDCAPIMSystemException;

    /**
     * submitInfoPayment get the EloquaUserProfileDO, update the InfoPaymentDO,
     * submit to API and return result
     * 
     * @param infoPaymentDO             Infopayment Data Object
     * @return Boolean true if successful, false otherwise
     */
    Boolean submitInfoPayment(InfoPaymentDO infoPaymentDO)  throws EDCAPIMSystemException;

}
