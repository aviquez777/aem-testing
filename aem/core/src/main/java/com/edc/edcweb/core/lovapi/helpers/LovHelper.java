package com.edc.edcweb.core.lovapi.helpers;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.lovapi.LovApiJsonDataBindingUtil;
import com.edc.edcweb.core.lovapi.pojo.SingleLovDO;
import com.edc.edcweb.core.lovapi.service.LovApiDAOService;

public class LovHelper {

    private static final Logger log = LoggerFactory.getLogger(LovHelper.class);

    private LovHelper() {
        // Sonar Qube
    }
/**
 *  Utility to get the Lovs from other Java Classes and the Sling models
 * @param query search term
 * @param lovApiDAOService to be used for file extension validation
 * @return SingleLovDO with the query result
 */
public static SingleLovDO getSingleLovDO(String query, LovApiDAOService lovApiDAOService) {
    SingleLovDO singleLovDO = new SingleLovDO();
    String responseJson = lovApiDAOService.getLov(query);
    try {
        singleLovDO = LovApiJsonDataBindingUtil.jsonToSingleLovDO(responseJson);
    } catch (IOException e) {
        // Unable to transform, set the error message to display a message to the user
        singleLovDO.setErrorMessage("error");
        log.error("FindByCodeLov error on jsonToSingleLovDO {}", e.getMessage());
    }
    return singleLovDO;
}

}
