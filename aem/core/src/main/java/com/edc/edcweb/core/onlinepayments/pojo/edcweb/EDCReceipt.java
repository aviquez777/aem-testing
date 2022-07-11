package com.edc.edcweb.core.onlinepayments.pojo.edcweb;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EDCReceipt {

    // from response.getReceipt().getResult()
    private String result;
    // if result = a from response.getReceipt().getCc().getReferenceNo())
    private String referenceNo;
    // Response from API
    private String apiResult;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getApiResult() {
        return apiResult;
    }

    public void setApiResult(String apiResult) {
        this.apiResult = apiResult;
    }

}
