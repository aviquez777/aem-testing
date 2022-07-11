package com.edc.edcweb.core.servlets.onlinepayments;

import java.io.IOException;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.gatedcontentaccess.helpers.GatedContentAccessHelper;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.EncryptUtils;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.ServletResponseHelper;
import com.edc.edcweb.core.helpers.formvalidation.FormCleaner;
import com.edc.edcweb.core.onlinepayments.OPJsonDataBindingUtil;
import com.edc.edcweb.core.onlinepayments.helpers.ConstantsOP;
import com.edc.edcweb.core.onlinepayments.pojo.digitalpayments.DPRequest;
import com.edc.edcweb.core.onlinepayments.pojo.edcweb.TicketNumberResponse;
import com.edc.edcweb.core.onlinepayments.pojo.moneris.ReceiptResponse;
import com.edc.edcweb.core.onlinepayments.pojo.moneris.common.CustInfo;
import com.edc.edcweb.core.onlinepayments.pojo.moneris.common.MonerisError;
import com.edc.edcweb.core.onlinepayments.services.DigitalPaymentsDAOService;
import com.edc.edcweb.core.onlinepayments.services.MonerisDAOService;
import com.fasterxml.jackson.core.JsonProcessingException;

@SuppressWarnings("CQRules:CQBP-75")
@Component(immediate = true, service = Servlet.class, property = { "sling.servlet.extensions=json",
        "sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.paths=" + ConstantsOP.SERVLET_URL_STEP_1,
        "sling.servlet.paths=" + ConstantsOP.SERVLET_URL_STEP_3 })

public class OPServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 4719301702351202236L;

    private static final Logger log = LoggerFactory.getLogger(OPServlet.class);

    @Reference
    @Inject
    private MonerisDAOService monerisDAOService;

    @Reference
    @Inject
    private DigitalPaymentsDAOService digitalPaymentsDAOService;

    @Reference
    @Inject
    private SlingSettingsService slingSettingsService;

    private String errorMsg = ConstantsOP.API_ERROR_MSG;
    private boolean formFound = false;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        String json = returnJsonError(request);
        // Proceed Only if have no Query String and form on page
        if (StringUtils.isEmpty(request.getQueryString()) && formFound) {
            String language = LanguageUtil.getLanguageAbbreviationFromPath(request.getHeader(Constants.Properties.REFERER),
                            Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
            String currency = FormCleaner.cleanAll(request.getParameter(ConstantsOP.PPS_CURRENCY_FIELD_NAME));
            // Preload Request
            if (request.getPathInfo().contains(ConstantsOP.SERVLET_URL_STEP_1)) {
                // Get required values from form
                String txnTotal = FormCleaner.checkCCAmount(
                        request.getParameter(ConstantsOP.PPS_AMOUNT_FIELD_NAME));
                String companyName =  FormCleaner.cleanAll(request.getParameter(ConstantsOP.PPS_COMPANY_NAME_FIELD_NAME), false, 50);
                CustInfo custInfo = new CustInfo();
                custInfo.setFirstName(FormCleaner.cleanAll(request.getParameter(ConstantsOP.PPS_FIRST_NAME_FIELD_NAME)));
                custInfo.setLastName(FormCleaner.cleanAll(request.getParameter(ConstantsOP.PPS_LAST_NAME_FIELD_NAME)));
                json = doStep1(json, currency, txnTotal, language, companyName, custInfo);
            }
            // Receipt Request
            if (request.getPathInfo().contains(ConstantsOP.SERVLET_URL_STEP_3)) {
                // Get values from form
                String ticketNumber = FormCleaner.cleanAll(request.getParameter(ConstantsOP.FORM_TICKET_FIELD_NAME));
                json = doStep3(currency, ticketNumber, request, language);
            }
        }
        // Log only on lower environments
        ServletResponseHelper.writeResponse(response, json);
    }

    // Helper Classes
    private String doStep1(String json, String currency, String txnTotal, String language, String companyName, CustInfo custInfo) {
        // Get the response only if we have values
        if (StringUtils.isNotBlank(txnTotal) && StringUtils.isNotBlank(currency)) {
            json = monerisDAOService.getMonerisTicketNumber(txnTotal, currency, language, companyName, custInfo);
            log.debug("Preload response data: {}", json);
        }
        return json;
    }

    private String doStep3(String currency, String ticketNumber, SlingHttpServletRequest request, String language) {
        String json;
        // Get the response
        ReceiptResponse receiptResponse = monerisDAOService.getMonerisReceipt(ticketNumber, currency);
        // Check for response, set declined as default
        String transResult = ConstantsOP.MONERIS_RECEIPT_RESULT_DECLINED_VALUE;
        if (receiptResponse.getResponse().getReceipt() != null) {
            transResult = receiptResponse.getResponse().getReceipt().getResult();
        }
        String apiResult = null;
        if (ConstantsOP.MONERIS_RECEIPT_RESULT_APPROVED_VALUE.equals(transResult)) {
            DPRequest dpRequest = digitalPaymentsDAOService.populateDPRequest(receiptResponse.getResponse(), request, language);
            // If prod, do not add the result in order to not expose unnecessary data
            Set<String> runModes = this.slingSettingsService.getRunModes();
            boolean isProd = (runModes != null && runModes.contains("prod"));
            apiResult = digitalPaymentsDAOService.postDigitalPaymentToApi(dpRequest, isProd);
        }
        json = monerisDAOService.getTransactionResponseJSON(receiptResponse, apiResult);
        log.debug("Receipt response data: {}", json);
        return json;
    }

    private String returnJsonError(SlingHttpServletRequest request) {
        Resource formRes = getFormRes(request);
        String jsonError = null;
        this.formFound = formRes != null;
        if (this.formFound) {
            this.errorMsg = formRes.getValueMap().get(ConstantsOP.ERROR_TEXT_PROPERTY, String.class);
        }
        // Prepare a default error json
        TicketNumberResponse transactionResponse = new TicketNumberResponse();
        MonerisError error = new MonerisError();
        error.setField(ConstantsOP.API_ERROR_KEY);
        error.setMessage(this.errorMsg);
        transactionResponse.setError(error);
        try {
            jsonError = OPJsonDataBindingUtil.pojoToJson(transactionResponse);
        } catch (JsonProcessingException e) {
            log.error("OPServlet returnJsonError failed processing Json {}", transactionResponse, e);
        }
        return jsonError;
    }

    private Resource getFormRes(SlingHttpServletRequest request) {
        Resource formRes = null;
        String encrypted = request.getParameter(ConstantsOP.CHECK_PATH_FIELD);
        try {
            String overridePath = EncryptUtils.decryptString(encrypted);
            formRes = GatedContentAccessHelper.getFormRes(ConstantsOP.RESOURCE_TYPE, request, overridePath);
        } catch (NullPointerException | IllegalArgumentException e) {
            log.debug("OPServlet getFormRes error", e);
        }
        return formRes;
    }
}