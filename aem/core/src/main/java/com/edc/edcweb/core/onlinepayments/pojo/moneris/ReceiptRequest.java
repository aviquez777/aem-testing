package com.edc.edcweb.core.onlinepayments.pojo.moneris;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReceiptRequest {

    private String storeId;
    private String apiToken;
    private String checkoutId;
    private String ticket;
    private String environment = "qa";
    private String action = "receipt";

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

    @JsonProperty("ticket")
    public String getTicket() {
        return this.ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
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

}
