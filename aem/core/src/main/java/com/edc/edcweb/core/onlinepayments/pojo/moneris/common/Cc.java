package com.edc.edcweb.core.onlinepayments.pojo.moneris.common;

import com.edc.edcweb.core.onlinepayments.pojo.moneris.receipt.Fraud;
import com.edc.edcweb.core.onlinepayments.pojo.moneris.receipt.Tokenize;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Cc {
    private String orderNo;
    private String custId;
    private String transactionNo;
    private String referenceNo;
    private String transactionCode;
    private String transactionType;
    private String transactionDateTime;
    private String corporateCard;
    private String amount;
    private String responseCode;
    private String isoResponseCode;
    private String approvalCode;
    private String cardType;
    private String dynamicDescriptor;
    private String invoiceNumber;
    private String customerCode;
    private String eci;
    private String cvdResultCode;
    private String avsResultCode;
    private String first6last4;
    private String expiryDate;
    private String recurSuccess;
    private String issuerId;
    private String isDebit;
    private String ecrNo;
    private String batchNo;
    private String sequenceNo;

    @JsonProperty("order_no")
    public String getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    @JsonProperty("cust_id")
    public String getCustId() {
        return this.custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    @JsonProperty("transaction_no")
    public String getTransactionNo() {
        return this.transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    @JsonProperty("reference_no")
    public String getReferenceNo() {
        return this.referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    @JsonProperty("transaction_code")
    public String getTransactionCode() {
        return this.transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    @JsonProperty("transaction_type")
    public String getTransactionType() {
        return this.transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    @JsonProperty("transaction_date_time")
    public String getTransactionDateTime() {
        return this.transactionDateTime;
    }

    public void setTransactionDateTime(String transactionDateTime) {
        this.transactionDateTime = transactionDateTime;
    }

    @JsonProperty("corporateCard")
    public String getCorporateCard() {
        return this.corporateCard;
    }

    public void setCorporateCard(String corporateCard) {
        this.corporateCard = corporateCard;
    }

    @JsonProperty("amount")
    public String getAmount() {
        return this.amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @JsonProperty("response_code")
    public String getResponseCode() {
        return this.responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    @JsonProperty("iso_response_code")
    public String getIsoResponseCode() {
        return this.isoResponseCode;
    }

    public void setIsoResponseCode(String isoResponseCode) {
        this.isoResponseCode = isoResponseCode;
    }

    @JsonProperty("approval_code")
    public String getApprovalCode() {
        return this.approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }

    @JsonProperty("card_type")
    public String getCardType() {
        return this.cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    @JsonProperty("dynamic_descriptor")
    public String getDynamicDescriptor() {
        return this.dynamicDescriptor;
    }

    public void setDynamicDescriptor(String dynamicDescriptor) {
        this.dynamicDescriptor = dynamicDescriptor;
    }

    @JsonProperty("invoice_number")
    public String getInvoiceNumber() {
        return this.invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    @JsonProperty("customer_code")
    public String getCustomerCode() {
        return this.customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    @JsonProperty("eci")
    public String getEci() {
        return this.eci;
    }

    public void setEci(String eci) {
        this.eci = eci;
    }

    @JsonProperty("cvd_result_code")
    public String getCvdResultCode() {
        return this.cvdResultCode;
    }

    public void setCvdResultCode(String cvdResultCode) {
        this.cvdResultCode = cvdResultCode;
    }

    @JsonProperty("avs_result_code")
    public String getAvsResultCode() {
        return this.avsResultCode;
    }

    public void setAvsResultCode(String avsResultCode) {
        this.avsResultCode = avsResultCode;
    }

    @JsonProperty("first6last4")
    public String getFirst6last4() {
        return this.first6last4;
    }

    public void setFirst6last4(String first6last4) {
        this.first6last4 = first6last4;
    }

    @JsonProperty("expiry_date")
    public String getExpiryDate() {
        return this.expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    @JsonProperty("recur_success")
    public String getRecurSuccess() {
        return this.recurSuccess;
    }

    public void setRecurSuccess(String recurSuccess) {
        this.recurSuccess = recurSuccess;
    }

    @JsonProperty("issuer_id")
    public String getIssuerId() {
        return this.issuerId;
    }

    public void setIssuerId(String issuerId) {
        this.issuerId = issuerId;
    }

    @JsonProperty("is_debit")
    public String getIsDebit() {
        return this.isDebit;
    }

    public void setIsDebit(String isDebit) {
        this.isDebit = isDebit;
    }

    @JsonProperty("ecr_no")
    public String getEcrNo() {
        return this.ecrNo;
    }

    public void setEcrNo(String ecrNo) {
        this.ecrNo = ecrNo;
    }

    @JsonProperty("batch_no")
    public String getBatchNo() {
        return this.batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    @JsonProperty("sequence_no")
    public String getSequenceNo() {
        return this.sequenceNo;
    }

    public void setSequenceNo(String sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    @JsonProperty("result")
    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    private String result;

    @JsonProperty("tokenize")
    public Tokenize getTokenize() {
        return this.tokenize;
    }

    public void setTokenize(Tokenize tokenize) {
        this.tokenize = tokenize;
    }

    Tokenize tokenize;

    @JsonProperty("fraud")
    public Fraud getFraud() {
        return this.fraud;
    }

    public void setFraud(Fraud fraud) {
        this.fraud = fraud;
    }

    Fraud fraud;
}