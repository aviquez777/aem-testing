package com.edc.edcweb.core.gps.pojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.sling.api.request.RequestParameter;

public class GpsFormObjects {

    private String formType;
    private String formJson;
    private List<RequestParameter> attachmentList = Collections.emptyList();
    Map<String, String> errorMsgs = new HashMap<>();
    private boolean returnConfNum = false;

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public String getFormJson() {
        return formJson;
    }

    public void setFormJson(String formJson) {
        this.formJson = formJson;
    }

    public List<RequestParameter> getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(List<RequestParameter> attachmentList) {
        this.attachmentList = attachmentList;
        attachmentList = new ArrayList<>(attachmentList);
        this.attachmentList = Collections.unmodifiableList(attachmentList);
    }

    public Map<String, String> getErrorMsgs() {
        return errorMsgs;
    }

    public void setErrorMsgs(Map<String, String> errorMsgs) {
        this.errorMsgs = errorMsgs;
    }

    public boolean isReturnConfNum() {
        return returnConfNum;
    }

    public void setReturnConfNum(boolean returnConfNum) {
        this.returnConfNum = returnConfNum;
    }

}
