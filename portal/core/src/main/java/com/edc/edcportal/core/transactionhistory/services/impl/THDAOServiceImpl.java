package com.edc.edcportal.core.transactionhistory.services.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.json.JSONException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.edc.edcportal.core.eloqua.pojo.EloquaCDO;
import com.edc.edcportal.core.eloqua.services.EloquaConfigService;
import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.LanguageUtil;
import com.edc.edcportal.core.helpers.LoginRequestHeadersUtil;
import com.edc.edcportal.core.helpers.WebinarsHelper;
import com.edc.edcportal.core.helpers.constants.ConstantsWebinars;
import com.edc.edcportal.core.helpers.formvalidation.FormCleaner;
import com.edc.edcportal.core.transactionhistory.helpers.THHelper;
import com.edc.edcportal.core.transactionhistory.services.THConfigService;
import com.edc.edcportal.core.transactionhistory.services.THDAOService;

@Component(immediate = true, service = THDAOService.class)
public class THDAOServiceImpl implements THDAOService {

    private static final Logger log = LoggerFactory.getLogger(THDAOServiceImpl.class);

    @Reference
    private EloquaConfigService eloquaConfigService;

    @Reference
    private THConfigService thConfigService;

    @Override
    public Boolean doWebinarTrackingRecord(SlingHttpServletRequest request, Page myPage) {
        String path = myPage.getPath();
        Map<String, String> partnersArr = WebinarsHelper.getPartnerArr(myPage);
        // get the external id
        Map<String, String> headers = LoginRequestHeadersUtil.getHeaders(request);
        Map<String, String> variables = new HashMap<>();
        String externalId = headers.get(Constants.Properties.HEADER_EXTERNAL_ID);
        // If no redirect to use current path
        if (StringUtils.isBlank(path)) {
            path = request.getPathInfo();
        }
        String pageLanguage = LanguageUtil.getLanguageAbbreviationFromPath(path,
                Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation().toLowerCase());
        // Remove content/edc if present
        path = StringUtils.replace(path, Constants.Paths.CONTENT_EDC, "");
        // Remove /lang
        path = StringUtils.replace(path, Constants.FORWARD_SLASH.concat(pageLanguage), "");
        EloquaCDO eloquaCDO = new EloquaCDO();
        // Do not update T&C and CALS if is "On Demand")
        String webinarStatus = WebinarsHelper.getWebinarStatus(myPage, 0);
        if (!ConstantsWebinars.WEBINAR_STATE_ONDEMAND.equals(webinarStatus)) {
            // Create partner's T&C CSV with all the partners
            variables.put("tcsCSV", String.join(Constants.COMMA, partnersArr.values()));
            // Create partner's CASL CSV from selected check boxes
            variables.put("caslsCSV", getCaslCSV(request, partnersArr));
        }
        // Get the wit radio value
        String witVal = request.getParameter(ConstantsWebinars.WIT_RADIO_FIELD);
        // if no radio present might not have selected or radio is not there, let's
        // check the page properties
        if (StringUtils.isBlank(witVal)) {
            witVal = WebinarsHelper.getDefaultWitValue(myPage);
        }
        variables.put("witVal", witVal);
        // Update the Transaction CDO
        try {
            variables.put("email", headers.get(Constants.Properties.HEADER_EMAIL_ID));
            String transactionID = externalId.concat(Constants.PIPE).concat(path);
            eloquaCDO = THHelper.addOrUpdateRecord(transactionID, variables, request, eloquaConfigService, thConfigService);
        } catch (IOException | JSONException e) {
            log.error("THDAOServiceImpl error updating record {} ", e.getStackTrace());
        }
        return eloquaCDO.getErrorMessage() == null;
    }

    /**
     * getCaslCSV do a CSV with the selected CASL check boxes only
     * 
     * @param request
     * @param partnersArr
     * @return
     */
    private String getCaslCSV(SlingHttpServletRequest request, Map<String, String> partnersArr) {
        String caslCSV = null;
        for (Map.Entry<String, String> partner : partnersArr.entrySet()) {
            boolean isChecked = FormCleaner.getBoolean(request.getParameter(partner.getKey()));
            if (isChecked) {
                String partnerValue = partner.getValue();
                if (StringUtils.isBlank(caslCSV)) {
                    caslCSV = partnerValue;
                } else {
                    caslCSV = caslCSV.concat(Constants.COMMA).concat(partnerValue);
                }
            }
        }
        return caslCSV;
    }
}
