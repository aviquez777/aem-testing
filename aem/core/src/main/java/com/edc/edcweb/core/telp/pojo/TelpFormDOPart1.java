package com.edc.edcweb.core.telp.pojo;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class TelpFormDOPart1 {
    @JsonProperty("Language")
    private String language;

    @JsonProperty("FinancialInstitution")
    private String financialInstitution;

    @JsonProperty("BankAccountManagerFirstName")
    private String bankAccountManagerFirstName;

    @JsonProperty("BankAccountManagerLastName")
    private String bankAccountManagerLastName;

    @JsonProperty("BankAccountManagerEmail")
    private String bankAccountManagerEmail;

    @JsonProperty("PrimaryContactFirstName")
    private String primaryContactFirstName;

    @JsonProperty("PrimaryContactLastName")
    private String primaryContactLastName;

    @JsonProperty("PrimaryContactEmail")
    private String primaryContactEmail;

    @JsonProperty("PrimaryContactPhone")
    private String primaryContactPhone;

    @JsonProperty("CaslConsent")
    private String caslConsent;

    @JsonProperty("LegalName")
    private String legalName;

    @JsonProperty("CompanyTradeName")
    private String companyTradeName;

    @JsonProperty("BusinessRegistrationNumber")
    private Integer businessRegistrationNumber;
    
    @JsonProperty("WomenOwnedOrLedCode")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String womenOwnedOrLedCode;

    @JsonProperty("IndigenousOwnedOrLedCode")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String indigenousOwnedOrLedCode;
    
    @JsonProperty("DiversityForMajorityOwned")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> diversityForMajorityOwned;

    @JsonProperty("DiversityForMinorityOwned")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> diversityForMinorityOwned;

    @JsonProperty("DiversityForLeadership")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> diversityForLeadership;

    // This will get manually parsed on TELPJsonDataBindingUtil as the structure
    // needs to be flattened
    @JsonIgnore()
    private List<BeneficialOwner> beneficialOwners = Collections.emptyList();

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFinancialInstitution() {
        return financialInstitution;
    }

    public void setFinancialInstitution(String financialInstitution) {
        this.financialInstitution = financialInstitution;
    }

    public String getBankAccountManagerFirstName() {
        return bankAccountManagerFirstName;
    }

    public void setBankAccountManagerFirstName(String bankAccountManagerFirstName) {
        this.bankAccountManagerFirstName = bankAccountManagerFirstName;
    }

    public String getBankAccountManagerLastName() {
        return bankAccountManagerLastName;
    }

    public void setBankAccountManagerLastName(String bankAccountManagerLastName) {
        this.bankAccountManagerLastName = bankAccountManagerLastName;
    }

    public String getBankAccountManagerEmail() {
        return bankAccountManagerEmail;
    }

    public void setBankAccountManagerEmail(String bankAccountManagerEmail) {
        this.bankAccountManagerEmail = bankAccountManagerEmail;
    }

    public String getPrimaryContactFirstName() {
        return primaryContactFirstName;
    }

    public void setPrimaryContactFirstName(String primaryContactFirstName) {
        this.primaryContactFirstName = primaryContactFirstName;
    }

    public String getPrimaryContactLastName() {
        return primaryContactLastName;
    }

    public void setPrimaryContactLastName(String primaryContactLastName) {
        this.primaryContactLastName = primaryContactLastName;
    }

    public String getPrimaryContactEmail() {
        return primaryContactEmail;
    }

    public void setPrimaryContactEmail(String primaryContactEmail) {
        this.primaryContactEmail = primaryContactEmail;
    }

    public String getPrimaryContactPhone() {
        return primaryContactPhone;
    }

    public void setPrimaryContactPhone(String primaryContactPhone) {
        this.primaryContactPhone = primaryContactPhone;
    }

    public String getCaslConsent() {
        return caslConsent;
    }

    public void setCaslConsent(String caslConsent) {
        this.caslConsent = caslConsent;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getCompanyTradeName() {
        return companyTradeName;
    }

    public void setCompanyTradeName(String companyTradeName) {
        this.companyTradeName = companyTradeName;
    }

    public Integer getBusinessRegistrationNumber() {
        return businessRegistrationNumber;
    }

    public void setBusinessRegistrationNumber(Integer businessRegistrationNumber) {
        this.businessRegistrationNumber = businessRegistrationNumber;
    }

    public List<BeneficialOwner> getBeneficialOwners() {
        return beneficialOwners;
    }

    public void setBeneficialOwners(List<BeneficialOwner> beneficialOwners) {
        this.beneficialOwners = beneficialOwners;
        beneficialOwners = new ArrayList<>(beneficialOwners);
        this.beneficialOwners = Collections.unmodifiableList(beneficialOwners); 
    }

    public String getWomenOwnedOrLedCode() {
        return womenOwnedOrLedCode;
    }

    public void setWomenOwnedOrLedCode(String womenOwnedOrLedCode) {
        this.womenOwnedOrLedCode = womenOwnedOrLedCode;
    }

    public String getIndigenousOwnedOrLedCode() {
        return indigenousOwnedOrLedCode;
    }

    public void setIndigenousOwnedOrLedCode(String indigenousOwnedOrLedCode) {
        this.indigenousOwnedOrLedCode = indigenousOwnedOrLedCode;
    }

    public List<String> getDiversityForMajorityOwned() {
        return this.diversityForMajorityOwned;
    }

    public void setDiversityForMajorityOwned(List<String> diversityForMajorityOwned) {
        this.diversityForMajorityOwned = diversityForMajorityOwned;
    }

    public List<String> getDiversityForMinorityOwned() {
        return this.diversityForMinorityOwned;
    }

    public void setDiversityForMinorityOwned(List<String> diversityForMinorityOwned) {
        this.diversityForMinorityOwned = diversityForMinorityOwned;
    }

    public List<String> getDiversityForLeadership() {
        return this.diversityForLeadership;
    }

    public void setDiversityForLeadership(List<String> diversityForLeadership) {
        this.diversityForLeadership = diversityForLeadership;
    }
}
