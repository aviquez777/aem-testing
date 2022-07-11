package com.edc.edcportal.core.models;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

@Model(adaptables = Resource.class)
public class ProfileTypeDefinition {

    @Inject
    @Named("enName")
    @Optional
    private String enName;

    @Inject
    @Named("frName")
    @Optional
    private String frName;

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
    @Named("value")
    @Optional
    private String value;

    @Inject
    @Named("formFields")
    @Optional
    private String formFields;

    @Inject
    @Named("showAutoComplete")
    @Optional
    private boolean showAutoComplete = true;

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getFrName() {
        return frName;
    }

    public void setFrName(String frName) {
        this.frName = frName;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFormFields() {
        return formFields;
    }

    public void setFormFields(String formFields) {
        this.formFields = formFields;
    }

    public boolean getShowAutoComplete() {
        return showAutoComplete;
    }

    public void setShowAutoComplete(boolean showAutoComplete) {
        this.showAutoComplete = showAutoComplete;
    }

}
