package com.edc.edcweb.core.onlinepayments.services.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.apicommon.service.ApiCommonConstants;
import com.edc.edcweb.core.apicommon.service.helpers.ApiCommonTokenHelper;
import com.edc.edcweb.core.helpers.formvalidation.FormCleaner;
import com.edc.edcweb.core.onlinepayments.helpers.ConstantsOP;
import com.edc.edcweb.core.onlinepayments.helpers.DigitalPaymentsAPIHelper;
import com.edc.edcweb.core.onlinepayments.pojo.digitalpayments.DPAccount;
import com.edc.edcweb.core.onlinepayments.pojo.digitalpayments.DPPayment;
import com.edc.edcweb.core.onlinepayments.pojo.digitalpayments.DPRequest;
import com.edc.edcweb.core.onlinepayments.pojo.moneris.receipt.RecResponse;
import com.edc.edcweb.core.onlinepayments.services.DigitalPaymentsConfigService;
import com.edc.edcweb.core.onlinepayments.services.DigitalPaymentsDAOService;
import com.edc.edcweb.core.restful.RestClientConstants;

@Component(immediate = true, service = DigitalPaymentsDAOService.class)
public class DigitalPaymentsDAOServiceImpl implements DigitalPaymentsDAOService {

    private static final Logger log = LoggerFactory.getLogger(DigitalPaymentsDAOServiceImpl.class);

    @Reference
    private DigitalPaymentsConfigService digitalPaymentsConfigService;

    @Override
    public String postDigitalPaymentToApi(DPRequest dpRequest, boolean isProd) {
        // Let's assume we have no access
        String response = ApiCommonConstants.TOKEN_ERROR_MSG;
        String apiUrl = digitalPaymentsConfigService.getDPProxyUrl()
                .concat(digitalPaymentsConfigService.getDPBaseEndpoint());
        // Get Token
        String accessToken = getToken(digitalPaymentsConfigService);
        // No token, don't bother
        if (!ApiCommonConstants.TOKEN_ERROR_MSG.equals(accessToken)) {
            // Prepare Headers
            Map<String, String> headers = new HashMap<>();
            headers.put(ApiCommonConstants.HeaderParams.OCP_APIM_SUB_KEY.getHeaderValue(),
                    digitalPaymentsConfigService.getOCPSubscriptionKey());
            String authorization = RestClientConstants.AuthMethods.BEARER_TOKEN.concat(accessToken);
            headers.put(RestClientConstants.AUTHORIZATION, authorization);
            response = DigitalPaymentsAPIHelper.postDigtalPayment(apiUrl, dpRequest, headers);
            // If prod, do not add the result in order to not expose unnecessary data
            if (isProd) {
                response = null;
            } else {
                log.debug("postDigitalPaymentToApi ApiRespones {}", response);
            }
        }
        return response;
    }

    @Override
    public DPRequest populateDPRequest(RecResponse recResponse, SlingHttpServletRequest request, String language) {
        DPRequest dpRequest = new DPRequest();
        // Create and Populate account object
        DPAccount account = new DPAccount();
        account.setEdcId(FormCleaner.cleanAll(request.getParameter(ConstantsOP.PPS_EDC_ID_FIELD_NAME)));
        account.setCompanyName(FormCleaner.cleanAll(request.getParameter(ConstantsOP.PPS_COMPANY_NAME_FIELD_NAME)));
        account.setFirstName(FormCleaner.cleanAll(request.getParameter(ConstantsOP.PPS_FIRST_NAME_FIELD_NAME)));
        account.setLastName(FormCleaner.cleanAll(request.getParameter(ConstantsOP.PPS_LAST_NAME_FIELD_NAME)));
        account.setEmailAddress(FormCleaner.cleanAll(request.getParameter(ConstantsOP.PPS_EMAIL_FIELD_NAME)));
        account.setReferenceNumber(
                FormCleaner.cleanAll(request.getParameter(ConstantsOP.PPS_POLICY_NUMBER_FIELD_NAME)));
        account.setLanguage(StringUtils.upperCase(language));
        dpRequest.setAccount(account);
        // Create and Populate payment object
        DPPayment payment = new DPPayment();
        String monerisDate = recResponse.getReceipt().getCc().getTransactionDateTime();
        payment.setDate(parseMonerisDate(monerisDate));
        payment.setId(recResponse.getReceipt().getCc().getReferenceNo());
        payment.setAmount(Float.parseFloat(recResponse.getReceipt().getCc().getAmount()));
        payment.setCreditCardLast4Digits(StringUtils.right(recResponse.getReceipt().getCc().getFirst6last4(), 4));
        payment.setCreditCardTypeCode(recResponse.getReceipt().getCc().getCardType());
        payment.setCurrency(FormCleaner.cleanAll(request.getParameter(ConstantsOP.PPS_CURRENCY_FIELD_NAME)));
        payment.setDetail(FormCleaner.escapeHtml(request.getParameter(ConstantsOP.PPS_COMMENTS_FIELD_NAME)));
        payment.setMethod(ConstantsOP.API_METHOD_VALUE);
        dpRequest.setPayment(payment);
        return dpRequest;
    }

    // Helpers
    private String getToken(DigitalPaymentsConfigService digitalPaymentsConfigService) {
        String cacheName = ConstantsOP.DP_TOKEN_CACHE_NAME;
        String tokenUrl = digitalPaymentsConfigService.getTokenUrl();
        String clientId = digitalPaymentsConfigService.getClientId();
        String clientSecret = digitalPaymentsConfigService.getClientSecret();
        String resource = digitalPaymentsConfigService.getDPResource();
        List<NameValuePair> formParams = ApiCommonTokenHelper.populateTokenFormParams(clientId, clientSecret, resource);
        return ApiCommonTokenHelper.getBearerToken(cacheName, tokenUrl, formParams);
    }

    private String parseMonerisDate(String monerisDate) {
        Date date = null;
        String dateStr = null;
        SimpleDateFormat monerisFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat apimFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            date = monerisFormatter.parse(monerisDate);
        } catch (ParseException e) {
            log.error("parseMonerisDate {}", monerisDate, e);
        }
        // Fall back
        if (date == null) {
            date = new Date();
        }
        dateStr = apimFormatter.format(date);
        return dateStr;
    }
}
