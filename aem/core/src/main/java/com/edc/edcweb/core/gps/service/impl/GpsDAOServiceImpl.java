package com.edc.edcweb.core.gps.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.sling.api.request.RequestParameter;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.apim.ConstantsAPIM;
import com.edc.edcweb.core.gps.GpsAbstractDAO;
import com.edc.edcweb.core.gps.GpsJsonDataBindingUtil;
import com.edc.edcweb.core.gps.helpers.GpsConstants;
import com.edc.edcweb.core.gps.pojo.GpsResponse;
import com.edc.edcweb.core.gps.service.GpsDAOService;
import com.edc.edcweb.core.gps.service.GpsService;
import com.edc.edcweb.core.restful.RestClientApacheHttpClient;
import com.edc.edcweb.core.restful.RestClientConstants;
import com.fasterxml.jackson.core.JsonProcessingException;

@Component(immediate = true, service = GpsDAOService.class)
public class GpsDAOServiceImpl extends GpsAbstractDAO implements GpsDAOService {

    private static final Logger log = LoggerFactory.getLogger(GpsDAOServiceImpl.class);

    @Reference
    private GpsService gpsService;

    @Override
    public GpsResponse submitForm(String formType, String formJson, List<RequestParameter> attchmentList,
            boolean returnConfNum) {
        GpsResponse gpsResponse;
        JSONObject responseJson = new JSONObject();
        // Prepare the form submission
        HashMap<String, String> formParams = new HashMap<>();
        formParams.put(GpsConstants.PostFormFields.FIELD_TYPE.getName(), formType);
        formParams.put(GpsConstants.PostFormFields.FIELD_DATA.getName(), formJson);
        try {
            String endPoint = gpsService.getGpsProxyUrl();
            endPoint = endPoint.concat(gpsService.getGpsBaseEndpoint());
            String accessToken = getToken(gpsService);
            String authorization = RestClientConstants.AuthMethods.BEARER_TOKEN.concat(accessToken);
            Map<String, String> headers = new HashMap<>();
            headers.put(ConstantsAPIM.HeaderParams.OCP_APIM_SUB_KEY.getHeaderValue(), gpsService.getOCPGpsSubKey());
            responseJson = RestClientApacheHttpClient.doMultipartPost(endPoint, authorization, headers, formParams, attchmentList);
            // no news are good news
        } catch (IOException | JSONException e) {
            log.error("GpsDAOServiceImpl submitForm params: {} response Json {}", formParams, responseJson);
            log.error("GpsDAOServiceImpl submitForm param {}", e.toString());
        }
        // Get the response
        try {
            gpsResponse = GpsJsonDataBindingUtil.jsonToGpsResponse(responseJson.toString());
        } catch (JsonProcessingException e) {
            gpsResponse = new GpsResponse();
            log.error("GpsDAOServiceImpl submitForm, error converting responseJson {}, {}", responseJson,
                    e.getStackTrace());
        }
        // Check if we need to return the "Clean" Confirmation number
        if (returnConfNum) {
            try {
                String confirmationNumber = gpsResponse.getGpsResult().getGpsIdentifier();
                gpsResponse.setConfirmationNumber(confirmationNumber.replaceAll("[^\\d.]", ""));
            } catch (Exception e) {
                log.error(
                        "GpsDAOServiceImpl returnConfNum, error reading gpsResponse.getGpsResult().getGpsIdentifier() {}, {}",
                        responseJson, e.getStackTrace());
            }
        }
        // Return the new response
        return gpsResponse;
    }

}
