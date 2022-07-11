package com.edc.edcportal.core.transactionhistory.helpers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.json.JSONException;

import com.edc.edcportal.core.eloqua.EloquaConnectionManager;
import com.edc.edcportal.core.eloqua.pojo.EloquaCDO;
import com.edc.edcportal.core.eloqua.services.EloquaConfigService;
import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.CookieHelper;
import com.edc.edcportal.core.transactionhistory.THJsonDataBindingUtil;
import com.edc.edcportal.core.transactionhistory.pojo.THFieldValue;
import com.edc.edcportal.core.transactionhistory.pojo.THRecordDO;
import com.edc.edcportal.core.transactionhistory.pojo.THSearchResults;
import com.edc.edcportal.core.transactionhistory.services.THConfigService;

public class THHelper {

    private THHelper() {
        // Utility classes should not have public constructors
    }

    public static EloquaCDO addOrUpdateRecord(String transactionID, Map<String, String> variables,
            SlingHttpServletRequest request, EloquaConfigService eloquaConfigService, THConfigService thConfigService)
            throws IOException, JSONException {
        // Check if there's a record using transactionID
        String jsonString = EloquaConnectionManager.getTransactionHistoryRecordJson(transactionID, eloquaConfigService);
        THSearchResults results = THJsonDataBindingUtil.jsonToTHSearchResults(jsonString);
        THRecordDO currentRecord = new THRecordDO();
        if (results.getTotal() > 0) {
            currentRecord = results.getElements().get(0);
        }
        EloquaCDO eloquaCDO = new EloquaCDO();
        String body = null;
        // Create the Record and populate later accordingly
        if (currentRecord.getFieldValues().isEmpty()){
            String email = variables.get("email");
            String tcsCSV = variables.getOrDefault("tcsCSV", null);
            String caslsCSV = variables.getOrDefault("caslsCSV", null);
            String witVal = variables.getOrDefault("witVal", null);

            
            String recordID = currentRecord.getId();
            if (StringUtils.isNotBlank(recordID)) {
                // Concat any new TCS
                String currentTcs = getFieldValue(currentRecord.getFieldValues(),
                        thConfigService.getParnersTCFieldId());
                if (StringUtils.isNotBlank(currentTcs) && StringUtils.isNotBlank(tcsCSV)
                        && !currentTcs.contains(tcsCSV)) {
                    tcsCSV = currentTcs.concat(",").concat(tcsCSV);
                }
                // Concat any new Casls
                String currentCasls = getFieldValue(currentRecord.getFieldValues(),
                        thConfigService.getParnersCASLFieldId());
                if (StringUtils.isNotBlank(currentCasls) && StringUtils.isNotBlank(caslsCSV)
                        && !currentCasls.contains(caslsCSV)) {
                    caslsCSV = currentCasls.concat(",").concat(caslsCSV);
                }
                THRecordDO thRecordDO = THDataObjectTransformationHelper.populatTHDataObject(thConfigService, email,
                        null, null, null, null, tcsCSV, caslsCSV, witVal);
                body = THJsonDataBindingUtil.thRecordDOToJson(thRecordDO);
                eloquaCDO = EloquaConnectionManager.updateTransactionHistoryRecord(recordID, body,
                        eloquaConfigService);
            } else {
                String pipeRegex = "\\".concat(Constants.PIPE);
                String[] transactionIDArr = transactionID.split(pipeRegex);
                String externalId = transactionIDArr[0];
                String path = transactionIDArr[1];
                String trackingCookie = CookieHelper.getCookieValue(request, Constants.Properties.TRAFFIC_SOURCE_COOKIE_NAME);
                // Add new record
                THRecordDO thRecordDO = THDataObjectTransformationHelper.populatTHDataObject(thConfigService, email,
                        externalId, path, trackingCookie, 0, tcsCSV, caslsCSV, witVal);
                body = THJsonDataBindingUtil.thRecordDOToJson(thRecordDO);
                eloquaCDO = EloquaConnectionManager.addTransactionHistoryRecord(body, eloquaConfigService);
            }
        }
        return eloquaCDO;
    }

    public static String getFieldValue(List<THFieldValue> fieldList, String fieldId) {
        String theValue = null;
        for (THFieldValue fieldValue : fieldList) {
            if (fieldValue.getId().equals(fieldId)) {
                theValue = fieldValue.getValue();
                break;
            }
        }
        return theValue;
    }

}
