package com.edc.edcweb.core.lovapi;

import java.io.IOException;
import com.edc.edcweb.core.lovapi.pojo.SingleLovDO;
import com.edc.edcweb.core.lovapi.pojo.FinancialInstitutionsDO;
import com.edc.edcweb.core.lovapi.pojo.GenericLovDO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * Use Jackson Databind to transform from JSON to the Object
 *
 */
public class LovApiJsonDataBindingUtil {

    private LovApiJsonDataBindingUtil() {
        // Sonar lint
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Transform JSON from API to GenericLovDO
     * 
     * @param jsonString
     * @return GenericLovDO with API data
     * @throws IOException
     */
    public static GenericLovDO jsonToGenericLovDO(String jsonString) throws IOException {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper.readValue(jsonString, GenericLovDO.class);
    }

    /**
     * Transform JSON String from API to SingleLovDO
     *
     * @param jsonString
     * @return SingleLovDO with API data
     * @throws IOException
     */
    public static SingleLovDO jsonToSingleLovDO(String jsonString) throws IOException {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper.readValue(jsonString, SingleLovDO.class);
    }

    /**
     * Transform JSON from API to FinancialInstitutionsDO
     *
     * @param jsonString
     * @return FinancialInstitutionsDO with API data
     * @throws IOException
     */
    public static FinancialInstitutionsDO jsonToFinancialInstitutionsDO(String jsonString) throws IOException {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper.readValue(jsonString, FinancialInstitutionsDO.class);
    }

}
