package com.edc.edcportal.core.helpers;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONReaderHelper {

    private static final Logger log = LoggerFactory.getLogger(JSONReaderHelper.class);

    private JSONReaderHelper() {
        // Sonar lint
    }

    public static String getStringValue(String keyName, JSONObject jsonObject) {
        String value = null;
        if (jsonObject.has(keyName)) {
            try {
                value = jsonObject.getString(keyName);
            } catch (JSONException e) {
                log.error("JsonReaderHelper getStringValue error reading key {} from {} ", keyName, jsonObject);
            }
        }
        return value;
    }

    public static List<String> getArrayValue(String keyName, JSONObject jsonObject) {
        List<String> list = new LinkedList<>();
        if (jsonObject.has(keyName)) {
            try {
                JSONArray jArray = jsonObject.getJSONArray(keyName);
                if (jArray != null) {
                    for (int i = 0; i < jArray.length(); i++) {
                        list.add(jArray.getString(i));
                    }
                }
            } catch (JSONException e) {
                log.error("JsonReaderHelper getArrayValue error reading key {} from {} ", keyName, jsonObject);
            }
        }
        return list;
    }
    
    public static Map<String, String> getArrayValueToMap(String keyName, JSONObject jsonObject) {
        Map<String, String> map = new LinkedHashMap<>();
        List<String> list = getArrayValue(keyName, jsonObject);
        int counter = 0;
        for (String item : list) {
            map.put(Integer.toString(counter), item);
            counter++;
          }
        return map;
    }
    

}
