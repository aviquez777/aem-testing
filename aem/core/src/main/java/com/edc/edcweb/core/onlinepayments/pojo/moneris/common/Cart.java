package com.edc.edcweb.core.onlinepayments.pojo.moneris.common;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Cart {
    private List<Item> items;
    private String subtotal;
    private Tax tax;

    @JsonProperty("items")
    public List<Item> getItems() {
        return this.items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @JsonProperty("subtotal")
    public String getSubtotal() {
        return this.subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    @JsonProperty("tax")
    public Tax getTax() {
        return this.tax;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
    }

}