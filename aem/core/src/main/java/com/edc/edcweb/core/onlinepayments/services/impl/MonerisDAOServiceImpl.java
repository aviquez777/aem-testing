package com.edc.edcweb.core.onlinepayments.services.impl;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.onlinepayments.OPJsonDataBindingUtil;
import com.edc.edcweb.core.onlinepayments.helpers.ConstantsOP;
import com.edc.edcweb.core.onlinepayments.helpers.MonerisAPIHelper;
import com.edc.edcweb.core.onlinepayments.pojo.edcweb.EDCReceipt;
import com.edc.edcweb.core.onlinepayments.pojo.edcweb.TicketNumberResponse;
import com.edc.edcweb.core.onlinepayments.pojo.edcweb.TransactionResponse;
import com.edc.edcweb.core.onlinepayments.pojo.moneris.CredentialsData;
import com.edc.edcweb.core.onlinepayments.pojo.moneris.PreloadRequest;
import com.edc.edcweb.core.onlinepayments.pojo.moneris.PreloadResponse;
import com.edc.edcweb.core.onlinepayments.pojo.moneris.ReceiptRequest;
import com.edc.edcweb.core.onlinepayments.pojo.moneris.ReceiptResponse;
import com.edc.edcweb.core.onlinepayments.pojo.moneris.common.CustInfo;
import com.edc.edcweb.core.onlinepayments.pojo.moneris.common.MonerisError;
import com.edc.edcweb.core.onlinepayments.pojo.moneris.preload.PreResponse;
import com.edc.edcweb.core.onlinepayments.pojo.moneris.receipt.RecResponse;
import com.edc.edcweb.core.onlinepayments.services.MonerisConfigurationService;
import com.edc.edcweb.core.onlinepayments.services.MonerisDAOService;
import com.fasterxml.jackson.core.JsonProcessingException;

@Component(immediate = true, service = MonerisDAOService.class)
public class MonerisDAOServiceImpl implements MonerisDAOService {

    private static final Logger log = LoggerFactory.getLogger(MonerisDAOServiceImpl.class);

    @Reference
    private MonerisConfigurationService monerisConfigurationService;

    /**
     * Implement getMonerisTicketNumber
     * String companyName, CustInfo custInfo added on TASK 329072
     */
    @Override
    public String getMonerisTicketNumber(String txnTotal, String currency, String language, String companyName, CustInfo custInfo) {
        String jsonResponse = null;
        String monerisUrl = monerisConfigurationService.getMonerisUrl();
        // add data to POJO
        PreloadRequest preloadRequest = addPreloadRequestData(txnTotal, currency, language, companyName, custInfo);
        // Get the PreloadResponse from the API
        PreloadResponse preloadResponse = MonerisAPIHelper.getPreload(monerisUrl, preloadRequest);
        // Initialize the OPTicketNumberResponse
        TicketNumberResponse ticketResponse = addTicketResponseData(preloadResponse.getResponse());
        // Add error if response is empty
        if (preloadResponse.getResponse() == null) {
            ticketResponse = addErrorToTicketResponse(ConstantsOP.API_ERROR_MSG);
        }
        try {
            jsonResponse = OPJsonDataBindingUtil.pojoToJson(ticketResponse);
        } catch (JsonProcessingException e) {
            log.error("MonerisDAOServiceImpl OPTicketNumberResponse failed processing Json {}", preloadResponse, e);
        }
        return jsonResponse;
    }
    /**
     * Implement getMonerisReceipt
     */
    @Override
    public ReceiptResponse getMonerisReceipt(String ticketNumber, String currency) {
        String monerisUrl = monerisConfigurationService.getMonerisUrl();
        // add data to POJO
        ReceiptRequest receiptRequest = addReceiptRequestData(ticketNumber, currency);
        // Get the PreloadResponse from the API
        return MonerisAPIHelper.getReceipt(monerisUrl, receiptRequest);
    }
    /**
     *  Implement getTransactionResponseJSON
     */
    @Override
    public String getTransactionResponseJSON (ReceiptResponse receiptResponse, String apiResult) {
        String jsonReceipt = null;
        // Initialize the OPTicketNumberResponse
        TransactionResponse transactionResponse = addTransactionResponseData(receiptResponse.getResponse(), apiResult);
        // Add error if response is empty
        if (receiptResponse.getResponse() == null) {
            transactionResponse = addErrorToTransResponse(ConstantsOP.API_ERROR_MSG);
        }
        try {
            jsonReceipt = OPJsonDataBindingUtil.pojoToJson(transactionResponse);
        } catch (JsonProcessingException e) {
            transactionResponse = addErrorToTransResponse(e.getMessage());
            log.error("MonerisDAOServiceImpl OPTransactionResponse failed processing Json {}", transactionResponse, e);
        }
        // Add error if response is empty
        if (receiptResponse.getResponse() == null) {
            transactionResponse = addErrorToTransResponse(ConstantsOP.API_ERROR_MSG);
        }
        try {
            jsonReceipt = OPJsonDataBindingUtil.pojoToJson(transactionResponse);
        } catch (JsonProcessingException e) {
            transactionResponse = addErrorToTransResponse(e.getMessage());
            log.error("MonerisDAOServiceImpl OPTransactionResponse failed processing Json {}", transactionResponse, e);
        }
        return jsonReceipt;
    }

    // Ticket Helper Classes
    /**
     * addTicketResponseData Helper to add data to OPTicketNumberResponse POJO
     * 
     * @param preloadResponse
     * @return
     */
    private TicketNumberResponse addTicketResponseData(PreResponse response) {
        // Initialize the OPTicketNumberResponse
        TicketNumberResponse ticketResponse = new TicketNumberResponse();
        // Check for errors
        MonerisError error = response.getError();
        if (error != null) {
            ticketResponse.setError(response.getError());
            log.error("getMonerisTicketNumber error on request field {} message {}", error.getField(),
                    error.getMessage());
        } else {
            ticketResponse.setTicket(response.getTicket());
        }
        return ticketResponse;
    }

    /**
     * addPreloadRequestData Helper to add data to PreloadRequest POJO
     * @param language 
     * 
     * @param request
     * @return PreloadRequest
     */
    private PreloadRequest addPreloadRequestData(String txnTotal, String currency, String language, String companyName, CustInfo custInfo) {
        PreloadRequest preloadRequest = new PreloadRequest();
        // Required fields
        CredentialsData credentialsData = getCredentialsByCurrency(currency);
        preloadRequest.setStoreId(credentialsData.getStoreId());
        preloadRequest.setApiToken(credentialsData.getApiToken());
        preloadRequest.setCheckoutId(credentialsData.getCheckoutId());
        preloadRequest.setEnvironment(monerisConfigurationService.getEnvironment());
        preloadRequest.setAction(ConstantsOP.MONERIS_ACTION_PRELOAD);
        preloadRequest.setTxnTotal(txnTotal);
        preloadRequest.setLanguage(StringUtils.lowerCase(language));
        preloadRequest.setCustId(companyName);
        preloadRequest.setContactDetails(custInfo);
        return preloadRequest;
    }

    

    // Receipt Helper Classes

    /**
     * addReceiptRequestData Helper to add data to PreloadRequest POJO
     * 
     * @param ticketNumber
     * 
     * @param request
     * @return PreloadRequest
     */
    private ReceiptRequest addReceiptRequestData(String ticketNumber, String currency) {
        ReceiptRequest receiptRequest = new ReceiptRequest();
        // Required fields
        CredentialsData credentialsData = getCredentialsByCurrency(currency);
        receiptRequest.setStoreId(credentialsData.getStoreId());
        receiptRequest.setApiToken(credentialsData.getApiToken());
        receiptRequest.setCheckoutId(credentialsData.getCheckoutId());
        receiptRequest.setEnvironment(monerisConfigurationService.getEnvironment());
        receiptRequest.setAction(ConstantsOP.MONERIS_ACTION_RECEIPT);
        receiptRequest.setTicket(ticketNumber);
        return receiptRequest;
    }

    /**
     * addTransactionResponseData Helper to add data to OPTransactionResponse POJO
     * 
     * @param receiptResponse
     * @return
     */
    private TransactionResponse addTransactionResponseData(RecResponse response, String apiResult) {
        TransactionResponse transactionResponse = new TransactionResponse();
        // Check for errors
        MonerisError error = response.getError();
        if (error != null) {
            transactionResponse.setError(error);
            log.error("getMonerisReceipt error on request field {} message {}", error.getField(), error.getMessage());
        } else {
            // get the receipt or error from the JSON
            EDCReceipt receipt = new EDCReceipt();
            String transResult = response.getReceipt().getResult();
            receipt.setResult(transResult);
            receipt.setApiResult(apiResult);
            if (ConstantsOP.MONERIS_RECEIPT_RESULT_APPROVED_VALUE.equals(transResult)) {
                receipt.setReferenceNo(response.getReceipt().getCc().getReferenceNo());
            }
            transactionResponse.setReceipt(receipt);
        }
        return transactionResponse;
    }

    /**
     *  getCredentialsByCurrency return the CredentialsData Object for the currency provided.
     *  If no valid currency is provided, returns null and Moneris call will fail
     * @param currency Must be a valid currency (CAD, USD)
     * @return CredentialsData with the data, object with null values if invalid currency
     */
    private CredentialsData getCredentialsByCurrency(String currency) {
        CredentialsData credentialsData = new CredentialsData();
        if (ConstantsOP.PPS_CUURENCY_CAN_DOL_VALUE.equals(currency)) {
            credentialsData.setStoreId(monerisConfigurationService.getCADStoreId());
            credentialsData.setApiToken(monerisConfigurationService.getCADApiToken());
            credentialsData.setCheckoutId(monerisConfigurationService.getCADCheckoutId());
        }
        if (ConstantsOP.PPS_CUURENCY_USA_DOL_VALUE.equals(currency)) {
            credentialsData.setStoreId(monerisConfigurationService.getUSDStoreId());
            credentialsData.setApiToken(monerisConfigurationService.getUSDApiToken());
            credentialsData.setCheckoutId(monerisConfigurationService.getUSDCheckoutId());
        }
        return credentialsData;
    }
    /**
     * Add a Generic Message if no response from API
     * 
     * @param errorMessage
     * @return OPTicketNumberResponse
     */
    private TicketNumberResponse addErrorToTicketResponse(String errorMessage) {
        TicketNumberResponse ticketResponse = new TicketNumberResponse();
        MonerisError error = new MonerisError();
        error.setMessage(errorMessage);
        ticketResponse.setError(error);
        return ticketResponse;
    }
    /**
     * Add a Generic Message if no response from API
     * 
     * @param errorMessage
     * @return OPTransactionResponse
     */
    private TransactionResponse addErrorToTransResponse(String errorMessage) {
        TransactionResponse transactionResponse = new TransactionResponse();
        MonerisError error = new MonerisError();
        error.setMessage(errorMessage);
        transactionResponse.setError(error);
        return transactionResponse;
    }

}
