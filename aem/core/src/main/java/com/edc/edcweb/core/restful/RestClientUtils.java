package com.edc.edcweb.core.restful;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

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
 
    /**
     * Helper to parse and convert the response into a JSONObject
     * 
     * @param is InputStream
     * @return JSONObject of the response
     * @throws JSONException
     * @throws IOException
     */
    public static JSONObject convertStreamToJSONObject(InputStream is) throws JSONException, IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();

        String line = null;

        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }

        return new JSONObject(sb.toString());
    }

}
