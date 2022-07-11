package com.edc.edcweb.core.helpers.apsgForm;

import java.util.Map;
import org.apache.sling.api.SlingHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author  Roberto Ramos Vargas
 * @version 1.0.0
 * @since 1.0.0
 *
 *
 * Helper class to perform business layer operations about apsg form.
 *
 *
 */
public class APSGFormHelper {
    private static final Logger log = LoggerFactory.getLogger(APSGFormHelper.class);

    /**
     * Validates the user access, and the APSG Request form
     * @param request
     * @param email
     * @param docId
     * @return The APSGFormUserAccess object
     */
    public static APSGFormUserAccess validateAccess(SlingHttpServletRequest request, String email, String docId) {
        APSGFormUserAccess access = new APSGFormUserAccess(email, docId);
        boolean eloquaOK = false;
        String response = access.initializeEloquaContact();

        if (response!=null && response.equalsIgnoreCase("200")) {
            eloquaOK = true;
        }

        if (!eloquaOK) {
            return null;
        }

        return access;
    }

    public static APSGFormUserAccess buildFromPostRequestParams(Map<String,Object> map, String email, String docId) {
        APSGFormUserAccess access = new APSGFormUserAccess(email, docId);

        // For each parameter we need to add the name and the value
        for (Map.Entry<String,Object> entry : map.entrySet()) {
            String key = entry.getKey();
            String[] strArr = (String [])entry.getValue();
            access.addSubmittedField(key, strArr);
        }

        return access;
    }

    public static String  updateUserDataToEloqua(SlingHttpServletRequest request, APSGFormUserAccess access, String resourcePath) {
        String statusCode = "";
        access.addHiddenSubmittedFields(request, resourcePath);
        statusCode = access.updateEloquaContact();
        return statusCode;
    }
}