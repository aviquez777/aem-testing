package com.edc.edcportal.core.models;

import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.edc.edcportal.core.eloqua.EloquaConnectionManagerConstants;

/**
 * Helper model to validate string from HTL
 *
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class StringOperationsHelperModel {

    @Self
    private SlingHttpServletRequest request;

    @Inject
    @Optional
    private String valuesToSearch;

    @Inject
    @Optional
    private String valueToFind;

    public boolean compareStrings() {
        boolean result = false;
        if (StringUtils.isNotBlank(valuesToSearch) && StringUtils.isNotBlank(valueToFind)) {
            String[] arrayValues = valuesToSearch.split(EloquaConnectionManagerConstants.MULTI_VALUE_SEPARATOR);
            result = ArrayUtils.contains(arrayValues, valueToFind);
        }
        return result;
    }

    @Inject
    @Optional
    private Map<String, String> mapToSearch;

    @Inject
    @Optional
    private String keyToFind;

    public boolean findKeyInMap() {
        boolean found = false;
        if ((mapToSearch != null && !mapToSearch.isEmpty()) && StringUtils.isNotBlank(keyToFind)) {
            found = mapToSearch.containsKey(keyToFind);
        }
        return found;
    }
}
