package com.edc.edcweb.core.myedc.eloqua;

import com.edc.edcweb.core.myedc.eloqua.pojo.EloquaDataItem;
import com.edc.edcweb.core.myedc.eloqua.pojo.EloquaUserProfileDO;
import com.edc.edcweb.core.restful.RestClientConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class EloquaConnectionManagerUtil {

    private EloquaConnectionManagerUtil() {
        // Sonar Lint
    }

    /**
     * Get the Response and return the first record
     *
     * @param jsonObject
     * @return first record
     * @throws JSONException
     */
    private static JSONObject getRecordFromSearch(JSONObject jsonObject) throws JSONException {
        JSONObject record = new JSONObject();
        if (!jsonObject.has(RestClientConstants.ERROR_MSG)
                && jsonObject.getInt(EloquaConnectionManagerConstants.JsonKeys.SEARCH_TOTAL) == 1) {
            JSONArray elements = jsonObject
                    .getJSONArray(EloquaConnectionManagerConstants.JsonKeys.SEARCH_RECORD_ELEMENTS);
            record = elements.getJSONObject(0);
        }
        return record;
    }

    /**
     * Gets the json from search and converts it into the EloquaUserProfileDO
     *
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    public static EloquaUserProfileDO cdoSearchToDO(JSONObject jsonObject) throws JSONException {
        EloquaUserProfileDO eloquaUserProfileDO = new EloquaUserProfileDO();
        eloquaUserProfileDO.setCdoRecordId(
                getStringElementFromRecordSearch(jsonObject, EloquaConnectionManagerConstants.JsonKeys.ID));
        JSONArray values = getArrayElementFromRecordSerach(jsonObject,
                EloquaConnectionManagerConstants.JsonKeys.FIELD_VALUES);
        return fieldValuesToDO(eloquaUserProfileDO, values);
    }

    /**
     * Gets the json from new record and converts it into the EloquaUserProfileDO
     *
     * @param eloquaUserProfileDO
     * @param values
     * @return
     * @throws JSONException
     */
    public static EloquaUserProfileDO fieldValuesToDO(EloquaUserProfileDO eloquaUserProfileDO, JSONArray values)
            throws JSONException {
        // create field values
        Map<String, EloquaDataItem> formFieldsValues = new LinkedHashMap<>();
        for (int i = 0; i < values.length(); i++) {
            // create data item
            EloquaDataItem eloquaDataItem = new EloquaDataItem();
            JSONObject fieldDef = values.getJSONObject(i);
            String fieldId = fieldDef.getString(EloquaConnectionManagerConstants.JsonKeys.ID);
            eloquaDataItem.setEloquaValue(fieldDef.getString(EloquaConnectionManagerConstants.JsonKeys.VALUE));
            formFieldsValues.put(fieldId, eloquaDataItem);
        }
        eloquaUserProfileDO.setFormFieldsValues(formFieldsValues);
        return eloquaUserProfileDO;
    }

    /**
     * Fetches a string value from the Elements Search recordset
     *
     * @param jsonObject
     * @param elementName
     * @return string value from the Elements Search recordset
     * @throws JSONException
     */
    public static String getStringElementFromRecordSearch(JSONObject jsonObject, String elementName)
            throws JSONException {
        String value = null;
        JSONObject record = getRecordFromSearch(jsonObject);
        if (record.has(elementName)) {
            value = record.getString(elementName);
        }
        return value;
    }

    /**
     * Fetches a JsonArray value from the Elements Search recordset
     *
     * @param jsonObject
     * @param elementName
     * @return JsonArray value from the Elements Search recordset
     * @throws JSONException
     */
    public static JSONArray getArrayElementFromRecordSerach(JSONObject jsonObject, String elementName)
            throws JSONException {
        JSONArray value = new JSONArray();
        JSONObject record = getRecordFromSearch(jsonObject);
        if (record.has(elementName)) {
            value = record.getJSONArray(elementName);
        }
        return value;
    }
}
