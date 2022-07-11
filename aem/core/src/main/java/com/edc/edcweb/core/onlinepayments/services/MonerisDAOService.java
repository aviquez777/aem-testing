package com.edc.edcweb.core.onlinepayments.services;

import com.edc.edcweb.core.onlinepayments.pojo.moneris.ReceiptResponse;
import com.edc.edcweb.core.onlinepayments.pojo.moneris.common.CustInfo;

public interface MonerisDAOService {

    String getMonerisTicketNumber(String txnTotal, String currency, String language, String companyName, CustInfo custInfo);

    ReceiptResponse getMonerisReceipt(String ticketNumber, String currency);

    String getTransactionResponseJSON (ReceiptResponse receiptResponse, String apiResult);

}
