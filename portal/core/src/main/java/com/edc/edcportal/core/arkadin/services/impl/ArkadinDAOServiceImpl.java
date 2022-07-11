package com.edc.edcportal.core.arkadin.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.arkadin.ArkadinConstants;
import com.edc.edcportal.core.arkadin.ArkadinXMLDataBindingUtil;
import com.edc.edcportal.core.arkadin.pojo.ArkadinRegistrationDO;
import com.edc.edcportal.core.arkadin.pojo.ArkadinUserActivityPO;
import com.edc.edcportal.core.arkadin.services.ArkadinConfigService;
import com.edc.edcportal.core.arkadin.services.ArkadinDAOService;
import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.restful.RestClientConstants;

@Component(immediate = true, service = ArkadinDAOService.class)
public class ArkadinDAOServiceImpl implements ArkadinDAOService {

    private static final Logger logger = LoggerFactory.getLogger(ArkadinDAOServiceImpl.class);

    @Reference
    private ArkadinConfigService arkadinConfigService;

    @Override
    public ArkadinRegistrationDO registerUserToWebinar(String externalUserID, String showKey) {
        ArkadinRegistrationDO arkadinRegistrationDO = new ArkadinRegistrationDO();
        try {
            String endPoint = arkadinConfigService.getAPIUrl();
            String userCode = arkadinConfigService.getAPIUserAuthCode();
            String userCredentials = arkadinConfigService.getAPIUserCredentials(); 
            endPoint = endPoint.concat(arkadinConfigService.getLASCmd())
                    .concat(ArkadinConstants.USER_AUTH_CODE).concat(userCode)
                    .concat(ArkadinConstants.USER_CREDENTIALS).concat(userCredentials)
                    .concat(ArkadinConstants.OP_CODE_LIST)
                    .concat(ArkadinConstants.OpCodes.ARKADIN_OPCODE_REGISTER_TO_WEBINAR.getOpCode()
                            .concat(ArkadinConstants.OUTPUT_FORMAT).concat("X")
                            .concat(ArkadinConstants.LOOKUP_BY_USERID).concat("1")
                            .concat(ArkadinConstants.EXTERNAL_USERID).concat(externalUserID)
                            .concat(ArkadinConstants.SHOWKEY).concat(showKey)
                            .concat(ArkadinConstants.TRIGGER_REG_EVENTS).concat("1"));
            URL url = new URL(endPoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // Task 221435 CQRules:ConnectionTimeoutMechanism
            conn.setConnectTimeout(RestClientConstants.CONNECT_TIMEOUT);
            conn.setReadTimeout(RestClientConstants.READ_TIMEOUT);
            conn.setRequestProperty(RestClientConstants.USER_AGENT_KEY, RestClientConstants.USER_AGENT_VALUE);
            InputStream responseStream = conn.getInputStream();
            arkadinRegistrationDO = ArkadinXMLDataBindingUtil.xmlToArkadinRegistrationDO(responseStream);

        } catch (IOException e) {
            logger.error("ApimDAOServiceImpl getRecordByUniqueCode: {}", e.getStackTrace());
        }
        return arkadinRegistrationDO;
    }

    @Override
    public ArkadinRegistrationDO getUserRegistrationStatus(String externalUserID, String showKey) {
        ArkadinRegistrationDO arkadinRegistrationDO = new ArkadinRegistrationDO();
        try {
            String endPoint = arkadinConfigService.getAPIUrl();
            String userCode = arkadinConfigService.getAPIUserAuthCode();
            String userCredentials = arkadinConfigService.getAPIUserCredentials();
            endPoint = endPoint.concat(arkadinConfigService.getLASCmd())
                    .concat(ArkadinConstants.USER_AUTH_CODE).concat(userCode)
                    .concat(ArkadinConstants.USER_CREDENTIALS).concat(userCredentials)
                    .concat(ArkadinConstants.OP_CODE_LIST)
                    .concat(ArkadinConstants.OpCodes.ARKADIN_OPCODE_GET_WEBINAR_REG_STATUS.getOpCode()
                            .concat(ArkadinConstants.OUTPUT_FORMAT).concat("X")
                            .concat(ArkadinConstants.LOOKUP_BY_USERID).concat("1")
                            .concat(ArkadinConstants.EXTERNAL_USERID).concat(externalUserID)
                            .concat(ArkadinConstants.SHOWKEY).concat(showKey)); 
            URL url = new URL(endPoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // Task 221435 CQRules:ConnectionTimeoutMechanism
            conn.setConnectTimeout(RestClientConstants.CONNECT_TIMEOUT);
            conn.setReadTimeout(RestClientConstants.READ_TIMEOUT);
            conn.setRequestProperty(RestClientConstants.USER_AGENT_KEY, RestClientConstants.USER_AGENT_VALUE);
            InputStream responseStream = conn.getInputStream();
            arkadinRegistrationDO = ArkadinXMLDataBindingUtil.xmlToArkadinRegistrationDO(responseStream);

        } catch (IOException e) {
            logger.error("ApimDAOServiceImpl getRecordByUniqueCode: ", e);
        }
        return arkadinRegistrationDO;
    }

    @Override
    public ArkadinUserActivityPO getUserActivity(String externalUserID) {
        ArkadinUserActivityPO arkadinUserActivityPO = new ArkadinUserActivityPO();
        try {
            String endPoint = arkadinConfigService.getAPIUrl();
            String userCode = arkadinConfigService.getAPIUserAuthCode();
            String userCredentials = arkadinConfigService.getAPIUserCredentials();
            endPoint = endPoint.concat(arkadinConfigService.getStudioShowRegistrationLASCmd())
                    .concat(ArkadinConstants.USER_AUTH_CODE).concat(userCode)
                    .concat(ArkadinConstants.USER_CREDENTIALS).concat(userCredentials)
                    .concat(ArkadinConstants.OUTPUT_FORMAT).concat("X")
                    .concat(ArkadinConstants.LOOKUP_BY_USERID).concat("1")
                    .concat(ArkadinConstants.EXTERNAL_USERID).concat(externalUserID);
            URL url = new URL(endPoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // Task 221435 CQRules:ConnectionTimeoutMechanism
            conn.setConnectTimeout(RestClientConstants.CONNECT_TIMEOUT);
            conn.setReadTimeout(RestClientConstants.READ_TIMEOUT);
            conn.setRequestProperty(RestClientConstants.USER_AGENT_KEY, RestClientConstants.USER_AGENT_VALUE);
            InputStream responseStream = conn.getInputStream();
            arkadinUserActivityPO = ArkadinXMLDataBindingUtil.xmlToArkadinUserActivityPO(responseStream);
        } catch (IOException e) {
            arkadinUserActivityPO.setDiag(Constants.Properties.ERROR_MESSAGE);
            logger.error("ApimDAOServiceImpl getRecordByUniqueCode: ", e);
        }
        return arkadinUserActivityPO;
    }

}
