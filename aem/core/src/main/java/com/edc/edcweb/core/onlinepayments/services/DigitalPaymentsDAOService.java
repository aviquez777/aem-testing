package com.edc.edcweb.core.onlinepayments.services;

import org.apache.sling.api.SlingHttpServletRequest;

import com.edc.edcweb.core.onlinepayments.pojo.digitalpayments.DPRequest;
import com.edc.edcweb.core.onlinepayments.pojo.moneris.receipt.RecResponse;

public interface DigitalPaymentsDAOService {
    DPRequest populateDPRequest(RecResponse recResponse, SlingHttpServletRequest request, String language);

    String postDigitalPaymentToApi(DPRequest dpRequest, boolean isProd);
}
