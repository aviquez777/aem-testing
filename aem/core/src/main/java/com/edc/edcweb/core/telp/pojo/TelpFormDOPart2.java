package com.edc.edcweb.core.telp.pojo;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TelpFormDOPart2 {

    @JsonProperty("HeadquarterStreetAddress")
    private String headquarterStreetAddress;

    @JsonProperty("HeadquarterStreetAddressLine2")
    private String headquarterStreetAddressLine2;

    @JsonProperty("HeadquarterCity")
    private String headquarterCity;

    @JsonProperty("HeadquarterProvince")
    private String headquarterProvince;

    @JsonProperty("HeadquarterPostalCode")
    private String headquarterPostalCode;

    @JsonProperty("HeadquarterCountry")
    private TelpCountry headquarterCountry;

    @JsonProperty("ChiefExecutiveOfficerFirstName")
    private String chiefExecutiveOfficerFirstName;

    @JsonProperty("ChiefExecutiveOfficerLastName")
    private String chiefExecutiveOfficerLastName;

    @JsonProperty("ChiefExecutiveOfficerCountryOfResidence")
    private TelpCountry chiefExecutiveOfficerCountryOfResidence;

    @JsonProperty("LatestAnnualSales")
    private Long latestAnnualSales;

    @JsonProperty("FinancialYearEndMonth")
    private String financialYearEndMonth;
    // send the month name value.

    @JsonProperty("ExporterType")
    private String exporterType;
    // Direct Exporter, Indirect Exporter, or Future Direct Exporter

    @JsonProperty("PrimaryCountryOfExport")
    private TelpCountry primaryCountryOfExport;

    @JsonProperty("ApplicationSigningDate")
    private String applicationSigningDate;

    @JsonProperty("AuthorizedSigningAuthorityName")
    private String authorizedSigningAuthorityName;

    @JsonProperty("AuthorizedSigningAuthorityTitle")
    private String authorizedSigningAuthorityTitle;

    @JsonProperty("EligibilityStatus")
    private String eligibilityStatus;
    // Eligible and Ineligible

    @JsonProperty("CDIA")
    private String cDIA;

// MMG Fields
    @JsonProperty("SoldInternationally")
    private String soldInternationally;

    @JsonProperty("NumberOfEmployeesCode")
    private String mmEmployees;

    @JsonProperty("MmEntryCountryOfOperation")
    private List<MmEntryCountryOfOperation> mmEntryCountryOfOperation = Collections.emptyList();

    @JsonProperty("MmEntrySector")
    private List<MmEntrySector> mmEntrySector  = Collections.emptyList();

    public String getHeadquarterStreetAddress() {
        return headquarterStreetAddress;
    }

    public void setHeadquarterStreetAddress(String headquarterStreetAddress) {
        this.headquarterStreetAddress = headquarterStreetAddress;
    }

    public String getHeadquarterStreetAddressLine2() {
        return headquarterStreetAddressLine2;
    }

    public void setHeadquarterStreetAddressLine2(String headquarterStreetAddressLine2) {
        this.headquarterStreetAddressLine2 = headquarterStreetAddressLine2;
    }

    public String getHeadquarterCity() {
        return headquarterCity;
    }

    public void setHeadquarterCity(String headquarterCity) {
        this.headquarterCity = headquarterCity;
    }

    public String getHeadquarterProvince() {
        return headquarterProvince;
    }

    public void setHeadquarterProvince(String headquarterProvince) {
        this.headquarterProvince = headquarterProvince;
    }

    public String getHeadquarterPostalCode() {
        return headquarterPostalCode;
    }

    public void setHeadquarterPostalCode(String headquarterPostalCode) {
        this.headquarterPostalCode = headquarterPostalCode;
    }

    public TelpCountry getHeadquarterCountry() {
        return headquarterCountry;
    }

    public void setHeadquarterCountry(TelpCountry headquarterCountry) {
        this.headquarterCountry = headquarterCountry;
    }

    public String getChiefExecutiveOfficerFirstName() {
        return chiefExecutiveOfficerFirstName;
    }

    public void setChiefExecutiveOfficerFirstName(String chiefExecutiveOfficerFirstName) {
        this.chiefExecutiveOfficerFirstName = chiefExecutiveOfficerFirstName;
    }

    public String getChiefExecutiveOfficerLastName() {
        return chiefExecutiveOfficerLastName;
    }

    public void setChiefExecutiveOfficerLastName(String chiefExecutiveOfficerLastName) {
        this.chiefExecutiveOfficerLastName = chiefExecutiveOfficerLastName;
    }

    public TelpCountry getChiefExecutiveOfficerCountryOfResidence() {
        return chiefExecutiveOfficerCountryOfResidence;
    }

    public void setChiefExecutiveOfficerCountryOfResidence(TelpCountry chiefExecutiveOfficerCountryOfResidence) {
        this.chiefExecutiveOfficerCountryOfResidence = chiefExecutiveOfficerCountryOfResidence;
    }

    public Long getLatestAnnualSales() {
        return latestAnnualSales;
    }

    public void setLatestAnnualSales(Long latestAnnualSales) {
        this.latestAnnualSales = latestAnnualSales;
    }

    public String getFinancialYearEndMonth() {
        return financialYearEndMonth;
    }

    public void setFinancialYearEndMonth(String financialYearEndMonth) {
        this.financialYearEndMonth = financialYearEndMonth;
    }

    public String getExporterType() {
        return exporterType;
    }

    public void setExporterType(String exporterType) {
        this.exporterType = exporterType;
    }

    public TelpCountry getPrimaryCountryOfExport() {
        return primaryCountryOfExport;
    }

    public void setPrimaryCountryOfExport(TelpCountry primaryCountryOfExport) {
        this.primaryCountryOfExport = primaryCountryOfExport;
    }

    public String getApplicationSigningDate() {
        return applicationSigningDate;
    }

    public void setApplicationSigningDate(String applicationSigningDate) {
        this.applicationSigningDate = applicationSigningDate;
    }

    public String getAuthorizedSigningAuthorityName() {
        return authorizedSigningAuthorityName;
    }

    public void setAuthorizedSigningAuthorityName(String authorizedSigningAuthorityName) {
        this.authorizedSigningAuthorityName = authorizedSigningAuthorityName;
    }

    public String getAuthorizedSigningAuthorityTitle() {
        return authorizedSigningAuthorityTitle;
    }

    public void setAuthorizedSigningAuthorityTitle(String authorizedSigningAuthorityTitle) {
        this.authorizedSigningAuthorityTitle = authorizedSigningAuthorityTitle;
    }

    public String getEligibilityStatus() {
        return eligibilityStatus;
    }

    public void setEligibilityStatus(String eligibilityStatus) {
        this.eligibilityStatus = eligibilityStatus;
    }

    public String getcDIA() {
        return cDIA;
    }

    public void setcDIA(String cDIA) {
        this.cDIA = cDIA;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getSoldInternationally() {
        return soldInternationally;
    }

    public void setSoldInternationally(String soldInternationally) {
        this.soldInternationally = soldInternationally;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getMmEmployees() {
        return mmEmployees;
    }

    public void setMmEmployees(String mmEmployees) {
        this.mmEmployees = mmEmployees;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<MmEntryCountryOfOperation> getMmEntryCountryOfOperation() {
        return mmEntryCountryOfOperation;
    }

    public void setMmEntryCountryOfOperation(List<MmEntryCountryOfOperation> mmEntryCountryOfOperation) {
        this.mmEntryCountryOfOperation = mmEntryCountryOfOperation;
        mmEntryCountryOfOperation = new ArrayList<>(mmEntryCountryOfOperation);
        this.mmEntryCountryOfOperation = Collections.unmodifiableList(mmEntryCountryOfOperation); 
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<MmEntrySector> getMmEntrySector() {
        return mmEntrySector;
    }

    public void setMmEntrySector(List<MmEntrySector> mmEntrySector) {
        this.mmEntrySector = mmEntrySector;
        mmEntrySector = new ArrayList<>(mmEntrySector);
        this.mmEntrySector = Collections.unmodifiableList(mmEntrySector); 
    }
}
