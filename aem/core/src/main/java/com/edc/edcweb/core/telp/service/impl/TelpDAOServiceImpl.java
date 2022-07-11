package com.edc.edcweb.core.telp.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.Request;
import com.edc.edcweb.core.helpers.ResourceResolverHelper;
import com.edc.edcweb.core.helpers.formvalidation.FormCleaner;
import com.edc.edcweb.core.restful.RestClient;
import com.edc.edcweb.core.restful.RestClientConstants;
import com.edc.edcweb.core.telp.TELPJsonDataBindingUtil;
import com.edc.edcweb.core.telp.TelpAbstractDAO;
import com.edc.edcweb.core.telp.helpers.DataObjectTransformationHelper;
import com.edc.edcweb.core.telp.helpers.TelpConstants;
import com.edc.edcweb.core.telp.pojo.TelpFormDOPart1;
import com.edc.edcweb.core.telp.pojo.TelpFormDOPart2;
import com.edc.edcweb.core.telp.service.TelpDAOService;
import com.edc.edcweb.core.telp.service.TelpService;

@Component(immediate = true, service = TelpDAOService.class)
public class TelpDAOServiceImpl extends TelpAbstractDAO implements TelpDAOService {

    private static final Logger log = LoggerFactory.getLogger(TelpDAOServiceImpl.class);

    @Reference
    private TelpService telpService;

    @Override
    public JSONObject submitForm(SlingHttpServletRequest request) {
        JSONObject responseJson = new JSONObject();
        String formType = getFormType(request);
        // Telp form was done in two parts to allow the Ultimate Beneficial Owners to be
        // generated dynamically generated and properly positioned on the response JSON
        TelpFormDOPart1 part1 = DataObjectTransformationHelper.populateTelpForm1FromPostedData(request, formType);
        TelpFormDOPart2 part2 = DataObjectTransformationHelper.populateTelpForm2FromPostedData(request, formType);
        String telpJson = TELPJsonDataBindingUtil.telpFormDOToJson(part1, part2);
        log.info("TELP / COVIDR / MMG Json {}", telpJson);
        // Got the formPrepare the REST Call
        HashMap<String, String> formParams = new HashMap<>();
        formParams.put(TelpConstants.PostFormFields.FIELD_TYPE.getName(), formType);
        formParams.put(TelpConstants.PostFormFields.FIELD_DATA.getName(), telpJson);
        try {
            String endPoint = telpService.getTelpProxyUrl();
            endPoint = endPoint.concat(telpService.getTelpBaseEndpoint());
            String accessToken = getToken(telpService);
            String authorization = RestClientConstants.AuthMethods.BEARER_TOKEN.concat(accessToken);
            Map<String, String> headers = new HashMap<>();
            headers.put(TelpConstants.HeaderParams.OCP_APIM_SUB_KEY.getHeaderValue(),
                    telpService.getOCPTelpsubscriptionKey());
            responseJson = RestClient.doMultiPartPost(endPoint, authorization, headers, formParams, null);
            // no news are good news
        } catch (IOException | JSONException e) {
            log.error("TelpDAOServiceImpl submitForm params: {} response Json {}", formParams, responseJson);
            log.error("TelpDAOServiceImpl submitForm param {}", e.toString());
        }
        return responseJson;
    }

    private String getFormType(SlingHttpServletRequest request) {
        // Task 110679 Read form Type from property
        // Deine form code to send as type in the API Parameter
        Resource pageRes = Request.getCurrentPageResource(request, null);
        Page currentPage = pageRes.adaptTo(Page.class);
        // Find the FORM
        Resource telpRes = ResourceResolverHelper.getResourceByType(currentPage, TelpConstants.FORM_RESOURCE_TYPE);
        String formType = telpRes.getValueMap().get(TelpConstants.FORM_TYPE_PROPERTY, TelpConstants.DEFAULT_FORM_TYPE);
        boolean preScreen = Boolean
                .parseBoolean(telpRes.getValueMap().get(TelpConstants.PRE_SCREEN_PROPERTY, String.class));
        // Check only if not telp
        if (!TelpConstants.DEFAULT_FORM_TYPE.equals(formType)) {
            String exporterType = FormCleaner
                    .cleanAll(request.getParameter(TelpConstants.FormFields.EXPORTER_TYPE.getFieldName()));
            String formVersion = formType;
            // if we have a pre-screen use value sent by javascript
            if (preScreen) {
                formVersion = StringUtils.upperCase(FormCleaner
                        .cleanAll(request.getParameter(TelpConstants.FormFields.FORM_VERSION.getFieldName())));
            }
            // BCAP (aka COVID)
            if (formVersion.startsWith(TelpConstants.FORM_VALUE_BCAP)) {
                formType = TelpConstants.COVIDR_E_FORM_TYPE;
                if (TelpConstants.DOMESTIC_TYPE.equals(exporterType)) {
                    formType = TelpConstants.COVIDR_D_FORM_TYPE;
                }
            }
            // MMG
            if (formVersion.startsWith(TelpConstants.FORM_VALUE_MMG)) {
                formType = TelpConstants.MMG_E_FORM_TYPE;
                if (TelpConstants.DOMESTIC_TYPE.equals(exporterType)) {
                    formType = TelpConstants.MMG_D_FORM_TYPE;
                }
            }
            // BCAP-Extension US 331242
            if (formVersion.equals(TelpConstants.FORM_VALUE_BCAP_EXT)) {
                formType = TelpConstants.BCAP_EXT_FORM_TYPE;
            }
        }
        return formType;
    }

    @Override
    public JSONObject getFiLov() {
        JSONObject responseJson = new JSONObject();
        try {
            String endPoint = telpService.getTelpProxyUrl();
            endPoint = endPoint.concat(telpService.getTelpBaseEndpoint());
            String accessToken = getToken(telpService);
            String authorization = RestClientConstants.AuthMethods.BEARER_TOKEN.concat(accessToken);
            Map<String, String> headers = new HashMap<>();
            headers.put(TelpConstants.HeaderParams.OCP_APIM_SUB_KEY.getHeaderValue(),
                    telpService.getOCPTelpsubscriptionKey());
            responseJson = RestClient.doGet(endPoint, authorization, headers, null, headers);
            // no news are good news
        } catch (IOException | JSONException e) {
            log.error("TelpDAOServiceImpl getFiLov response Json {}", responseJson);
        }
        return responseJson;
    }

}
