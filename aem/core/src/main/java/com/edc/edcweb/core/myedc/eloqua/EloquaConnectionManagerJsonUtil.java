package com.edc.edcweb.core.myedc.eloqua;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class EloquaConnectionManagerJsonUtil {

    private EloquaConnectionManagerJsonUtil() {
        // sonar lint
    }

    public static Map<String, String> transJsonToSet(JSONObject jsonObject, String aemPathFieldId) throws JSONException {
        //Use tree map to order the records from newer to older
        Map<String, String> pagePaths = new LinkedHashMap<>();
        if (jsonObject.has(EloquaConnectionManagerConstants.JsonKeys.SEARCH_RECORD_ELEMENTS)) {
            JSONArray elements = jsonObject
                    .getJSONArray(EloquaConnectionManagerConstants.JsonKeys.SEARCH_RECORD_ELEMENTS);
            for (int i = 0; i < elements.length(); i++) {
                JSONObject record = elements.getJSONObject(i);
                if (record.has(EloquaConnectionManagerConstants.JsonKeys.FIELD_VALUES)) {
                    String createdAt = record.getString(EloquaConnectionManagerConstants.JsonKeys.CREATED_AT);
                    JSONArray values = record.getJSONArray(EloquaConnectionManagerConstants.JsonKeys.FIELD_VALUES);
                    for (int v = 0; v < values.length(); v++) {
                        JSONObject value = values.getJSONObject(v);
                        if (value.has(EloquaConnectionManagerConstants.JsonKeys.ID)) {
                            String fieldId = value.getString(EloquaConnectionManagerConstants.JsonKeys.ID);
                            if (aemPathFieldId.equals(fieldId)) {
                                String aemPathValue = value.getString(EloquaConnectionManagerConstants.JsonKeys.VALUE);
                                //use as key, avoid duplicates
                                pagePaths.put(aemPathValue, createdAt);
                            }
                        }
                    }
                }
            }
        }
        return pagePaths;
    }
}
