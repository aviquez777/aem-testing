package com.edc.edcportal.core.helpers;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.restful.RestClientConstants;

public class QueryParamsHelper {
    private static final Logger log = LoggerFactory.getLogger(QueryParamsHelper.class);

    private QueryParamsHelper() {
        // Sonar Lint
    }

    public static String[] getUpdateBasicInfoParams(String actionTypeQs) {
        String[] returnArray = new String[2];
        if (StringUtils.isNotBlank(actionTypeQs)) {
            String[] actionTypeArr = actionTypeQs.split(Constants.Properties.QUERY_PARAM_ACTION_TYPE_DELIMITER);
            int arrLength = actionTypeArr.length;
            if (arrLength >= 1) {
                returnArray[0] = actionTypeArr[0];
            }
            if (arrLength == 2) {
                returnArray[1] = actionTypeArr[1];
            } else {
                log.debug(
                        "QueryParamsHelper getUpdateBasicInfoParams Incorrect Query String size from B2C, size: {} array {}",
                        arrLength, actionTypeArr);
            }
        }
        if (StringUtils.isBlank(returnArray[1])) {
            returnArray[1] = Constants.Properties.ERROR_MESSAGE;
            log.debug("QueryParamsHelper getUpdateBasicInfoParams set default type");
        }
        return returnArray;
    }

    public static String[] getProgressivProfileParams(String actionTypeQs) { // NOSONAR
        String[] returnArray = new String[6];
        if (StringUtils.isNotBlank(actionTypeQs)) {
            String[] actionTypeArr = actionTypeQs.split(Constants.Properties.QUERY_PARAM_ACTION_TYPE_PP_DELIMITER);
            int arrLength = actionTypeArr.length;
            if (arrLength >= 1) {
                returnArray[0] = actionTypeArr[0];
            }
            if (arrLength >= 2) {
                returnArray[1] = StringUtils.replace(actionTypeArr[1], Constants.UNDERSCORE, Constants.COLON);
            }
            if (arrLength >= 3) {
                returnArray[2] = StringUtils.replace(actionTypeArr[2],
                        Constants.Properties.SUB_QUERY_PARAM_ACTION_TYPE_DELIMITER,
                        Constants.Properties.SUB_QUERY_PARAM_ACTION_TYPE_AMPERSAND);
            }
            if (arrLength >= 4) {
                returnArray[3] = actionTypeArr[3];
            }
            if (arrLength >= 5) {
                returnArray[4] = actionTypeArr[4];
            }
            if (arrLength == 6) {
                returnArray[5] = actionTypeArr[5];
            }
            if (arrLength < 3) {
                log.debug(
                        "QueryParamsHelper getProgressivProfileParams Incorrect Query String size from EDC, size: {} array {}",
                        arrLength, actionTypeArr);
            }
        }
        if (StringUtils.isBlank(returnArray[0])) {
            returnArray[0] = Constants.Properties.PRODUCT_TYPE_DEFAULT_VALUE;
        }
        if (StringUtils.isBlank(returnArray[1])) {
            returnArray[1] = Constants.Properties.PRODUCT_DESC_DEFAULT_VALUE;
        }
        return returnArray;
    }

    public static String getCampaignQs(String utmQs, SlingHttpServletRequest request) {
        String queryString = "";
        if (StringUtils.isNotBlank(utmQs)) {
            String[] utmArr = utmQs.split(Constants.Properties.QUERY_PARAM_ACTION_TYPE_PP_DELIMITER);
            int arrLength = utmArr.length;
            if (arrLength >= 1) {
                queryString = Constants.Properties.QUERY_PARAM_UTM_SOURCE
                        .concat(Constants.Properties.SUB_QUERY_PARAM_ACTION_TYPE_EQUAL_SIGN).concat(utmArr[0]);
            }
            if (arrLength >= 2) {
                queryString = queryString.concat(Constants.Properties.SUB_QUERY_PARAM_ACTION_TYPE_AMPERSAND)
                        .concat(Constants.Properties.QUERY_PARAM_UTM_MEDIUM)
                        .concat(Constants.Properties.SUB_QUERY_PARAM_ACTION_TYPE_EQUAL_SIGN).concat(utmArr[1]);
            }
            if (arrLength >= 3) {
                queryString = queryString.concat(Constants.Properties.SUB_QUERY_PARAM_ACTION_TYPE_AMPERSAND)
                        .concat(Constants.Properties.QUERY_PARAM_UTM_CAMPAIGN)
                        .concat(Constants.Properties.SUB_QUERY_PARAM_ACTION_TYPE_EQUAL_SIGN).concat(utmArr[2]);
            }
            if (arrLength == 4) {
                queryString = queryString.concat(Constants.Properties.SUB_QUERY_PARAM_ACTION_TYPE_AMPERSAND)
                        .concat(Constants.Properties.QUERY_PARAM_UTM_CONTENT)
                        .concat(Constants.Properties.SUB_QUERY_PARAM_ACTION_TYPE_EQUAL_SIGN).concat(utmArr[3]);
            } else {
                log.debug("QueryParamsHelper getCampaignParams Incorrect Query String size from EDC, size: {} array {}",
                        arrLength, utmArr);
            }
        }
        return queryString;
    }

    public static String getUmtsFromQs(SlingHttpServletRequest request) {
        String queryString = "";
        String[] qsParams = { Constants.Properties.QUERY_PARAM_UTM_SOURCE, Constants.Properties.QUERY_PARAM_UTM_MEDIUM,
                Constants.Properties.QUERY_PARAM_UTM_CAMPAIGN, Constants.Properties.QUERY_PARAM_UTM_CONTENT,
                Constants.Properties.QUERY_PARAM_UTM_TERM };
        boolean useAmp = false;
        for (String qsParam : qsParams) {
            String qsValue = request.getParameter(qsParam);
            if (useAmp) {
                queryString = queryString.concat(Constants.Properties.SUB_QUERY_PARAM_ACTION_TYPE_AMPERSAND);
            }
            if (StringUtils.isNotBlank(qsValue)) {
                queryString = queryString.concat(qsParam).concat(Constants.Properties.SUB_QUERY_PARAM_ACTION_TYPE_EQUAL_SIGN)
                        .concat(qsValue);
                useAmp = true;
            }
        }
        return queryString;
    }

    public static String getRedirectUrl(SlingHttpServletRequest request) {
        String redirectUrl = request.getParameter(Constants.Properties.QUERY_PARAM_REDIRECT_URL);
        if (StringUtils.isNotBlank(redirectUrl)) {
            String rawQs = request.getQueryString();
            // Use the FORM_FIELD_QS_TEXT if post
            if (RestClientConstants.Methods.METHOD_POST.equals(request.getMethod())) {
                rawQs = request.getParameter(Constants.Properties.FORM_FIELD_QS_TEXT);
            }
            redirectUrl = StringUtils.remove(rawQs, Constants.Properties.QUERY_PARAM_REDIRECT_URL
                    .concat(Constants.Properties.SUB_QUERY_PARAM_ACTION_TYPE_EQUAL_SIGN));
        }
        return redirectUrl;
    }
}
