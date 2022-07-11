package com.edc.edcportal.core.restful;

import java.util.Base64;

public class RestClientUtils {

    private RestClientUtils() {
        // Sonar lint
    }

    public static String base64Encode(String toEncode) {
        byte[] authByte = toEncode.getBytes();
        return Base64.getEncoder().encodeToString(authByte);
    }

    public static String base64Decode(String encoded) {
        byte[] decodedByte = Base64.getDecoder().decode(encoded.getBytes());
        return new String(decodedByte);
    }

}
