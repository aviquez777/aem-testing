package com.edc.edcweb.core.onlinepayments.pojo.moneris.receipt;

import java.util.List;

import com.edc.edcweb.core.onlinepayments.pojo.moneris.common.Cc;
import com.edc.edcweb.core.onlinepayments.pojo.moneris.common.Gift;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Receipt {
    private String result;
    private  List<Gift> gift;
    private Cc cc;

    @JsonProperty("result")
    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @JsonProperty("gift")
    public List<Gift> getGift() {
        return this.gift;
    }

    public void setGift(List<Gift> gift) {
        this.gift = gift;
    }

    @JsonProperty("cc")
    public Cc getCc() {
        return this.cc;
    }

    public void setCc(Cc cc) {
        this.cc = cc;
    }
}