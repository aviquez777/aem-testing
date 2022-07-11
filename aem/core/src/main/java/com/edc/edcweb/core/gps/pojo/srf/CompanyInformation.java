package com.edc.edcweb.core.gps.pojo.srf;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyInformation {

    @JsonProperty("LegalName")
    private String legalName;

    @JsonProperty("KnownAs")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String knownAs;

    @JsonProperty("ParentCompany")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String parentCompany;

    @JsonProperty("Website")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String website;

    @JsonProperty("CompanyCountry")
    private String companyCountry;

    @JsonProperty("CompanyAddress1")
    private String companyAddress1;

    @JsonProperty("CompanyAddress2")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String companyAddress2;

    @JsonProperty("CompanyCity")
    private String companyCity;

    @JsonProperty("CompanyProvince")
    private String companyProvince;

    @JsonProperty("CompanyPostal")
    private String companyPostal;

    @JsonProperty("BCorpCertification")
    private String bCorpCertification;

    @JsonProperty("CertifiedDiverseSupplier")
    private String certifiedDiverseSupplier;

    // Bug 225774 Just change the json key name
    @JsonProperty("CertifiedDiverseSupplierType")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String[] certifiedDiverseSupplierYes;

    @JsonProperty("OtherDiversityOrganizations")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String[] otherDiversityOrganizations;
    // Bug 225774
    @JsonProperty("DiverseSupplier")
    private String diverseSupplier;

    @JsonProperty("RemitNameOfEntity")
    private String remitNameOfEntity;

    @JsonProperty("RemitTransitNumber")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String remitTransitNumber;

    @JsonProperty("RemitInstitutionNumber")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String remitInstitutionNumber;

    @JsonProperty("RemitAccountNumber")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String remitAccountNumber;

    @JsonProperty("RemitEftEmailaddress")
    private String remitEftEmailaddress;

    // Start International form Only
    @JsonProperty("RemitSameAddress")
    private String remitSameAddress;

    @JsonProperty("RemitBeneficiaryName")
    private String remitBeneficiaryName;

    @JsonProperty("RemitBeneficiaryAccount")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String remitBeneficiaryAccount;

    @JsonProperty("RemitBeneficiaryIBAN")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String remitBeneficiaryIBAN;

    @JsonProperty("RemitBeneficiaryCurrency")
    private String remitBeneficiaryCurrency;

    @JsonProperty("RemitBeneficiaryBankName")
    private String remitBeneficiaryBankName;

    @JsonProperty("RemitBeneficiaryBankRoutingMethod")
    private String remitBeneficiaryBankRoutingMethod;

    @JsonProperty("RemitBeneficiaryIntermediaryBankName")
    private String remitBeneficiaryIntermediaryBankName;

    @JsonProperty("RemitBeneficiaryIntermediaryBankRoutingMethod")
    private String remitBeneficiaryIntermediaryBankRoutingMethod;

    @JsonProperty("RemitCompanyCountry")
    private String remitCompanyCountry;

    @JsonProperty("RemitCompanyAddress1")
    private String remitCompanyAddress1;

    @JsonProperty("RemitCompanyAddress2")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String remitCompanyAddress2;

    @JsonProperty("RemitCompanyCity")
    private String remitCompanyCity;

    @JsonProperty("RemitCompanyProvince")
    private String remitCompanyProvince;

    @JsonProperty("RemitCompanyPostal")
    private String remitCompanyPostal;

    @JsonProperty("RemitEmailAddress")
    private String remitEmailAddress;

    @JsonProperty("Comments")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String comments;

    @JsonIgnore
    Map<String, String> errorMsgs = new HashMap<>();

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getKnownAs() {
        return knownAs;
    }

    public void setKnownAs(String knownAs) {
        this.knownAs = knownAs;
    }

    public String getParentCompany() {
        return parentCompany;
    }

    public void setParentCompany(String parentCompany) {
        this.parentCompany = parentCompany;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getCompanyCountry() {
        return companyCountry;
    }

    public void setCompanyCountry(String companyCountry) {
        this.companyCountry = companyCountry;
    }

    public String getCompanyAddress1() {
        return companyAddress1;
    }

    public void setCompanyAddress1(String companyAddress1) {
        this.companyAddress1 = companyAddress1;
    }

    public String getCompanyAddress2() {
        return companyAddress2;
    }

    public void setCompanyAddress2(String companyAddress2) {
        this.companyAddress2 = companyAddress2;
    }

    public String getCompanyCity() {
        return companyCity;
    }

    public void setCompanyCity(String companyCity) {
        this.companyCity = companyCity;
    }

    public String getCompanyProvince() {
        return companyProvince;
    }

    public void setCompanyProvince(String companyProvince) {
        this.companyProvince = companyProvince;
    }

    public String getCompanyPostal() {
        return companyPostal;
    }

    public void setCompanyPostal(String companyPostal) {
        this.companyPostal = companyPostal;
    }

    public String getbCorpCertification() {
        return bCorpCertification;
    }

    public void setbCorpCertification(String bCorpCertification) {
        this.bCorpCertification = bCorpCertification;
    }

    public String getCertifiedDiverseSupplier() {
        return certifiedDiverseSupplier;
    }

    public void setCertifiedDiverseSupplier(String certifiedDiverseSupplier) {
        this.certifiedDiverseSupplier = certifiedDiverseSupplier;
    }

    public String[] getCertifiedDiverseSupplierYes() {
        return certifiedDiverseSupplierYes;
    }

    public void setCertifiedDiverseSupplierYes(String[] certifiedDiverseSupplierYes) {
        this.certifiedDiverseSupplierYes = certifiedDiverseSupplierYes;
    }

    public String[] getOtherDiversityOrganizations() {
        return otherDiversityOrganizations;
    }

    public void setOtherDiversityOrganizations(String[] otherDiversityOrganizations) {
        this.otherDiversityOrganizations = otherDiversityOrganizations;
    }
    // Bug 225774
    public String getDiverseSupplier() {
        return diverseSupplier;
    }
    // Bug 225774
    public void setDiverseSupplier(String diverseSupplier) {
        this.diverseSupplier = diverseSupplier;
    }

    public String getRemitNameOfEntity() {
        return remitNameOfEntity;
    }

    public void setRemitNameOfEntity(String remitNameOfEntity) {
        this.remitNameOfEntity = remitNameOfEntity;
    }

    public String getRemitTransitNumber() {
        return remitTransitNumber;
    }

    public void setRemitTransitNumber(String remitTransitNumber) {
        this.remitTransitNumber = remitTransitNumber;
    }

    public String getRemitInstitutionNumber() {
        return remitInstitutionNumber;
    }

    public void setRemitInstitutionNumber(String remitInstitutionNumber) {
        this.remitInstitutionNumber = remitInstitutionNumber;
    }

    public String getRemitAccountNumber() {
        return remitAccountNumber;
    }

    public void setRemitAccountNumber(String remitAccountNumber) {
        this.remitAccountNumber = remitAccountNumber;
    }

    public String getRemitEftEmailaddress() {
        return remitEftEmailaddress;
    }

    public void setRemitEftEmailaddress(String remitEftEmailaddress) {
        this.remitEftEmailaddress = remitEftEmailaddress;
    }

    public String getRemitSameAddress() {
        return remitSameAddress;
    }

    public void setRemitSameAddress(String remitSameAddress) {
        this.remitSameAddress = remitSameAddress;
    }

    public String getRemitBeneficiaryName() {
        return remitBeneficiaryName;
    }

    public void setRemitBeneficiaryName(String remitBeneficiaryName) {
        this.remitBeneficiaryName = remitBeneficiaryName;
    }

    public String getRemitBeneficiaryAccount() {
        return remitBeneficiaryAccount;
    }

    public void setRemitBeneficiaryAccount(String remitBeneficiaryAccount) {
        this.remitBeneficiaryAccount = remitBeneficiaryAccount;
    }

    public String getRemitBeneficiaryIBAN() {
        return remitBeneficiaryIBAN;
    }

    public void setRemitBeneficiaryIBAN(String remitBeneficiaryIBAN) {
        this.remitBeneficiaryIBAN = remitBeneficiaryIBAN;
    }

    public String getRemitBeneficiaryCurrency() {
        return remitBeneficiaryCurrency;
    }

    public void setRemitBeneficiaryCurrency(String remitBeneficiaryCurrency) {
        this.remitBeneficiaryCurrency = remitBeneficiaryCurrency;
    }

    public String getRemitBeneficiaryBankName() {
        return remitBeneficiaryBankName;
    }

    public void setRemitBeneficiaryBankName(String remitBeneficiaryBankName) {
        this.remitBeneficiaryBankName = remitBeneficiaryBankName;
    }

    public String getRemitBeneficiaryBankRoutingMethod() {
        return remitBeneficiaryBankRoutingMethod;
    }

    public void setRemitBeneficiaryBankRoutingMethod(String remitBeneficiaryBankRoutingMethod) {
        this.remitBeneficiaryBankRoutingMethod = remitBeneficiaryBankRoutingMethod;
    }

    public String getRemitBeneficiaryIntermediaryBankName() {
        return remitBeneficiaryIntermediaryBankName;
    }

    public void setRemitBeneficiaryIntermediaryBankName(String remitBeneficiaryIntermediaryBankName) {
        this.remitBeneficiaryIntermediaryBankName = remitBeneficiaryIntermediaryBankName;
    }

    public String getRemitBeneficiaryIntermediaryBankRoutingMethod() {
        return remitBeneficiaryIntermediaryBankRoutingMethod;
    }

    public void setRemitBeneficiaryIntermediaryBankRoutingMethod(String remitBeneficiaryIntermediaryBankRoutingMethod) {
        this.remitBeneficiaryIntermediaryBankRoutingMethod = remitBeneficiaryIntermediaryBankRoutingMethod;
    }

    public String getRemitCompanyCountry() {
        return remitCompanyCountry;
    }

    public void setRemitCompanyCountry(String remitCompanyCountry) {
        this.remitCompanyCountry = remitCompanyCountry;
    }

    public String getRemitCompanyAddress1() {
        return remitCompanyAddress1;
    }

    public void setRemitCompanyAddress1(String remitCompanyAddress1) {
        this.remitCompanyAddress1 = remitCompanyAddress1;
    }

    public String getRemitCompanyAddress2() {
        return remitCompanyAddress2;
    }

    public void setRemitCompanyAddress2(String remitCompanyAddress2) {
        this.remitCompanyAddress2 = remitCompanyAddress2;
    }

    public String getRemitCompanyCity() {
        return remitCompanyCity;
    }

    public void setRemitCompanyCity(String remitCompanyCity) {
        this.remitCompanyCity = remitCompanyCity;
    }

    public String getRemitCompanyProvince() {
        return remitCompanyProvince;
    }

    public void setRemitCompanyProvince(String remitCompanyProvince) {
        this.remitCompanyProvince = remitCompanyProvince;
    }

    public String getRemitCompanyPostal() {
        return remitCompanyPostal;
    }

    public void setRemitCompanyPostal(String remitCompanyPostal) {
        this.remitCompanyPostal = remitCompanyPostal;
    }

    public String getRemitEmailAddress() {
        return remitEmailAddress;
    }

    public void setRemitEmailAddress(String remitEmailAddress) {
        this.remitEmailAddress = remitEmailAddress;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Map<String, String> getErrorMsgs() {
        return errorMsgs;
    }

    public void setErrorMsgs(Map<String, String> errorMsgs) {
        this.errorMsgs = errorMsgs;
    }
}