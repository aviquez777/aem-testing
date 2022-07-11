package com.edc.edcweb.core.gps.pojo.telp;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TelpV2 {
    private String language;

    private String financialInstitution;

    private String bankAccountManagerFirstName;

    private String bankAccountManagerLastName;

    private String bankAccountManagerEmail;

    private String primaryContactFirstName;

    private String primaryContactLastName;

    private String primaryContactEmail;

    private String primaryContactPhone;

    private String caslConsent;

    private String legalName;

    private String companyTradeName;

    private int businessRegistrationNumber;

    private String headquarterStreetAddress;

    private String headquarterStreetAddressLine2;

    private String headquarterCity;

    private String headquarterProvince;

    private String headquarterPostalCode;

    private TelpV2Country headquarterCountry;

    long latestAnnualSales;

    private String financialYearEndMonth;

    private String exporterType;

    private TelpV2Country countryOfExport;

    private String applicationSigningDate;

    private String authorizedSigningAuthorityName;

    private String authorizedSigningAuthorityTitle;

    private String eligibilityStatus;

    private String cDIA;

    private String employees;

    private List<String> diversityForMajorityOwned;

    private List<String> diversityForMinorityOwned;

    private List<String> diversityForLeadership;

    @JsonProperty("Language")
    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @JsonProperty("FinancialInstitution")
    public String getFinancialInstitution() {
        return this.financialInstitution;
    }

    public void setFinancialInstitution(String financialInstitution) {
        this.financialInstitution = financialInstitution;
    }

    @JsonProperty("BankAccountManagerFirstName")
    public String getBankAccountManagerFirstName() {
        return this.bankAccountManagerFirstName;
    }

    public void setBankAccountManagerFirstName(String bankAccountManagerFirstName) {
        this.bankAccountManagerFirstName = bankAccountManagerFirstName;
    }

    @JsonProperty("BankAccountManagerLastName")
    public String getBankAccountManagerLastName() {
        return this.bankAccountManagerLastName;
    }

    public void setBankAccountManagerLastName(String bankAccountManagerLastName) {
        this.bankAccountManagerLastName = bankAccountManagerLastName;
    }

    @JsonProperty("BankAccountManagerEmail")
    public String getBankAccountManagerEmail() {
        return this.bankAccountManagerEmail;
    }

    public void setBankAccountManagerEmail(String bankAccountManagerEmail) {
        this.bankAccountManagerEmail = bankAccountManagerEmail;
    }

    @JsonProperty("PrimaryContactFirstName")
    public String getPrimaryContactFirstName() {
        return this.primaryContactFirstName;
    }

    public void setPrimaryContactFirstName(String primaryContactFirstName) {
        this.primaryContactFirstName = primaryContactFirstName;
    }

    @JsonProperty("PrimaryContactLastName")
    public String getPrimaryContactLastName() {
        return this.primaryContactLastName;
    }

    public void setPrimaryContactLastName(String primaryContactLastName) {
        this.primaryContactLastName = primaryContactLastName;
    }

    @JsonProperty("PrimaryContactEmail")
    public String getPrimaryContactEmail() {
        return this.primaryContactEmail;
    }

    public void setPrimaryContactEmail(String primaryContactEmail) {
        this.primaryContactEmail = primaryContactEmail;
    }

    @JsonProperty("PrimaryContactPhone")
    public String getPrimaryContactPhone() {
        return this.primaryContactPhone;
    }

    public void setPrimaryContactPhone(String primaryContactPhone) {
        this.primaryContactPhone = primaryContactPhone;
    }

    @JsonProperty("CaslConsent")
    public String getCaslConsent() {
        return this.caslConsent;
    }

    public void setCaslConsent(String caslConsent) {
        this.caslConsent = caslConsent;
    }

    @JsonProperty("LegalName")
    public String getLegalName() {
        return this.legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    @JsonProperty("CompanyTradeName")
    public String getCompanyTradeName() {
        return this.companyTradeName;
    }

    public void setCompanyTradeName(String companyTradeName) {
        this.companyTradeName = companyTradeName;
    }

    @JsonProperty("BusinessRegistrationNumber")
    public int getBusinessRegistrationNumber() {
        return this.businessRegistrationNumber;
    }

    public void setBusinessRegistrationNumber(int businessRegistrationNumber) {
        this.businessRegistrationNumber = businessRegistrationNumber;
    }

    @JsonProperty("HeadquarterStreetAddress")
    public String getHeadquarterStreetAddress() {
        return this.headquarterStreetAddress;
    }

    public void setHeadquarterStreetAddress(String headquarterStreetAddress) {
        this.headquarterStreetAddress = headquarterStreetAddress;
    }

    @JsonProperty("HeadquarterStreetAddressLine2")
    public String getHeadquarterStreetAddressLine2() {
        return this.headquarterStreetAddressLine2;
    }

    public void setHeadquarterStreetAddressLine2(String headquarterStreetAddressLine2) {
        this.headquarterStreetAddressLine2 = headquarterStreetAddressLine2;
    }

    @JsonProperty("HeadquarterCity")
    public String getHeadquarterCity() {
        return this.headquarterCity;
    }

    public void setHeadquarterCity(String headquarterCity) {
        this.headquarterCity = headquarterCity;
    }

    @JsonProperty("HeadquarterProvince")
    public String getHeadquarterProvince() {
        return this.headquarterProvince;
    }

    public void setHeadquarterProvince(String headquarterProvince) {
        this.headquarterProvince = headquarterProvince;
    }

    @JsonProperty("HeadquarterPostalCode")
    public String getHeadquarterPostalCode() {
        return this.headquarterPostalCode;
    }

    public void setHeadquarterPostalCode(String headquarterPostalCode) {
        this.headquarterPostalCode = headquarterPostalCode;
    }

    @JsonProperty("HeadquarterCountry")
    public TelpV2Country getHeadquarterCountry() {
        return this.headquarterCountry;
    }

    public void setHeadquarterCountry(TelpV2Country headquarterCountry) {
        this.headquarterCountry = headquarterCountry;
    }

    @JsonProperty("LatestAnnualSales")
    public long getLatestAnnualSales() {
        return this.latestAnnualSales;
    }

    public void setLatestAnnualSales(long latestAnnualSales) {
        this.latestAnnualSales = latestAnnualSales;
    }

    @JsonProperty("FinancialYearEndMonth")
    public String getFinancialYearEndMonth() {
        return this.financialYearEndMonth;
    }

    public void setFinancialYearEndMonth(String financialYearEndMonth) {
        this.financialYearEndMonth = financialYearEndMonth;
    }

    @JsonProperty("ExporterType")
    public String getExporterType() {
        return this.exporterType;
    }

    public void setExporterType(String exporterType) {
        this.exporterType = exporterType;
    }

    @JsonProperty("PrimaryCountryOfExport")
    public TelpV2Country getCountryOfExport() {
        return this.countryOfExport;
    }

    public void setCountryOfExport(TelpV2Country countryOfExport) {
        this.countryOfExport = countryOfExport;
    }

    @JsonProperty("ApplicationSigningDate")
    public String getApplicationSigningDate() {
        return this.applicationSigningDate;
    }

    public void setApplicationSigningDate(String applicationSigningDate) {
        this.applicationSigningDate = applicationSigningDate;
    }

    @JsonProperty("AuthorizedSigningAuthorityName")
    public String getAuthorizedSigningAuthorityName() {
        return this.authorizedSigningAuthorityName;
    }

    public void setAuthorizedSigningAuthorityName(String authorizedSigningAuthorityName) {
        this.authorizedSigningAuthorityName = authorizedSigningAuthorityName;
    }

    @JsonProperty("AuthorizedSigningAuthorityTitle")
    public String getAuthorizedSigningAuthorityTitle() {
        return this.authorizedSigningAuthorityTitle;
    }

    public void setAuthorizedSigningAuthorityTitle(String authorizedSigningAuthorityTitle) {
        this.authorizedSigningAuthorityTitle = authorizedSigningAuthorityTitle;
    }

    @JsonProperty("EligibilityStatus")
    public String getEligibilityStatus() {
        return this.eligibilityStatus;
    }

    public void setEligibilityStatus(String eligibilityStatus) {
        this.eligibilityStatus = eligibilityStatus;
    }

    @JsonProperty("CDIA")
    public String getCDIA() {
        return this.cDIA;
    }

    public void setCDIA(String cDIA) {
        this.cDIA = cDIA;
    }

    @JsonProperty("NumberOfEmployeesCode")
    public String getEmployees() {
        return this.employees;
    }

    public void setEmployees(String employees) {
        this.employees = employees;
    }

    @JsonProperty("DiversityForMajorityOwned")
    public List<String> getDiversityForMajorityOwned() {
        return this.diversityForMajorityOwned;
    }

    public void setDiversityForMajorityOwned(List<String> diversityForMajorityOwned) {
        this.diversityForMajorityOwned = diversityForMajorityOwned;
    }

    @JsonProperty("DiversityForMinorityOwned")
    public List<String> getDiversityForMinorityOwned() {
        return this.diversityForMinorityOwned;
    }

    public void setDiversityForMinorityOwned(List<String> diversityForMinorityOwned) {
        this.diversityForMinorityOwned = diversityForMinorityOwned;
    }

    @JsonProperty("DiversityForLeadership")
    public List<String> getDiversityForLeadership() {
        return this.diversityForLeadership;
    }

    public void setDiversityForLeadership(List<String> diversityForLeadership) {
        this.diversityForLeadership = diversityForLeadership;
    }

}
