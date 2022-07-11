package com.edc.edcportal.core.lovapi.service;

/**
 * 
 * Service Interface to add future LOV API integration
 *
 */
public interface LovApiDAOService {

    /**
     * getLov specified by codeTableName
     * @param codeTableName
     * @return Json with Response
     */
    String getLov(String codeTableName);

    /**
     * Use postLovSearchRequest to search the LOV API
     * @param codeSearchType 0 Table Id and Code, 1 Table Id and English Description,
     *                       2 Table Name and Code, 3 Table Name and English
     *                       Description
     * @param codeTableId 
     * @param codeTableName Provided by the API team i.e. "PopFinancialInstitution"
     * @param code Item Code
     * @param desc Item English Description
     * @param lang to return result
     * @return code or desc label in requested Language
     */
    String postLovSearchRequest(Integer codeSearchType, Integer codeTableId, String codeTableName, String code,
            String desc, String lang);

}
