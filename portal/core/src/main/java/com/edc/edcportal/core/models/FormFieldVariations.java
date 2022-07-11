package com.edc.edcportal.core.models;

import javax.inject.Inject;
import javax.inject.Named;

import com.edc.edcportal.core.helpers.Constants;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

@Model(adaptables = Resource.class)
public class FormFieldVariations {

    @Inject
    @Named("fieldType")
    @Optional
    private String fieldType;

    @Inject
    @Named("enTitle")
    @Optional
    private String enTitle;

    @Inject
    @Named("frTitle")
    @Optional
    private String frTitle;

    @Inject
    @Named("enSubtitle")
    @Optional
    private String enSubtitle;

    @Inject
    @Named("frSubtitle")
    @Optional
    private String frSubtitle;

    @Inject
    @Named("enTooltip")
    @Optional
    private String enTooltip;

    @Inject
    @Named("frTooltip")
    @Optional
    private String frTooltip;

    @Inject
    @Named("enLabel")
    @Optional
    private String enLabel;

    @Inject
    @Named("frLabel")
    @Optional
    private String frLabel;

    @Inject
    @Named("enPlaceholder")
    @Optional
    private String enPlaceholder;

    @Inject
    @Named("frPlaceholder")
    @Optional
    private String frPlaceholder;

    @Inject
    @Named("lovApiName")
    @Optional
    private String lovApiName;

    @Inject
    @Named("formGroupClass")
    @Optional
    private String formGroupClass;

    @Inject
    @Named("cssClass")
    @Optional
    private String cssClass;

    @Inject
    @Named("startFormRow")
    @Optional
    private String startFormRow;

    @Inject
    @Named("endFormRow")
    @Optional
    private String endFormRow;

    @Inject
    @Named("validationRule")
    @Optional
    private String validationRule;

    // Remember to add the values to the map on
    // com.edc.edcportal.core.models.FormExtraInfoObjects
    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getEnTitle() {
        return enTitle;
    }

    public void setEnTitle(String enTitle) {
        this.enTitle = enTitle;
    }

    public String getFrTitle() {
        return frTitle;
    }

    public void setFrTitle(String frTitle) {
        this.frTitle = frTitle;
    }

    public String getEnSubtitle() {
        return enSubtitle;
    }

    public void setEnSubtitle(String enSubtitle) {
        this.enSubtitle = enSubtitle;
    }

    public String getFrSubtitle() {
        return frSubtitle;
    }

    public void setFrSubtitle(String frSubtitle) {
        this.frSubtitle = frSubtitle;
    }

    public String getEnTooltip() {
        return enTooltip;
    }

    public void setEnTooltip(String enTooltip) {
        this.enTooltip = enTooltip;
    }

    public String getFrTooltip() {
        return frTooltip;
    }

    public void setFrTooltip(String frTooltip) {
        this.frTooltip = frTooltip;
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
            label = frLabel.trim().endsWith("?") ? frLabel : frLabel.concat(Constants.COLON);
        }
        return label;
    }

    public void setFrLabel(String frLabel) {
        this.frLabel = frLabel;
    }

    public String getEnPlaceholder() {
        return enPlaceholder;
    }

    public void setEnPlaceholder(String enPlaceholder) {
        this.enPlaceholder = enPlaceholder;
    }

    public String getFrPlaceholder() {
        return frPlaceholder;
    }

    public void setFrPlaceholder(String frPlaceholder) {
        this.frPlaceholder = frPlaceholder;
    }

    public String getLovApiName() {
        return lovApiName;
    }

    public void setLovApiName(String lovApiName) {
        this.lovApiName = lovApiName;
    }

    public String getFormGroupClass() {
        return formGroupClass;
    }

    public void setFormGroupClass(String formGroupClass) {
        this.formGroupClass = formGroupClass;
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
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

    public String getValidationRule() {
        return validationRule;
    }

    public void setValidationRule(String validationRule) {
        this.validationRule = validationRule;
    }

}
