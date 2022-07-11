package com.edc.edcportal.core.models;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.edc.edcportal.core.helpers.Constants;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

@Model(adaptables = Resource.class)
public class FormFieldDefintion {

    @Inject
    @Named("fieldType")
    @Optional
    private String fieldType;

    @Inject
    @Named("lovPath")
    @Optional
    private String lovPath;

    @Inject
    @Named("lovApiName")
    @Optional
    private String lovApiName;

    @Inject
    @Named("lovUseEnValAsCode")
    @Optional
    private Boolean lovUseEnValAsCode;

    @Inject
    @Named("lovOtherLast")
    @Optional
    private Boolean lovOtherLast;

    @Inject
    @Named("fieldName")
    @Optional
    private String fieldName;

    @Inject
    @Named("eloquaid")
    @Optional
    private String eloquaid;

    @Inject
    @Named("isMultiple")
    @Optional
    private Boolean isMultiple;

    @Inject
    @Named("labelVariation")
    @Optional
    private Boolean labelVariation;

    @Inject
    @Named("enLabel")
    @Optional
    private String enLabel;

    @Inject
    @Named("frLabel")
    @Optional
    private String frLabel;

    @Inject
    @Named("placeHolderVariation")
    @Optional
    private Boolean placeHolderVariation;

    @Inject
    @Named("enPlaceHolder")
    @Optional
    private String enPlaceHolder;

    @Inject
    @Named("frPlaceHolder")
    @Optional
    private String frPlaceHolder;

    @Inject
    @Named("enHelpText")
    @Optional
    private String enHelpText;

    @Inject
    @Named("frPlaceHolder")
    @Optional
    private String frHelpText;

    @Inject
    @Named("cssClass")
    @Optional
    private String cssClass;

    @Inject
    @Named("formGroupClass")
    @Optional
    private String formGroupClass;

    // Validations
    @Inject
    @Named("isRequired")
    @Optional
    private Boolean isRequired;

    @Inject
    @Named("isDisabled")
    @Optional
    private Boolean isDisabled;

    @Inject
    @Named("validation")
    @Optional
    private String validation;

    @Inject
    @Named("validationRule")
    @Optional
    private String validationRule;

    @Inject
    @Named("minLength")
    @Optional
    private String minLength;

    @Inject
    @Named("maxLength")
    @Optional
    private String maxLength;

    @Inject
    @Named("capitalize")
    @Optional
    private String capitalize;

    @Inject
    @Named("dataMask")
    @Optional
    private String dataMask;

    @Inject
    @Named("dataNoUrl")
    @Optional
    private String dataNoUrl;

    // display properties
    @Inject
    @Named("startFormRow")
    @Optional
    private String startFormRow;

    @Inject
    @Named("endFormRow")
    @Optional
    private String endFormRow;

    @Inject
    @Named("jsGroupDataElements")
    @Optional
    private String[] jsGroupDataElements;

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getLovPath() {
        return lovPath;
    }

    public void setLovPath(String lovPath) {
        this.lovPath = lovPath;
    }

    public String getLovApiName() {
        return lovApiName;
    }

    public void setLovApiName(String lovApiName) {
        this.lovApiName = lovApiName;
    }

    public Boolean getLovUseEnValAsCode() {
        return this.lovUseEnValAsCode;
    }

    public void setLovUseEnValAsCode(Boolean lovUseEnValAsCode) {
        this.lovUseEnValAsCode = lovUseEnValAsCode;
    }

    public Boolean getLovOtherLast() {
        return lovOtherLast;
    }

    public void setLovOtherLast(Boolean lovOtherLast) {
        this.lovOtherLast = lovOtherLast;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getEloquaid() {
        return eloquaid;
    }

    public void setEloquaid(String eloquaid) {
        this.eloquaid = eloquaid;
    }

    public Boolean getIsMultiple() {
        return isMultiple;
    }

    public void setIsMultiple(Boolean isMultiple) {
        this.isMultiple = isMultiple;
    }

    public Boolean getLabelVariation() {
        return labelVariation;
    }

    public void setLabelVariation(Boolean labelVariation) {
        this.labelVariation = labelVariation;
    }

    public String getEnLabel() {
        String label = "";
        if(StringUtils.isNotBlank(enLabel)){
            label = enLabel.trim().endsWith("?") ? enLabel : enLabel.concat(Constants.COLON);
        }
        return label;
    }

    public void setEnLabel(String enLabel) {
        this.enLabel = enLabel;
    }

    public String getFrLabel() {
        String label = "";
        if(StringUtils.isNotBlank(frLabel)){
            label =  frLabel.trim().endsWith("?") ? frLabel : frLabel.concat(Constants.COLON);
        }
        return label;
    }

    public void setFrLabel(String frLabel) {
        this.frLabel = frLabel;
    }

    public Boolean getPlaceHolderVariation() {
        return placeHolderVariation;
    }

    public void setPlaceHolderVariation(Boolean placeHolderVariation) {
        this.placeHolderVariation = placeHolderVariation;
    }

    public String getEnPlaceHolder() {
        return enPlaceHolder;
    }

    public void setEnPlaceHolder(String enPlaceHolder) {
        this.enPlaceHolder = enPlaceHolder;
    }

    public String getFrPlaceHolder() {
        return frPlaceHolder;
    }

    public void setFrPlaceHolder(String frPlaceHolder) {
        this.frPlaceHolder = frPlaceHolder;
    }

    public String getEnHelpText() {
        return enHelpText;
    }

    public void setEnHelpText(String enHelpText) {
        this.enHelpText = enHelpText;
    }

    public String getFrHelpText() {
        return frHelpText;
    }

    public void setFrHelpText(String frHelpText) {
        this.frHelpText = frHelpText;
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getFormGroupClass() {
        return formGroupClass;
    }

    public void setFormGroupClass(String formGroupClass) {
        this.formGroupClass = formGroupClass;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public Boolean getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(Boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    public String getValidation() {
        return validation;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }

    public String getValidationRule() {
        return validationRule;
    }

    public void setValidationRule(String validationRule) {
        this.validationRule = validationRule;
    }

    public String getMinLength() {
        return minLength;
    }

    public void setMinLength(String minLength) {
        this.minLength = minLength;
    }

    public String getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(String maxLength) {
        this.maxLength = maxLength;
    }

    public String getCapitalize() {
        return capitalize;
    }

    public void setCapitalize(String capitalize) {
        this.capitalize = capitalize;
    }

    public String getDataMask() {
        return dataMask;
    }

    public void setDataMask(String dataMask) {
        this.dataMask = dataMask;
    }

    public String getDataNoUrl() {
        return dataNoUrl;
    }

    public void setDataNoUrl(String dataNoUrl) {
        this.dataNoUrl = dataNoUrl;
    }

    public String getStartFormRow() {
        return startFormRow;
    }

    public void setStartFormRow(String startFormRow) {
        this.startFormRow = startFormRow;
    }

    public String getEndFormRow() {
        return endFormRow;
    }

    public void setEndFormRow(String endFormRow) {
        this.endFormRow = endFormRow;
    }

    public String[] getGroupJsDataElements() {
        return jsGroupDataElements;
    }

    public void setGroupJsDataElements(String[] jsGroupDataElements) {
        this.jsGroupDataElements = jsGroupDataElements;
    }

    public Map<String, String> getGroupAttrMap() {
        Map<String, String> groupAttrMap = new LinkedHashMap<>();
        if (jsGroupDataElements != null) {
            for (String dataElem : jsGroupDataElements) {
                String[] subElems = dataElem.split("=");
                if (subElems.length == 2) {
                    groupAttrMap.put(subElems[0], subElems[1]);
                }
            }
        }
        return groupAttrMap;
    }

}
