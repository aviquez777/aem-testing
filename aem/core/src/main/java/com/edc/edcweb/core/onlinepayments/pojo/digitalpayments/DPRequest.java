package com.edc.edcweb.core.onlinepayments.pojo.digitalpayments;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DPRequest {

    private DPAccount account;
    private DPPayment payment;

    public DPAccount getAccount() {
        return account;
    }

    @JsonProperty("Account")
    public void setAccount(DPAccount account) {
        this.account = account;
    }

    @JsonProperty("Payment")
    public DPPayment getPayment() {
        return payment;
    }

    public void setPayment(DPPayment payment) {
        this.payment = payment;
    }

}
