package com.edc.edcportal.core.eloqua;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.edc.edcportal.core.eloqua.pojo.EloquaCDO;
import com.edc.edcportal.core.eloqua.pojo.EloquaCDOField;
import com.edc.edcportal.core.eloqua.pojo.EloquaDataItem;
import com.edc.edcportal.core.eloqua.pojo.EloquaRecordDO;
import com.edc.edcportal.core.eloqua.pojo.EloquaUserProfileDO;

public class EloquaConnectionManagerUtil {

    private EloquaConnectionManagerUtil() {
        // Sonar Lint
    }

    /**
     * Get the Response and return the first record
     * 
     * @param eloquaRecordDO
     * @return first record
     */
    public static EloquaCDO getRecordFromSearch(EloquaRecordDO eloquaRecordDO) {
        EloquaCDO record = new EloquaCDO();
        if (eloquaRecordDO.getErrorMessage() == null && eloquaRecordDO.getTotal() == 1) {
            record = eloquaRecordDO.getElements().get(0);
        }
        return record;
    }

    /**
     * Gets the json from search and converts it into the EloquaUserProfileDO
     * 
     * @param eloquaRecordDO
     * @return
     * @throws JSONException
     */
    public static EloquaUserProfileDO cdoSearchToDO(EloquaRecordDO eloquaRecordDO) throws JSONException {
        EloquaUserProfileDO eloquaUserProfileDO = new EloquaUserProfileDO();
        if (getRecordFromSearch(eloquaRecordDO).getId() != null) {
            eloquaUserProfileDO.setCdoRecordId(getRecordFromSearch(eloquaRecordDO).getId());
        }
        List<EloquaCDOField> values = new LinkedList<>();
        if (getRecordFromSearch(eloquaRecordDO).getFieldValues() != null) {
            values = getRecordFromSearch(eloquaRecordDO).getFieldValues();
        }
        return fieldValuesToDO(eloquaUserProfileDO, values);
    }

    /**
     * Gets the info from new json and converts it into the EloquaUserProfileDO
     * 
     * @param eloquaUserProfileDO
     * @param eloquaCDO
     * @return
     * @throws JSONException
     */
    public static EloquaUserProfileDO cdoNewToDO(EloquaUserProfileDO eloquaUserProfileDO, EloquaCDO eloquaCDO)
            throws JSONException {
        //new JSON Object is different than the search
        if (eloquaCDO.getId() != null) {
            String recordID = eloquaCDO.getId();
            eloquaUserProfileDO.setCdoRecordId(recordID);
        }
        List<EloquaCDOField> values = new LinkedList<>();
        if (eloquaCDO.getFieldValues() != null) {
            values = eloquaCDO.getFieldValues();
        }
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
    public static EloquaUserProfileDO fieldValuesToDO(EloquaUserProfileDO eloquaUserProfileDO, List<EloquaCDOField> values)
            throws JSONException {
        // create field values
        Map<String, EloquaDataItem> formFieldsValues = new LinkedHashMap<>();
        for (EloquaCDOField fieldDef : values) {
            // create data item
            EloquaDataItem eloquaDataItem = new EloquaDataItem();
            String fieldId = fieldDef.getId();
            eloquaDataItem.setEloquaValue(fieldDef.getValue());
            formFieldsValues.put(fieldId, eloquaDataItem);
        }
        eloquaUserProfileDO.setFormFieldsValues(formFieldsValues);
        return eloquaUserProfileDO;
    }

    /**
     * Convenience method to get an elouqua value from the object
     * 
     * @param eloquaUserProfileDO
     * @param eloquaFormId
     * @return String with value
     */
    public static String getELoquaValueFromDO(EloquaUserProfileDO eloquaUserProfileDO, String eloquaFormId) {
        return eloquaUserProfileDO.getFormFieldsValues().get(eloquaFormId).getEloquaValue();
    }

    /**
     * Convenience method to get the corresponding field name
     * 
     * @param eloquaUserProfileDO
     * @param eloquaFormId
     * @return String with fieldName
     */
    public static String getFieldNameFromDO(EloquaUserProfileDO eloquaUserProfileDO, String eloquaFormId) {
        return eloquaUserProfileDO.getFormFieldsValues().get(eloquaFormId).getFormFieldName();
    }

    /**
     * Convenience method to get an posted value from the object
     * 
     * @param eloquaUserProfileDO
     * @param eloquaFormId
     * @return String with value
     */
    public static String getFormValueFromDO(EloquaUserProfileDO eloquaUserProfileDO, String eloquaFormId) {
        return eloquaUserProfileDO.getFormFieldsValues().get(eloquaFormId).getFormFieldValue();
    }

    /**
     * Fetches all the values from CDO and return them as Map<id, value>
     * 
     * @param eloquaCDO
     * @return String String Map recordMap with field ids
     */
    public static Map<String, String> getCDOFields(EloquaCDO eloquaCDO) {
        Map<String, String> recordMap = new LinkedHashMap<>();
        if (eloquaCDO.getErrorMessage() == null) {
            List<EloquaCDOField> fields = eloquaCDO.getFields();
            for (EloquaCDOField field : fields) {
                recordMap.put(field.getId(), "");
            }
        }
        return recordMap;
    }
}
