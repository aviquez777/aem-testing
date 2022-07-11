package com.edc.edcportal.core.transactionhistory.helpers;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.transactionhistory.pojo.THFieldValue;
import com.edc.edcportal.core.transactionhistory.pojo.THRecordDO;
import com.edc.edcportal.core.transactionhistory.services.THConfigService;

public class THDataObjectTransformationHelper {

    private THDataObjectTransformationHelper() {
        // Sonar Lint
    }

    /**
     * 
     * @param thConfigService
     * @param email
     * @param externalId
     * @param path
     * @param trafficsrc
     * @param counter
     * @param tcsCSV
     * @param witVal
     * @param caslC
     * @return
     */
    public static THRecordDO populatTHDataObject(THConfigService thConfigService, String email, String externalId,
            String path, String trafficsrc, Integer counter, String tcsCSV, String caslsCSV, String witVal) {
        THRecordDO thRecordDO = new THRecordDO();
        List<THFieldValue> fieldValues = new LinkedList<>();
        /// Add Email Address
        THFieldValue emailFV = new THFieldValue();
        emailFV.setId(thConfigService.getEmailAdressFieldId());
        emailFV.setValue(email);
        fieldValues.add(emailFV);

        /// Add ACC ExternalID if present
        if (StringUtils.isNotBlank(externalId)) {
            THFieldValue extId = new THFieldValue();
            extId.setId(thConfigService.getExternalIDFieldId());
            extId.setValue(externalId);
            fieldValues.add(extId);
        }

        /// Add AEM path if present
        if (StringUtils.isNotBlank(path)) {
            THFieldValue pathFV = new THFieldValue();
            pathFV.setId(thConfigService.getAEMpathFieldId());
            pathFV.setValue(path);
            fieldValues.add(pathFV);
        }

        /// Add ACC ExternalID | AEM path if present
        if (StringUtils.isNotBlank(externalId) && StringUtils.isNotBlank(path)) {
            THFieldValue extpathFV = new THFieldValue();
            extpathFV.setId(thConfigService.getExternalIDAEMPathFieldId());
            extpathFV.setValue(externalId.concat(Constants.PIPE).concat(path));
            fieldValues.add(extpathFV);
        }

        /// Add Counter if present
        if (counter != null) {
            THFieldValue counterFV = new THFieldValue();
            counterFV.setId(thConfigService.getCounterFieldId());
            counter++;
            String counterStr = counter.toString();
            counterFV.setValue(counterStr);
            fieldValues.add(counterFV);
        }

        /// Add Traffic SRC if present
        if (trafficsrc != null) {
            THFieldValue trafficSrcFV = new THFieldValue();
            trafficSrcFV.setId(thConfigService.getEloquaTrafficSourceFieldId());
            trafficSrcFV.setValue(trafficsrc);
            fieldValues.add(trafficSrcFV);
        }

        /// Add Terms & Conditions if present
        if (StringUtils.isNotBlank(tcsCSV)) {
            THFieldValue tcsCSVFV = new THFieldValue();
            tcsCSVFV.setId(thConfigService.getParnersTCFieldId());
            tcsCSVFV.setValue(tcsCSV);
            fieldValues.add(tcsCSVFV);
        }

        /// Add Terms & Conditions if present
        if (StringUtils.isNotBlank(caslsCSV)) {
            THFieldValue caslsFV = new THFieldValue();
            caslsFV.setId(thConfigService.getParnersCASLFieldId());
            caslsFV.setValue(caslsCSV);
            fieldValues.add(caslsFV);
        }

        /// Add WIT Value if present
        if (StringUtils.isNotBlank(witVal)) {
            THFieldValue witFV = new THFieldValue();
            witFV.setId(thConfigService.getWitFieldId());
            witFV.setValue(witVal);
            fieldValues.add(witFV);
        }

        // Update Time stamp field
        Instant instant = Instant.now();
        long timeStamp = instant.getEpochSecond();
        THFieldValue timeStampFV = new THFieldValue();
        timeStampFV.setId(thConfigService.getTimestampFieldId());
        timeStampFV.setValue(Long.toString(timeStamp));
        fieldValues.add(timeStampFV);

        // Add FV to record
        thRecordDO.setFieldValues(fieldValues);
        return thRecordDO;
    }
}
