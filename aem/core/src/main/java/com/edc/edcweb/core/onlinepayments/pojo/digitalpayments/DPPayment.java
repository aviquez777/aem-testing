package com.edc.edcweb.core.onlinepayments.pojo.digitalpayments;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DPPayment {

    private String date;
    private String id;
    private String product;
    private float amount;
    private String method;
    private String creditCardTypeCode;
    private String creditCardLast4Digits;
    private  String currency;
    private String detail;

    @JsonProperty("Date")
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @JsonProperty("ID")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("Product")
    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    @JsonProperty("Amount")
    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
    @JsonProperty("Method")
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @JsonProperty("CreditCardTypeCode")
    public String getCreditCardTypeCode() {
        return creditCardTypeCode;
    }

    public void setCreditCardTypeCode(String creditCardTypeCode) {
        this.creditCardTypeCode = creditCardTypeCode;
    }

    @JsonProperty("CreditCardLast4Digits")
    public String getCreditCardLast4Digits() {
        return creditCardLast4Digits;
    }

    public void setCreditCardLast4Digits(String creditCardLast4Digits) {
        this.creditCardLast4Digits = creditCardLast4Digits;
    }

    @JsonProperty("Currency")
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @JsonProperty("Detail")
    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }


}
