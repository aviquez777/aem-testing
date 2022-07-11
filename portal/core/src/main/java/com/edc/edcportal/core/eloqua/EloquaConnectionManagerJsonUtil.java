package com.edc.edcportal.core.eloqua;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.eloqua.pojo.EloquaCDO;
import com.edc.edcportal.core.eloqua.pojo.EloquaCDOField;
import com.edc.edcportal.core.eloqua.pojo.EloquaDataItem;
import com.edc.edcportal.core.eloqua.pojo.EloquaRecordDO;
import com.edc.edcportal.core.eloqua.pojo.EloquaUserProfileDO;
import com.fasterxml.jackson.core.JsonProcessingException;

public class EloquaConnectionManagerJsonUtil {

    private static final Logger logger = LoggerFactory.getLogger(EloquaConnectionManagerJsonUtil.class);

    private EloquaConnectionManagerJsonUtil() {
        // sonar lint
    }

    /**
     * Transforms the EloquaUserProfileDO into a JSon String to be sent as body on
     * the Put request
     * 
     * @param eloquaUserProfileDO Object with values
     * @return JSon String to be sent as body on the Put request
     */

    public static String profileDOToJsonPut(EloquaUserProfileDO eloquaUserProfileDO) {
        String jsonString = null;
        EloquaCDO eloquaCDO = new EloquaCDO();
        List<EloquaCDOField> fieldValues = new LinkedList<>();
        // Add the record type
        eloquaCDO.setType(EloquaConnectionManagerConstants.JsonKeys.COD);
        // Get the Values
        Map<String, EloquaDataItem> formFieldsValue = eloquaUserProfileDO.getFormFieldsValues();
        // Loop over the values on the object to populate the EloquaCDO with the
        // fieldValues
        for (Map.Entry<String, EloquaDataItem> item : formFieldsValue.entrySet()) {
            // EloquaCDOField which contains type=FieldValue, id=<eloqua fieldId>,
            // value=<updated value>
            EloquaCDOField fieldDefinition = new EloquaCDOField();
            fieldDefinition.setType(EloquaConnectionManagerConstants.JsonKeys.FIELD_VALUE);
            fieldDefinition.setId(item.getKey());
            String itemValue = item.getValue().getFormFieldValue();
            // If we don't have a value use the one from Eloqua
            if (StringUtils.isBlank(itemValue) && item.getValue().getRequiered()) {
                itemValue = item.getValue().getEloquaValue();
            }

            fieldDefinition.setValue(itemValue);
            fieldValues.add(fieldDefinition);
        }
        // add the array to the "main" eloquaCDO
        eloquaCDO.setFieldValues(fieldValues);
        try {
            jsonString = EloquaDataBindingUtil.pojoToJson(eloquaCDO);
        } catch (JsonProcessingException e) {
            logger.error("EloquaConnectionManagerJsonUtil profileDOToJsonPut failed processing Json {}", jsonString, e);
        }
        return jsonString;
    }

    /**
     * Transforms the EloquaUserProfileDO into a JSon String to be sent as body on
     * the Post request
     * 
     * @param eloquaUserProfileDO Object with values
     * @return JSon String to be sent as body on the Post request
     * @throws JSONException
     */
    public static String profileDOToJsonPost(EloquaUserProfileDO eloquaUserProfileDO) throws JSONException {
        String jsonString = null;
        EloquaCDO eloquaCDO = new EloquaCDO();
        List<EloquaCDOField> fieldValues = new LinkedList<>();
        // Add the record type
        eloquaCDO.setType(EloquaConnectionManagerConstants.JsonKeys.COD);
        // Get the Values
        Map<String, EloquaDataItem> formFieldsValue = eloquaUserProfileDO.getFormFieldsValues();
        // Loop over the values on the object to populate the EloquaCDO with the
        // fieldValues
        for (Map.Entry<String, EloquaDataItem> item : formFieldsValue.entrySet()) {
            // EloquaCDOField which contains type=FieldValue, id=<eloqua fieldId>,
            // value=<updated value>
            EloquaCDOField fieldDefinition = new EloquaCDOField();
            fieldDefinition.setType(EloquaConnectionManagerConstants.JsonKeys.FIELD_VALUE);
            fieldDefinition.setId(item.getKey());
            String itemValue = item.getValue().getFormFieldValue();
            // If we don't have a value use the one from Eloqua
            if (StringUtils.isBlank(itemValue)) {
                itemValue = item.getValue().getEloquaValue();
            }

            fieldDefinition.setValue(itemValue);
            fieldValues.add(fieldDefinition);
        }
        // add the array to the "main" eloquaCDO
        eloquaCDO.setFieldValues(fieldValues);
        try {
            jsonString = EloquaDataBindingUtil.pojoToJson(eloquaCDO);
        } catch (JsonProcessingException e) {
            logger.error("EloquaConnectionManagerJsonUtil profileDOToJsonPut failed processing Json {}", jsonString, e);
        }
        return jsonString;
    }

    public static Map<String, String> transJsonToSet(EloquaRecordDO eloquaRecordDO, String aemPathFieldId) throws JSONException {
        //Use tree map to order the records from newer to older
        Map<String, String> pagePaths = new LinkedHashMap<>();
        if (eloquaRecordDO.getElements() != null) {
            List<EloquaCDO> elements = eloquaRecordDO.getElements();
            for (EloquaCDO record : elements) {
                if (record.getFieldValues() != null) {
                    String createdAt = record.getCreatedAt();
                    List<EloquaCDOField> values = record.getFieldValues();
                    for (EloquaCDOField value : values) {
                        if (value.getId() != null) {
                            String fieldId = value.getId();
                            if (aemPathFieldId.equals(fieldId)) {
                                String aemPathValue = value.getValue();
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
