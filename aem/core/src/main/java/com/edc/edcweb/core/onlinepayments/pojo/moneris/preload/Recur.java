package com.edc.edcweb.core.onlinepayments.pojo.moneris.preload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Recur {
    private String billNow;
    private String recurAmount;
    private String startDate;
    private String recurUnit;
    private String recurPeriod;
    private String numberOfRecurs;

    @JsonProperty("bill_now")
    public String getBillNow() {
        return this.billNow;
    }

    public void setBillNow(String billNow) {
        this.billNow = billNow;
    }

    @JsonProperty("recur_amount")
    public String getRecurAmount() {
        return this.recurAmount;
    }

    public void setRecurAmount(String recurAmount) {
        this.recurAmount = recurAmount;
    }

    @JsonProperty("start_date")
    public String getStartDate() {
        return this.startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @JsonProperty("recur_unit")
    public String getRecurUnit() {
        return this.recurUnit;
    }

    public void setRecurUnit(String recurUnit) {
        this.recurUnit = recurUnit;
    }

    @JsonProperty("recur_period")
    public String getRecurPeriod() {
        return this.recurPeriod;
    }

    public void setRecurPeriod(String recurPeriod) {
        this.recurPeriod = recurPeriod;
    }

    @JsonProperty("number_of_recurs")
    public String getNumberOfRecurs() {
        return this.numberOfRecurs;
    }

    public void numberOfRecurs(String numberOfRecurs) {
        this.numberOfRecurs = numberOfRecurs;
    }

}