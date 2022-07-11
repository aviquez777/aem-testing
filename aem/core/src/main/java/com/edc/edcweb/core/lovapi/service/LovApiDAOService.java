package com.edc.edcweb.core.lovapi.service;

/**
 * 
 * Service Interface to add future LOV API integration
 *
 */
public interface LovApiDAOService {
    /**
     * Generic Lov Interface to Get the JSONObject from
     * the API
     * 
     * @param lovType Lov type to retrieve the proper list for
     * @return String with the Data, error key with error's information
     */
    String getLov(String lovType);

    /**
     * NMG / BCAP / TELP Financial institutions Interface to Get the JSONObject from
     * the API
     * 
     * @param formType Form type to retrieve the proper list for
     * @return String with the Data, error key with error's information
     */
    String getFiLov(String formType);

}
