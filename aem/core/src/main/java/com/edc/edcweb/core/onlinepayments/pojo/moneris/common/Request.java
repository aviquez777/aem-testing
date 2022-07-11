package com.edc.edcweb.core.onlinepayments.pojo.moneris.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Request {
    private String txnTotal;
    private CustInfo custInfo;
    private Cart cart;
    private Address shipping;
    private Address billing;
    private String ccTotal;
    private Gift gift;
    private RequestCC cc;
    private String ticket;
    private String custId;
    private String dynamicDescriptor;
    private String orderNo;
    private String eci;

    @JsonProperty("txn_total")
    public String getTxnTotal() {
        return this.txnTotal;
    }

    public void setTxnTotal(String txnTotal) {
        this.txnTotal = txnTotal;
    }

    @JsonProperty("cart")
    public Cart getCart() {
        return this.cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    @JsonProperty("cust_info")
    public CustInfo getCustInfo() {
        return this.custInfo;
    }

    public void setCustInfo(CustInfo custInfo) {
        this.custInfo = custInfo;
    }

    @JsonProperty("shipping")
    public Address getShipping() {
        return this.shipping;
    }

    public void setShipping(Address shipping) {
        this.shipping = shipping;
    }

    @JsonProperty("billing")
    public Address getBilling() {
        return this.billing;
    }

    public void setBilling(Address billing) {
        this.billing = billing;
    }

    @JsonProperty("cc_total")
    public String getCcTotal() {
        return this.ccTotal;
    }

    public void setCcTotal(String ccTotal) {
        this.ccTotal = ccTotal;
    }

    @JsonProperty("gift")
    public Gift getGift() {
        return this.gift;
    }

    public void setGift(Gift gift) {
        this.gift = gift;
    }

    @JsonProperty("cc")
    public RequestCC getCc() {
        return this.cc;
    }

    public void setCc(RequestCC cc) {
        this.cc = cc;
    }

    @JsonProperty("ticket")
    public String getTicket() {
        return this.ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
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

    @JsonProperty("order_no")
    public String getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    @JsonProperty("eci")
    public String getEci() {
        return this.eci;
    }

    public void setEci(String eci) {
        this.eci = eci;
    }

}