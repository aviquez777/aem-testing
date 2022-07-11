package com.edc.edcweb.core.helpers;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class QueryStringHelper {

    private QueryStringHelper() {
        // Sonar Lint
    }

    /**
     * getParamMap Parses the raw query string and returns a Value Map with the
     * parameter as key and the value as value
     * 
     * @param queryString the raw query string
     * @param charset To decode the values
     * @return Value Map with the parameter as key and the value as value
     */
    public static Map<String, String> getParamMap(String queryString, Charset charset) {
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }
        Map<String, String> paramMap = new HashMap<>();
        String[] pairs = queryString.split(Constants.AMPERSAND_SIGN);
        for (String pair : pairs) {
            int idx = pair.indexOf(Constants.EQUAL_SIGN);
            paramMap.put(pair.substring(0, idx), Base64EnconderHelper.decodeString(pair.substring(idx + 1), charset));
        }
        return paramMap;
    }
}