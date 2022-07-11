package com.edc.edcportal.core.services;

public interface FieldMappingConfigService {

    String[] getExternalUserIdMap();
    String[] getFirstNameMap();
    String[] getLastNameMap();
    String[] getEmailIdMap();
    String[] getMobileNumberMap();
    String[] getProfileFieldIdMap();
    String[] getMainPhoneMap();
    String[] getPhoneExtMap();
    String[] getTitleMap();
    String[] getCompanyNameMap();
    String[] getTradeStatusMap();
    String[] getCompanyAddress1Map();
    String[] getCompanyAddress2Map();
    String[] getCompanyCityMap();
    String[] getCompanyCountryMap();
    String[] getCompanyProvinceMap();
    String[] getCompanyProvinceAltMap();
    String[] getCompanyProvinceInputMap();
    String[] getCompanyPostalMap();
    String[] getAnnualSalesMap();
    String[] getEmployeesMap();
    String[] getPainPointMap();
    String[] getIndustryMap();
    String[] getMarketsIntMap();
    String[] getCreatedDateTimeFieldIdMap();
    String[] getProductFieldIdMap();
    String[] getProductDescriptionFieldIdMap();
    String[] getLanguageMap();
    String[] getReferralUrlMap();
    String[] getElqCustomerGuidMap();
    String[] getOldEmailMap();
    String[] getEmailChangedMap();
    String[] getAemPathFieldIdMap();
    String[] getDataShareConsentMap();
    String[] getInitialTrafficSrcMap();
    String[] getFinalTrafficSrcMap();
    String[] getRenewalTrafficSrcMap();
    String[] getRenewalUtmSourceMap();
    String[] getRenewalUtmMediumMap();
    String[] getRenewalUtmCampaignMap();
    String[] getRenewalUtmContentMap();
    String[] getRenewalUtmTermMap();

    String[] getITMValueMap();
    String[] getITMOtherMap();

    String[] getCompanyNameCanMap();
    String[] getCompanyNameOtherMap();
    String[] getFiJobRoleMap();
    String[] getFiOtherJobRoleMap();
    String[] getFiClientsAnnualSalesMap();

}
