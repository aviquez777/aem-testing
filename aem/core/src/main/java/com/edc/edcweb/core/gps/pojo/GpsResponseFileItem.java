package com.edc.edcweb.core.gps.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GpsResponseFileItem {

    @JsonProperty("AttachmentName")
    private String attachmentName;

    @JsonProperty("AttachmentFileName")
    private String attachmentFileName;

    @JsonProperty("AttachmentFile")
    private String sttachmentFile;

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public String getAttachmentFileName() {
        return attachmentFileName;
    }

    public void setAttachmentFileName(String attachmentFileName) {
        this.attachmentFileName = attachmentFileName;
    }

    public String getSttachmentFile() {
        return sttachmentFile;
    }

    public void setSttachmentFile(String sttachmentFile) {
        this.sttachmentFile = sttachmentFile;
    }

}
