package com.edc.edcportal.core.ci.services;

import java.io.IOException;

import org.json.JSONObject;

import com.edc.edcportal.core.ci.pojo.CiCompanySearchDO;
import com.edc.edcportal.core.exception.EDCAPIMSystemException;

public interface CiDAOService {

    String getCountries() throws EDCAPIMSystemException;

    CiCompanySearchDO searchCompanyByName(String companyName, String edcCountryCode, String language)
            throws EDCAPIMSystemException, IOException;

    JSONObject getCompanyDetailsById(String companyId, String language) throws EDCAPIMSystemException, IOException;
}
