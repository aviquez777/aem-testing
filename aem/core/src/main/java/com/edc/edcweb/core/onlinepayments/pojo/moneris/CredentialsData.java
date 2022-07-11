package com.edc.edcweb.core.onlinepayments.pojo.moneris;

/**
 * CredentialsData Object to return the required 3 pieces of information
 * 
 *
 */
public class CredentialsData {
    String storeId;
    String apiToken;
    String checkoutId;

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public String getCheckoutId() {
        return checkoutId;
    }

    public void setCheckoutId(String checkoutId) {
        this.checkoutId = checkoutId;
    }

}
