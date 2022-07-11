package com.edc.edcportal.core.ci.helpers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.edc.edcportal.core.ci.CiConstants;
import com.edc.edcportal.core.helpers.JSONReaderHelper;

public class DataObjectTransformationHelper {
    /**
     * Utilities to transform data between objects
     */

    private DataObjectTransformationHelper() {
        // Sonar Lint
    }

    public static Map<String, Map<String, String>> getSections(Map<String, List<String>> sectionMap,
            JSONObject jsonObject, String mapFieldList) {
        Map<String, Map<String, String>> results = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> section : sectionMap.entrySet()) {
            String sectionName = section.getKey();
            List<String> fields = section.getValue();
            Map<String, String> fieldMap = new LinkedHashMap<>();
            int fieldSize = fields.size();
            int counter = 0;
            for (String keyName : fields) {
                if (sectionName.contains(CiConstants.SECTION_ARAY_IDENTIFIED)) {
                    fieldMap = JSONReaderHelper.getArrayValueToMap(keyName, jsonObject);
                } else {
                    String value = getValueFromJson(keyName, jsonObject);
                    if (StringUtils.isNotBlank(value)) {
                        fieldMap.put(keyName, value);
                    }
                }
                counter++;
            }
            results.put(sectionName, fieldMap);
            // Add the google map link to the last item
            if (CiConstants.SECTION_HEADQUARTER.equals(sectionName) && counter == fieldSize) {
                fieldMap.put(CiConstants.MAP_LINK_VAR, doMapLink(jsonObject, mapFieldList));
                results.put(sectionName, fieldMap);
            }
        }
        return results;
    }

    public static Map<String, Map<String, String>> getSectionsForAuthor(Map<String, List<String>> sectionMap) {
        Map<String, Map<String, String>> results = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> section : sectionMap.entrySet()) {
            String sectionName = section.getKey();
            List<String> fields = section.getValue();
            Map<String, String> fieldMap = new LinkedHashMap<>();
            int fieldSize = fields.size();
            int counter = 0;
            for (String keyName : fields) {
                // check if we have multiple keyNames
                String[] fieldsArr = keyName.split(",");
                String value = null;
                for (String key : fieldsArr) {
                    if (StringUtils.isBlank(value)) {
                        value = key.concat(CiConstants.SAMPLE_TEXT);
                    } else {
                        value = value.concat(", ").concat(key).concat(CiConstants.SAMPLE_TEXT);
                    }
                }
                fieldMap.put(keyName, value);
                counter++;
            }
            results.put(sectionName, fieldMap);
            // Add the google map link to the last item
            if (CiConstants.SECTION_HEADQUARTER.equals(sectionName) && counter == fieldSize) {
                fieldMap.put(CiConstants.MAP_LINK_VAR, CiConstants.POUND);
                results.put(sectionName, fieldMap);
            }
        }
        return results;
    }

    private static String doMapLink(JSONObject jsonObject, String mapFieldList) {
        StringBuilder sb = new StringBuilder();
        String[] fieldsArr = mapFieldList.split(CiConstants.COMMA);
        int counter = 1;
        int arrLen = fieldsArr.length;
        for (String fieldName : fieldsArr) {
            String value = JSONReaderHelper.getStringValue(fieldName, jsonObject);
            if (StringUtils.isNotBlank(value)) {
                sb.append(value);
                if (arrLen > counter) {
                    sb.append(CiConstants.COMMA_SPACE);
                }
            }
            counter++;
        }
        return sb.toString();
    }
    
    private static String getValueFromJson(String keyName, JSONObject jsonObject) {
        // check if we have multiple keyNames
        String[] fieldsArr = keyName.split(CiConstants.COMMA);
        String value = null;
        for (String key : fieldsArr) {
            String thisValue = JSONReaderHelper.getStringValue(key, jsonObject);
            if (StringUtils.isNotBlank(thisValue)) {
                if (StringUtils.isBlank(value)) {
                    value = thisValue;
                } else {
                    value = value.concat(CiConstants.COMMA_SPACE).concat(thisValue);
                }
            }
        }
        return value;
    }
}
