package com.edc.edcportal.core.arkadin.pojo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.ImmutableList;

/**
 * 
 * ArkadinRegistrationDO POJO to use with jaxb databind
 *
 */
@XmlRootElement(name = "APIResults")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ArkadinRegistrationDO {

    private String apiCallResult;

    private String apiCallDiagnostic;

    private String opCodesProcessed;

    private String opCodesInError;

    private List<OpCodeResult> opCodeResults;

    public String getApiCallResult() {
        return apiCallResult;
    }

    @XmlAttribute(name = "APICallResult")
    public void setApiCallResult(String apiCallResult) {
        this.apiCallResult = apiCallResult;
    }

    public String getApiCallDiagnostic() {
        return apiCallDiagnostic;
    }

    @XmlAttribute(name = "APICallDiagnostic")
    public void setApiCallDiagnostic(String apiCallDiagnostic) {
        this.apiCallDiagnostic = apiCallDiagnostic;
    }

    public String getOpCodesProcessed() {
        return opCodesProcessed;
    }

    @XmlAttribute(name = "OpCodesProcessed")
    public void setOpCodesProcessed(String opCodesProcessed) {
        this.opCodesProcessed = opCodesProcessed;
    }

    public String getOpCodesInError() {
        return opCodesInError;
    }

    @XmlAttribute(name = "OpCodesInError")
    public void setOpCodesInError(String opCodesInError) {
        this.opCodesInError = opCodesInError;
    }

    public List<OpCodeResult> getOpCodeResults() {
        // Task 22143 squid:S2384
        return opCodeResults != null ? ImmutableList.copyOf(opCodeResults) : opCodeResults;
    }

    // Task 22143 squid:S2384
    @SuppressWarnings("squid:S2384")
    @XmlElement(name = "OpCodeResult")
    public void setOpCodeResults(List<OpCodeResult> opCodeResults) {
        /// we cannot store as an inmutable copy as we need to we need to modify the
        /// content on the property later.
        this.opCodeResults = opCodeResults;
    }

}
