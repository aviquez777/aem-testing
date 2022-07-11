package com.edc.edcweb.core.onlinepayments.pojo.moneris;

import com.edc.edcweb.core.onlinepayments.pojo.moneris.common.Address;
import com.edc.edcweb.core.onlinepayments.pojo.moneris.common.Cart;
import com.edc.edcweb.core.onlinepayments.pojo.moneris.common.CustInfo;
import com.edc.edcweb.core.onlinepayments.pojo.moneris.preload.Recur;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PreloadRequest {

    private String storeId;
    private String apiToken;
    private String checkoutId;
    private String txnTotal;
    private String environment = "qa";
    private String action = "preload";
    private String orderNo;
    private String custId;
    private String dynamicDescriptor;
    private String language;
    private Recur recur;
    private Cart cart;
    private CustInfo contactDetails;
    private CustInfo shippingDetails;
    private Address billingDetails;

    @JsonProperty("store_id")
    public String getStoreId() {
        return this.storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    @JsonProperty("api_token")
    public String getApiToken() {
        return this.apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    @JsonProperty("checkout_id")
    public String getCheckoutId() {
        return this.checkoutId;
    }

    public void setCheckoutId(String checkoutId) {
        this.checkoutId = checkoutId;
    }

    @JsonProperty("txn_total")
    public String getTxnTotal() {
        return this.txnTotal;
    }

    public void setTxnTotal(String txnTotal) {
        this.txnTotal = txnTotal;
    }

    @JsonProperty("environment")
    public String getEnvironment() {
        return this.environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    @JsonProperty("action")
    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

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

    @JsonProperty("dynamic_descriptor")
    public String getDynamicDescriptor() {
        return this.dynamicDescriptor;
    }

    public void setDynamicDescriptor(String dynamicDescriptor) {
        this.dynamicDescriptor = dynamicDescriptor;
    }

    @JsonProperty("language")
    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @JsonProperty("recur")
    public Recur getRecur() {
        return this.recur;
    }

    public void setRecur(Recur recur) {
        this.recur = recur;
    }

    @JsonProperty("cart")
    public Cart getCart() {
        return this.cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    @JsonProperty("contact_details")
    public CustInfo getContactDetails() {
        return this.contactDetails;
    }

    public void setContactDetails(CustInfo contactDetails) {
        this.contactDetails = contactDetails;
    }

    @JsonProperty("shipping_details")
    public CustInfo getShippingDetails() {
        return this.shippingDetails;
    }

    public void setShippingDetails(CustInfo shippingDetails) {
        this.shippingDetails = shippingDetails;
    }

    @JsonProperty("billing_details")
    public Address getBillingDetails() {
        return this.billingDetails;
    }

    public void setBillingDetails(Address billingDetails) {
        this.billingDetails = billingDetails;
    }

}
