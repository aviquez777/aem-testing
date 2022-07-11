package com.edc.edcweb.core.gps.pojo;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GpsResponseRequest {
    @JsonProperty("Type")
    private String type;

    @JsonProperty("Data")
    private String data;

    @JsonProperty("Attachments")
    private List<GpsResponseFileItem> attachments = Collections.emptyList();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<GpsResponseFileItem> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<GpsResponseFileItem> attachments) {
        this.attachments = attachments;
        attachments = new ArrayList<>(attachments);
        this.attachments = Collections.unmodifiableList(attachments);
    }

}
