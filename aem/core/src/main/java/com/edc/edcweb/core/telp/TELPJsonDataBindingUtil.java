package com.edc.edcweb.core.telp;

import com.edc.edcweb.core.models.questionnaire.QestionnaireDO;
import com.edc.edcweb.core.telp.helpers.TelpConstants;
import com.edc.edcweb.core.telp.pojo.BeneficialOwner;
import com.edc.edcweb.core.telp.pojo.TelpFormDOPart1;
import com.edc.edcweb.core.telp.pojo.TelpFormDOPart2;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TELPJsonDataBindingUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    private TELPJsonDataBindingUtil() {
        // Sonar lint
    }

    public static String telpFormDOToJson(TelpFormDOPart1 telpForm1, TelpFormDOPart2 telpForm2) {
        // Get the first part of the form
        JsonNode part1 = mapper.valueToTree(telpForm1);
        // Magic to keep the UBOS Dynamic
        Integer counter = 1;
        for(BeneficialOwner ubo : telpForm1.getBeneficialOwners()) {
            // Generate the Keys with the numbers
            String counterString  = counter.toString();
            String uboFirstKey = String.format(TelpConstants.UBOKeys.UBO_FIRST_NAME_KEY.getKeyName(), counterString);
            String uboLastKey = String.format(TelpConstants.UBOKeys.UBO_LAST_NAME_KEY.getKeyName(), counterString);
            String uboCountryKey = String.format(TelpConstants.UBOKeys.UBO_COUNTRY_KEY.getKeyName(), counterString);
            // Read the elements from the class and add them to the  dynamic keys
            ((ObjectNode) part1).put(uboFirstKey, ubo.getUboFirstName());
            ((ObjectNode) part1).put(uboLastKey, ubo.getUboLastName());
            // Get the country Object as JSON Node
            JsonNode countryNode = mapper.valueToTree(ubo.getUboCountry());
            // Add it to the  dynamic keys info we have
            ((ObjectNode) part1).set(uboCountryKey,countryNode);
            counter++;
        }
        // Get the second part of the form
        JsonNode part2 = mapper.valueToTree(telpForm2);
        String concatenated = part1.toString().concat(part2.toString());
        // Append to get the complete JSON
        return concatenated.replace("}{",",");
    }

    public static String telpQestionnaireDOToJson(QestionnaireDO qestionnaireDO) throws JsonProcessingException {
        return mapper.writeValueAsString(qestionnaireDO);
    }
}
