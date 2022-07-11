package com.edc.edcportal.core.eloqua.services;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.eloqua.EloquaConnectionManagerConstants;
import com.edc.edcportal.core.eloqua.pojo.EloquaDataItem;
import com.edc.edcportal.core.eloqua.pojo.EloquaUserProfileDO;
import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.FieldMapUtils;
import com.edc.edcportal.core.helpers.FormFieldDefinitionsUtil;
import com.edc.edcportal.core.helpers.FormFieldsUtil;
import com.edc.edcportal.core.helpers.FormValidationHelper;
import com.edc.edcportal.core.helpers.formvalidation.FormCleaner;
import com.edc.edcportal.core.models.FormFieldDefintion;
import com.edc.edcportal.core.services.FieldMappingConfigService;

public class EloquaDAOUtils {

    protected static final Logger log = LoggerFactory.getLogger(EloquaDAOUtils.class);

    private EloquaDAOUtils() {
        // Sonar lint
    }

    /**
     * Updates the Object with the field data of the profile type.
     * 
     * @param eloquaUserProfileDO
     * @param fieldMap
     * @return updated EloquaUserProfileDO
     */
    public static EloquaUserProfileDO updateProfileDOWithFieldData(EloquaUserProfileDO eloquaUserProfileDO,
            Map<String, String> fieldMap) {
        Map<String, EloquaDataItem> formFieldsValue = eloquaUserProfileDO.getFormFieldsValues();
        for (Map.Entry<String, EloquaDataItem> item : formFieldsValue.entrySet()) {
            String formFieldName = fieldMap.get(item.getKey());
            item.getValue().setFormFieldName(formFieldName);
        }
        return eloquaUserProfileDO;
    }

    /**
     * Update the EloquaUserProfileDO with the Values from the Map, if empty will
     * create the key to have al the Keys needed for the API
     * 
     * @param eloquaUserProfileDO
     * @param updatedValues
     * @return
     */
    public static EloquaUserProfileDO updateProfileDOWithMap(EloquaUserProfileDO eloquaUserProfileDO,
            Map<String, String> updatedValues) {
        Map<String, EloquaDataItem> formFieldsValue = eloquaUserProfileDO.getFormFieldsValues();
        // Fist update all the Keys
        if (formFieldsValue.isEmpty()) {
            for (Entry<String, String> updated : updatedValues.entrySet()) {
                formFieldsValue.put(updated.getKey(), new EloquaDataItem());
            }
        }
        // Now update the values
        for (Map.Entry<String, EloquaDataItem> item : formFieldsValue.entrySet()) {
            String updatedValue = updatedValues.get(item.getKey());
            item.getValue().setFormFieldValue(updatedValue);
        }
        return eloquaUserProfileDO;
    }

    /**
     * Update the EloquaUserProfileDO with the Values from the PostData If value is
     * required and it's empty will set a flag to not update POP and redirect the
     * user to edit profile to correct the errors
     * 
     * @param eloquaUserProfileDO
     * @param filedList
     * @param request
     * @param fieldMappingConfigService
     * @return EloquaUserProfileDO
     */
    public static EloquaUserProfileDO updateEloquaUserProfileDOFromPost(EloquaUserProfileDO eloquaUserProfileDO,
            String[] filedList, SlingHttpServletRequest request, FieldMappingConfigService fieldMappingConfigService) {
        Map<String, FormFieldDefintion> fieldDefinitions = FormFieldDefinitionsUtil.getDefinitions(request,
                fieldMappingConfigService);
        String selectedCountry = null;
        Map<String, EloquaDataItem> formFieldValues = eloquaUserProfileDO.getFormFieldsValues();
        // if no fields, then we should not submit to POP
        boolean popReady = !formFieldValues.isEmpty();
        for (Map.Entry<String, EloquaDataItem> item : formFieldValues.entrySet()) {
            String fieldName = item.getValue().getFormFieldName();
            if (ArrayUtils.contains(filedList, fieldName)) {
                // Get the value from post data
                String[] postValue = FormCleaner.cleanArray(request.getParameterValues(fieldName));
                FormFieldDefintion formFieldDefintion = fieldDefinitions.get(fieldName);
                boolean fieldIsRequired = formFieldDefintion.getIsRequired();
                if (Constants.Properties.COUNTRY_FIELD.equals(fieldName) && postValue.length > 0) {
                    selectedCountry = postValue[0];
                }
                if (StringUtils.isNotBlank(selectedCountry)) {
                    fieldIsRequired = FormValidationHelper.checkReqByCountry(selectedCountry, fieldName,
                            fieldIsRequired);
                }
                if (postValue != null) {
                    String valueToSave = StringUtils.join(postValue,
                            EloquaConnectionManagerConstants.MULTI_VALUE_SEPARATOR);
                    // Remove the "clear-all" text from the multiple multi fields that can be
                    // selected if javaScript is disabled.
                    if (formFieldDefintion.getIsMultiple() != null && formFieldDefintion.getIsMultiple()) {
                        valueToSave = FormValidationHelper.removeTheCheckAll(valueToSave);
                    }
                    // Validate the field
                    Map<String, Boolean> validationsResults = FormValidationHelper.checkRules(valueToSave,
                            fieldIsRequired, formFieldDefintion);
                    // format data if necessary
                    valueToSave = FormValidationHelper.formatData(valueToSave, formFieldDefintion);
                    // if there's one validation not passing, we are not Ok
                    boolean fieldIsNotOK = validationsResults.containsValue(false);
                    item.getValue().setFormFieldValue(valueToSave);
                    // this is needed for pop data transformation utility
                    item.getValue().setRequiered(fieldIsRequired);
                    // if the field is required should have data
                    // set the flag so we do not submit to pop
                    if (fieldIsRequired && fieldIsNotOK) {
                        // update flag only if has been set to true
                        // we just need one required field not set to prevent pop submission
                        if (popReady) {
                            popReady = false;
                        }
                        log.error("Field {} did not pass valudations {}", fieldName, validationsResults);
                    }
                } else {
                    if (fieldIsRequired) {
                        // Required field not submitted at all
                        // set the flag so we do not submit to pop
                        popReady = false;
                        log.error("Required field not submitted {} ", fieldName);
                    }
                }
            }
        }
        eloquaUserProfileDO.setPopReady(popReady);
        return eloquaUserProfileDO;
    }

    /**
     * 
     * @param eloquaUserProfileDO       Updated the current eloquaUserProfileDO with
     *                                  predefined list of utm parameter values if
     *                                  any
     * @param request
     * @param fieldMappingConfigService
     * @return updated eloquaUserProfileDO
     */
    public static EloquaUserProfileDO updateProfileDOWithRenewUTMS(EloquaUserProfileDO eloquaUserProfileDO,
            SlingHttpServletRequest request, FieldMappingConfigService fieldMappingConfigService) {
        Iterator<Resource> optionsIt = getUtmResourceIterator(request);
        while (optionsIt.hasNext()) {
            Resource thisUtm = optionsIt.next();
            String fieldName = thisUtm.getValueMap().get("fieldName", String.class);
            try {
                // new EloquaDataItem
                EloquaDataItem eloquaDataItem = new EloquaDataItem();
                String[] arr = FormFieldsUtil.getFieldMappingFromConfig(fieldName, fieldMappingConfigService);
                // Set the values
                String utmEloquaID = FieldMapUtils.getEloquaId(arr);
                String utmValue = FormCleaner.cleanAll(request.getParameter(fieldName));
                // Update the data item
                eloquaDataItem.setFormFieldName(fieldName);
                eloquaDataItem.setFormFieldValue(utmValue);
                // Update the DO
                eloquaUserProfileDO.getFormFieldsValues().put(utmEloquaID, eloquaDataItem);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                log.error("updateProfileDOWithRenewUTMS Invalid fieldName {} ", fieldName);
            }
        }
        return eloquaUserProfileDO;
    }

    /**
     * getUtmResourceIterator get the UTM resource iterator
     * @param request
     * @return Iterator<Resource>
     */
    public static Iterator<Resource> getUtmResourceIterator (SlingHttpServletRequest request) {
        String dataPath = Constants.DataPaths.MYEDCDATA_RENEWAL_UTM_DEFINITIONS;
        ResourceResolver resourceResolver = request.getResourceResolver();
        Resource fieldOptions = resourceResolver.getResource(dataPath);
        return fieldOptions.listChildren();

    }
}
