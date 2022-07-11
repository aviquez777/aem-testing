package com.edc.edcportal.core.eloqua.services.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.edc.edcportal.core.eloqua.EloquaConnectionManager;
import com.edc.edcportal.core.eloqua.EloquaConnectionManagerConstants;
import com.edc.edcportal.core.eloqua.EloquaConnectionManagerJsonUtil;
import com.edc.edcportal.core.eloqua.EloquaConnectionManagerUtil;
import com.edc.edcportal.core.eloqua.pojo.EloquaCDO;
import com.edc.edcportal.core.eloqua.pojo.EloquaCDOField;
import com.edc.edcportal.core.eloqua.pojo.EloquaRecordDO;
import com.edc.edcportal.core.eloqua.pojo.EloquaUserProfileDO;
import com.edc.edcportal.core.eloqua.services.EloquaConfigService;
import com.edc.edcportal.core.eloqua.services.EloquaDAOService;
import com.edc.edcportal.core.eloqua.services.EloquaDAOUtils;
import com.edc.edcportal.core.exception.EDCEloquaSystemException;
import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.FieldMapUtils;
import com.edc.edcportal.core.helpers.FormFieldsUtil;
import com.edc.edcportal.core.services.FieldMappingConfigService;
import com.edc.edcportal.core.transactionhistory.THJsonDataBindingUtil;
import com.edc.edcportal.core.transactionhistory.helpers.THHelper;
import com.edc.edcportal.core.transactionhistory.pojo.THRecordDO;
import com.edc.edcportal.core.transactionhistory.pojo.THSearchResults;
import com.edc.edcportal.core.transactionhistory.services.THConfigService;

@Component(immediate = true, service = EloquaDAOService.class)
public class EloquaDAOServiceImpl implements EloquaDAOService {

    @Reference
    private EloquaConfigService eloquaConfigService;

    @Override
    public EloquaUserProfileDO createUserProfile(EloquaUserProfileDO eloquaUserProfileDO,
            Map<String, String> headersForEloqua) throws EDCEloquaSystemException {
        try {
            if (eloquaUserProfileDO.getFormFieldsValues().isEmpty()) {
                EloquaCDO eloquaCDO = EloquaConnectionManager.getCDO(eloquaConfigService);
                Map<String, String> fieldValues = EloquaConnectionManagerUtil.getCDOFields(eloquaCDO);
                EloquaDAOUtils.updateProfileDOWithMap(eloquaUserProfileDO, fieldValues);
            }
            EloquaDAOUtils.updateProfileDOWithMap(eloquaUserProfileDO, headersForEloqua);
            eloquaUserProfileDO = EloquaConnectionManager.createRecordWithHeaders(eloquaUserProfileDO,
                    eloquaConfigService);
        } catch (JSONException | IOException e) {
            throw new EDCEloquaSystemException("headersForEloqua" + headersForEloqua.toString(),
                    this.getClass().getName(), "createUserProfile", e.toString());
        }
        return eloquaUserProfileDO;
    }

    @Override
    public EloquaUserProfileDO getUserProfileByExternalId(String externalId) throws EDCEloquaSystemException {
        // Create an empty object
        EloquaUserProfileDO eloquaUserProfileDO = new EloquaUserProfileDO();
        try {
            eloquaUserProfileDO = EloquaConnectionManager.getRecordByExternalId(externalId, eloquaConfigService);
        } catch (JSONException | IOException e) {
            eloquaUserProfileDO.setErrorMessage(e.toString());
            throw new EDCEloquaSystemException("externalId: " + externalId, this.getClass().getName(),
                    "getUserProfileByExternalId", e.toString());
        }
        return eloquaUserProfileDO;
    }

    @Override
    public Map<String, String> prePopulatePPDataByEMailId(Map<String, String> formFields, String email,
            FieldMappingConfigService fieldMappingConfigService) throws EDCEloquaSystemException {
        try {
            EloquaRecordDO eloquaRecordDO = EloquaConnectionManager.getPPRecordByEmail(email, eloquaConfigService);List<EloquaCDOField> values = new LinkedList<>();
            if (eloquaRecordDO != null && EloquaConnectionManagerUtil.getRecordFromSearch(eloquaRecordDO).getFieldValues() != null) {
                values = EloquaConnectionManagerUtil.getRecordFromSearch(eloquaRecordDO).getFieldValues();
                // create a temp array with the key value
                Map<String, String> ppValues = new LinkedHashMap<>();
                for (EloquaCDOField value : values) {
                    // create data item
                    ppValues.put(value.getId(), value.getValue());
                }
                for (Map.Entry<String, String> item : formFields.entrySet()) {
                    String fieldName = item.getKey();
                    String fieldValue = "";
                    String[] arr = FormFieldsUtil.getFieldMappingFromConfig(fieldName, fieldMappingConfigService);
                    String ppId = FieldMapUtils.getPPId(arr);
                    if (StringUtils.isNotBlank(ppId)) {
                        fieldValue = ppValues.get(ppId);
                    }
                    // Set the state, if not state it will not get selected
                    if (fieldName.equals(EloquaConnectionManagerConstants.COMPANY_PROVINCE_FIELD_NAME)) {
                        formFields.put(EloquaConnectionManagerConstants.STATE_FIELD_NAME, fieldValue);
                        formFields.put(EloquaConnectionManagerConstants.PROVINCE_INPUT_FIELD_NAME, fieldValue);
                    }
                    // do not override if value is empty and state and input
                    if (StringUtils.isNotBlank(fieldValue)
                            && !fieldName.equals(EloquaConnectionManagerConstants.STATE_FIELD_NAME)
                            && !fieldName.equals(EloquaConnectionManagerConstants.PROVINCE_INPUT_FIELD_NAME)) {
                        item.setValue(fieldValue);
                    }
                }
            }
        } catch (IOException | NoSuchMethodException | SecurityException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            throw new EDCEloquaSystemException("email: " + email, this.getClass().getName(),
                    "prePopulatePPDataByEMailId", e.toString());
        }
        return formFields;
    }

    @Override
    public Boolean updateUserProfile(EloquaUserProfileDO eloquaUserProfileDO) throws EDCEloquaSystemException {
        // update profile
        Boolean result = false;
        try {
            result = EloquaConnectionManager.updateRecord(eloquaUserProfileDO, eloquaConfigService);
        } catch (IOException e) {
            throw new EDCEloquaSystemException(
                    "eloquaUserProfileDO field values: " + eloquaUserProfileDO.getFormFieldsValues().toString(),
                    this.getClass().getName(), "updateUserProfile", e.toString());
        }
        return result;
    }

    @Override
    public Map<String, String> getTransactionHistoryByExternalId(String externalId) throws EDCEloquaSystemException {
        Map<String, String> pagePaths = new LinkedHashMap<>();
        try {
            String aemPathFieldId = eloquaConfigService.getTransAemPathFieldId();
            EloquaRecordDO eloquaRecordDO = EloquaConnectionManager.getTransactionHistoryByExternalId(externalId,
                    eloquaConfigService);
            pagePaths = EloquaConnectionManagerJsonUtil.transJsonToSet(eloquaRecordDO, aemPathFieldId);
        } catch (JSONException | IOException e) {
            throw new EDCEloquaSystemException("externalId: " + externalId, this.getClass().getName(),
                    "getTransactionHistoryByExternalId", e.toString());
        }

        return pagePaths;
    }

    @Override
    public boolean checkConsentRequestGating(String externalId, String pagePath, String partnersCode,
            THConfigService thConfigService) throws EDCEloquaSystemException {
        boolean result = false;
        try {
            String transactionID = externalId.concat(Constants.PIPE).concat(pagePath);
            String jsonString = EloquaConnectionManager.getTransactionHistoryRecordJson(transactionID,
                    eloquaConfigService);
            THSearchResults results = THJsonDataBindingUtil.jsonToTHSearchResults(jsonString);
            THRecordDO currentRecord = new THRecordDO();
            if (results.getTotal() > 0) {
                currentRecord = results.getElements().get(0);
            }
            String currentCasls = THHelper.getFieldValue(currentRecord .getFieldValues(),
                    thConfigService.getParnersCASLFieldId());
            result = StringUtils.contains(currentCasls, partnersCode);
        } catch (IOException e) {
            String params = "externalId: ".concat(externalId).concat(" pagePath: ").concat(pagePath)
                    .concat(" partnersCode: ").concat(partnersCode);
            throw new EDCEloquaSystemException(params, this.getClass().getName(), "getConsentRequestGating",
                    e.toString());
        }
        return result;
    }

    @Override
    public EloquaConfigService getEloquaConfigService() {
        return eloquaConfigService;
    }

}
