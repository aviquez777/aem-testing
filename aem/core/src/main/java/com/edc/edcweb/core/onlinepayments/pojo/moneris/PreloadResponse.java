package com.edc.edcweb.core.onlinepayments.pojo.moneris;

import com.edc.edcweb.core.onlinepayments.pojo.moneris.preload.PreResponse;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PreloadResponse {

    private PreResponse response;

    public PreResponse getResponse() {
        return response;
    }

    public void setResponse(PreResponse response) {
        this.response = response;
    }

}
