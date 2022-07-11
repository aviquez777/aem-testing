package com.edc.edcportal.core.eloqua.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EloquaCDO {

    private String updatedBy;
    private String uniqueCodeFieldId;
    private String description;
    private String emailAddressFieldId;
    private String type;
    private String folderId;
    private String createdAt;
    private String displayNameFieldId;
    private String depth;
    private String createdBy;
    private String name;
    private String id;
    private List<EloquaCDOField> fields;
    private String errorMessage;
    private String customObjectRecordStatus;
    private String uniqueCode;
    private String contactId;
    private String isMapped;
    private List<EloquaCDOField> fieldValues;

    @JsonProperty("updatedBy")
    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @JsonProperty("uniqueCodeFieldId")
    public String getUniqueCodeFieldId() {
        return uniqueCodeFieldId;
    }

    public void setUniqueCodeFieldId(String uniqueCodeFieldId) {
        this.uniqueCodeFieldId = uniqueCodeFieldId;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("emailAddressFieldId")
    public String getEmailAddressFieldId() {
        return emailAddressFieldId;
    }

    public void setEmailAddressFieldId(String emailAddressFieldId) {
        this.emailAddressFieldId = emailAddressFieldId;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("folderId")
    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    @JsonProperty("createdAt")
    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("displayNameFieldId")
    public String getDisplayNameFieldId() {
        return displayNameFieldId;
    }

    public void setDisplayNameFieldId(String displayNameFieldId) {
        this.displayNameFieldId = displayNameFieldId;
    }

    @JsonProperty("depth")
    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    @JsonProperty("createdBy")
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("fields")
    public List<EloquaCDOField> getFields() {
        return fields;
    }

    public void setFields(List<EloquaCDOField> fields) {
        this.fields = fields;
    }

    @JsonProperty("ErrorMessage")
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    } 

    @JsonProperty("customObjectRecordStatus")
    public String getCustomObjectRecordStatus() {
        return customObjectRecordStatus;
    }

    public void setCustomObjectRecordStatus(String customObjectRecordStatus) {
        this.customObjectRecordStatus = customObjectRecordStatus;
    }

    @JsonProperty("uniqueCode")
    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    @JsonProperty("contactId")
    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    @JsonProperty("isMapped")
    public String getIsMapped() {
        return isMapped;
    }

    public void setIsMapped(String isMapped) {
        this.isMapped = isMapped;
    }

    @JsonProperty("fieldValues")
    public List<EloquaCDOField> getFieldValues() {
        return fieldValues;
    }

    public void setFieldValues(List<EloquaCDOField> fieldValues) {
        this.fieldValues = fieldValues;
    }

}
