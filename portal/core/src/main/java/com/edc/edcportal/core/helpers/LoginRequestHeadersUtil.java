package com.edc.edcportal.core.helpers;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.SlingHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.eloqua.services.EloquaConfigService;

public class LoginRequestHeadersUtil {

    private static final Logger logger = LoggerFactory.getLogger(LoginRequestHeadersUtil.class);

    private LoginRequestHeadersUtil() {
        // Sonar Lint
    }

    /**
     * Get the fields mapped to params
     * 
     * @param request
     * @return
     */
    public static Map<String, String> getHeaders(SlingHttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.Properties.HEADER_FIRST_NAME,
                reEncodeChars(request.getHeader(Constants.Properties.HEADER_FIRST_NAME), Constants.Properties.HEADER_FIRST_NAME));
        headers.put(Constants.Properties.HEADER_LAST_NAME,
                reEncodeChars(request.getHeader(Constants.Properties.HEADER_LAST_NAME), Constants.Properties.HEADER_LAST_NAME));
        headers.put(Constants.Properties.HEADER_EMAIL_ID, request.getHeader(Constants.Properties.HEADER_EMAIL_ID));
        headers.put(Constants.Properties.HEADER_MOBILE_NUMBER,
                request.getHeader(Constants.Properties.HEADER_MOBILE_NUMBER));
        headers.put(Constants.Properties.HEADER_EXTERNAL_ID,
                request.getHeader(Constants.Properties.HEADER_EXTERNAL_ID));
        headers.put(Constants.Properties.HEADER_CREATED_DATE_TIME, DateUtilsHelper
                .iSOToUnixTimestamp(request.getHeader(Constants.Properties.HEADER_CREATED_DATE_TIME), true));
        return headers;
    }

    /**
     * Get the fields mapped to Eloqua Ids
     * 
     * @param request
     * @return
     */
    public static Map<String, String> getHeadersForEloqua(Map<String, String> headers,
            EloquaConfigService eloquaService) {
        Map<String, String> eloquaHeaders = new HashMap<>();
        eloquaHeaders.put(eloquaService.getFirstNameFieldId(), headers.get(Constants.Properties.HEADER_FIRST_NAME));
        eloquaHeaders.put(eloquaService.getLastNameFieldId(), headers.get(Constants.Properties.HEADER_LAST_NAME));
        eloquaHeaders.put(eloquaService.getEmailIdFieldId(), headers.get(Constants.Properties.HEADER_EMAIL_ID));
        eloquaHeaders.put(eloquaService.getMobileNumberFieldId(),
                headers.get(Constants.Properties.HEADER_MOBILE_NUMBER));
        eloquaHeaders.put(eloquaService.getExtenalIdFieldId(), headers.get(Constants.Properties.HEADER_EXTERNAL_ID));
        eloquaHeaders.put(eloquaService.getCreatedDateTimeFieldId(),
                headers.get(Constants.Properties.HEADER_CREATED_DATE_TIME));
        return eloquaHeaders;
    }

    // Could not find a specific exception to replace
    @SuppressWarnings("squid:S2221")
    private static String reEncodeChars(String value, String key) {
        // Shibboleth attributes are by default UTF-8 encoded. However, depending on the
        // servlet container configuration they are interpreted as ISO-8859-1 values.
        // This causes problems with non-ASCII characters The solution is to re-encode
        // attributes.
        try {
            value = new String(value.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        } catch (Exception e) {
            String keyValue = "key: "+ key +"value:" + value;
            logger.warn("LoginRequestHeadersUtil error re-encoding string: {}", keyValue, e);
        }
        return value;
    }
}
