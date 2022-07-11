package com.edc.edcweb.core.onlinepayments.pojo.moneris.receipt;

import com.edc.edcweb.core.onlinepayments.pojo.moneris.receipt.fraud.CvdAvs;
import com.edc.edcweb.core.onlinepayments.pojo.moneris.receipt.fraud.Kount;
import com.edc.edcweb.core.onlinepayments.pojo.moneris.receipt.fraud.ThreeDSecure;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Fraud {
    private CvdAvs cvd;
    private CvdAvs avs;
    private ThreeDSecure threeDSecure;
    private Kount kount;

    @JsonProperty("cvd")
    public CvdAvs getCvd() {
        return this.cvd;
    }

    public void setCvd(CvdAvs cvd) {
        this.cvd = cvd;
    }

    @JsonProperty("avs")
    public CvdAvs getAvs() {
        return this.avs;
    }

    public void setAvs(CvdAvs avs) {
        this.avs = avs;
    }

    @JsonProperty("3d_secure")
    public ThreeDSecure getThreeDSecure() {
        return this.threeDSecure;
    }

    public void setThreeDSecure(ThreeDSecure threeDSecure) {
        this.threeDSecure = threeDSecure;
    }

    @JsonProperty("kount")
    public Kount getKount() {
        return this.kount;
    }

    public void setKount(Kount kount) {
        this.kount = kount;
    }
}