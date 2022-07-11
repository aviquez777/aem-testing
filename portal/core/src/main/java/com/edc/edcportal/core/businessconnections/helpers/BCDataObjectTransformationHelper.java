package com.edc.edcportal.core.businessconnections.helpers;

import java.util.Map;

import org.apache.sling.api.SlingHttpServletRequest;

import com.edc.edcportal.core.businessconnections.pojo.BCRecordDO;
import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.LoginRequestHeadersUtil;

public class BCDataObjectTransformationHelper {

    private BCDataObjectTransformationHelper() {
        // Sonar Lint
    }

    public static BCRecordDO populateBCRecordDO(SlingHttpServletRequest request) {
        Map<String, String> headers = LoginRequestHeadersUtil.getHeaders(request);
        BCRecordDO bcRecordDO = new BCRecordDO();
        bcRecordDO.setSurname(headers.get(Constants.Properties.HEADER_LAST_NAME));
        bcRecordDO.setGivenName(headers.get(Constants.Properties.HEADER_FIRST_NAME));
        bcRecordDO.setEmail(headers.get(Constants.Properties.HEADER_EMAIL_ID));
        bcRecordDO.setMobile(headers.get(Constants.Properties.HEADER_MOBILE_NUMBER));
        bcRecordDO.setCreatedDateTime(headers.get(Constants.Properties.HEADER_CREATED_DATE_TIME));
        bcRecordDO.setObjectId(headers.get(Constants.Properties.HEADER_EXTERNAL_ID));
        return bcRecordDO;
    }
}
