package com.edc.edcportal.core.lovapi.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchCodesBysearchRequest {

    @JsonProperty("CodeSearchType")
    private Integer codeSearchType = 3;
    @JsonProperty("CodeTableId")
    private Integer codeTableId;
    @JsonProperty("CodeTableName")
    private String codeTableName;
    @JsonProperty("Code")
    private String code;
    @JsonProperty("Desc")
    private String desc;

    public Integer getCodeSearchType() {
        return codeSearchType;
    }

    public void setCodeSearchType(Integer codeSearchType) {
        this.codeSearchType = codeSearchType;
    }

    public Integer getCodeTableId() {
        return codeTableId;
    }

    public void setCodeTableId(Integer codeTableId) {
        this.codeTableId = codeTableId;
    }

    public String getCodeTableName() {
        return codeTableName;
    }

    public void setCodeTableName(String codeTableName) {
        this.codeTableName = codeTableName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
