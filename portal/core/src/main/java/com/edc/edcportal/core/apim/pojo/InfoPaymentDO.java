package com.edc.edcportal.core.apim.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * InfoPaymentDO POJO to use with Jackson databind
 *
 */
public class InfoPaymentDO {
    // required fields
    @JsonProperty("ExternalUserIdentifier")
    private String externalUserId;

    @JsonProperty("InformationPaymentReceivedDate")
    private String createdDateTime;

    @JsonProperty("UserLanguageCode")
    private String language;

    @JsonProperty("UserMobilePhoneNumber")
    private String mobileNumber;

    @JsonProperty("UserFirstName")
    private String firstName;

    @JsonProperty("UserLastName")
    private String lastName;

    @JsonProperty("UserEmailAddress")
    private String emailId;

    @JsonProperty("UserCaslConsentCode")
    private String caslCode;

    // optional fields
    @JsonProperty("InformationPaymentProfileTypeCode")
    private String profile;

    @JsonProperty("CompanyHeadquarterProvinceEdcCode")
    private String companyProvince;

    @JsonProperty("CompanyHeadquarterCountryEdcCode")
    private String companyCountry;

    @JsonProperty("CompanyEmployeeSizeRangeCode")
    private String employees;

    @JsonProperty("CompanyAnnualSalesRangeCode")
    private String annualSales;

    @JsonProperty("CompanyExportStatusCode")
    private String tradeStatus;

    @JsonProperty("CompanyPrimaryIndustryCode")
    private String industry;

    @JsonProperty("CompanyMarketOfInterestEdcCodeList")
    private String[] marketsIntCodeList;

    @JsonProperty("CompanyExportChallengeCodeList")
    private String[] painPointCodeList;

    @JsonProperty("CompanyLegalOrganizationName")
    private String companyName;

    @JsonProperty("CompanyHeadquarterAddressLine1Text")
    private String companyAddress1;

    @JsonProperty("CompanyHeadquarterAddressLine2Text")
    private String companyAddress2;

    @JsonProperty("CompanyHeadquarterCityText")
    private String companyCity;

    @JsonProperty("CompanyHeadquarterProvinceStateRegionText")
    private String companyProvinceDesc;

    @JsonProperty("CompanyHeadquarterPostalZipCode")
    private String companyPostal;

    @JsonProperty("ContactBusinessPhoneNumber")
    private String mainPhone;

    @JsonProperty("ContactJobTitleText")
    private String title;

    /** Descriptions **/
    @JsonProperty("InformationPaymentProfileTypeEnglishDescription")
    private String profileType;

    @JsonProperty("KvsProductEnglishDescription")
    private String product;

    @JsonProperty("KvsProductCategoryEnglishDescription")
    private String productDescription;

    @JsonProperty("UserCaslConsentEnglishDescription")
    private String casl;

    @JsonProperty("CompanyHeadquarterCountryEnglishDescription")
    private String companyCountryDesc;
    
    @JsonProperty("FiClientAnnualSalesRangeCode")
    private String fiClientAnnualSalesRangeCode;

    public String getExternalUserId() {
        return externalUserId;
    }

    public void setExternalUserId(String externalUserId) {
        this.externalUserId = externalUserId;
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getProfileType() {
        return profileType;
    }

    public void setProfileType(String profileType) {
        this.profileType = profileType;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCasl() {
        return casl;
    }

    public void setCasl(String casl) {
        this.casl = casl;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getCaslCode() {
        return caslCode;
    }

    public void setCaslCode(String caslCode) {
        this.caslCode = caslCode;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getCompanyProvince() {
        return companyProvince;
    }

    public void setCompanyProvince(String companyProvince) {
        this.companyProvince = companyProvince;
    }

    public String getCompanyCountry() {
        return companyCountry;
    }

    public void setCompanyCountry(String companyCountry) {
        this.companyCountry = companyCountry;
    }

    public String getEmployees() {
        return employees;
    }

    public void setEmployees(String employees) {
        this.employees = employees;
    }

    public String getAnnualSales() {
        return annualSales;
    }

    public void setAnnualSales(String annualSales) {
        this.annualSales = annualSales;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String[] getMarketsIntCodeList() {
        // Task 22143 squid:S2384
        return marketsIntCodeList != null ? marketsIntCodeList.clone() : marketsIntCodeList;
    }

    public void setMarketsIntCodeList(String[] marketsIntCodeList) {
        // Task 22143 squid:S2384
        this.marketsIntCodeList = marketsIntCodeList != null ? marketsIntCodeList.clone() : marketsIntCodeList;
    }

    public String[] getPainPointCodeList() {
        // Task 22143 squid:S2384 check for null first
        return painPointCodeList != null ?  painPointCodeList.clone() : painPointCodeList;
    }

    public void setPainPointCodeList(String[] painPointCodeList) {
        // Task 22143 squid:S2384 check for null first
        this.painPointCodeList = painPointCodeList != null ?  painPointCodeList.clone() : painPointCodeList;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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

    public String getCompanyProvinceDesc() {
        return companyProvinceDesc;
    }

    public void setCompanyProvinceDesc(String companyProvinceDesc) {
        this.companyProvinceDesc = companyProvinceDesc;
    }

    public String getCompanyPostal() {
        return companyPostal;
    }

    public void setCompanyPostal(String companyPostal) {
        this.companyPostal = companyPostal;
    }

    public String getMainPhone() {
        return mainPhone;
    }

    public void setMainPhone(String mainPhone) {
        this.mainPhone = mainPhone;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /** Descriptions **/
    public String getCompanyCountryDesc() {
        return companyCountryDesc;
    }

    public void setCompanyCountryDesc(String companyCountryDesc) {
        this.companyCountryDesc = companyCountryDesc;
    }

    public String getFiClientAnnualSalesRangeCode() {
        return fiClientAnnualSalesRangeCode;
    }

    public void setFiClientAnnualSalesRangeCode(String fiClientAnnualSalesRangeCode) {
        this.fiClientAnnualSalesRangeCode = fiClientAnnualSalesRangeCode;
    }

}
