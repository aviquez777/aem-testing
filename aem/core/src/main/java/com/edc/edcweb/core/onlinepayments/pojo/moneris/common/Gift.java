package com.edc.edcweb.core.onlinepayments.pojo.moneris.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Gift {
    private String balanceRemaining;
    private String description;
    private String first4last4;
    private String pan;
    private String cvd;
    private String balanceUsed;
    private String orderNo;
    private String transactionNo;
    private String referenceNo;
    private String responseCode;
    private String benefitAmount;
    private String benefitRemaining;

    @JsonProperty("balance_remaining")
    public String getBalanceRemaining() {
        return this.balanceRemaining;
    }

    public void setBalanceRemaining(String balanceRemaining) {
        this.balanceRemaining = balanceRemaining;
    }

    @JsonProperty("Description")
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("first4last4")
    public String getFirst4last4() {
        return this.first4last4;
    }

    public void setFirst4last4(String first4last4) {
        this.first4last4 = first4last4;
    }

    @JsonProperty("pan")
    public String getPan() {
        return this.pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    @JsonProperty("cvd")
    public String getCvd() {
        return this.cvd;
    }

    public void setCvd(String cvd) {
        this.cvd = cvd;
    }

    @JsonProperty("balance_used")
    public String getBalanceUsed() {
        return this.balanceUsed;
    }

    public void setBalanceUsed(String balanceUsed) {
        this.balanceUsed = balanceUsed;
    }

    @JsonProperty("order_no")
    public String getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
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

    public void referenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    @JsonProperty("response_code")
    public String getResponseCode() {
        return this.responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    @JsonProperty("benefit_amount")
    public String getBenefitAmount() {
        return this.benefitAmount;
    }

    public void setBenefitAmount(String benefitAmount) {
        this.benefitAmount = benefitAmount;
    }

    @JsonProperty("benefit_remaining")
    public String getBenefitRemaining() {
        return this.benefitRemaining;
    }

    public void setBenefitRemaining(String benefitRemaining) {
        this.benefitRemaining = benefitRemaining;
    }

    @JsonProperty("first6last4")
    public String getFirst6last4() {
        return this.first6last4;
    }

    public void setFirst6last4(String first6last4) {
        this.first6last4 = first6last4;
    }

    private String first6last4;
}