package com.edc.edcweb.core.onlinepayments.pojo.moneris.receipt.fraud;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FraudDetailsBase {
    private String decisionOrigin;
    private String result;
    private String condition;
    private String status;
    private String code;

    @JsonProperty("decision_origin")
    public String getDecisionOrigin() {
        return this.decisionOrigin;
    }

    public void setDecisionOrigin(String decisionOrigin) {
        this.decisionOrigin = decisionOrigin;
    }

    @JsonProperty("result")
    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @JsonProperty("condition")
    public String getCondition() {
        return this.condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    @JsonProperty("status")
    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("code")
    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
