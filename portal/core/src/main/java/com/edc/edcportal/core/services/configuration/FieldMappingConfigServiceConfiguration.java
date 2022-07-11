package com.edc.edcportal.core.services.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
/**
 * OSGi configuration model. This model is used by an OSGi service to handle its
 * configuration.
 */
@ObjectClassDefinition(name = "Field Mapping Config Service Configuration", description = "Field Mapping Config Service Configuration")
public @interface FieldMappingConfigServiceConfiguration {

    @AttributeDefinition(name = "getExternalUserIdMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getExternalUserIdMap() default {};

    @AttributeDefinition(name = "getFirstNameMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getFirstNameMap()  default {};

    @AttributeDefinition(name = "getLastNameMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getLastNameMap()  default {};

    @AttributeDefinition(name = "getEmailIdMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getEmailIdMap()  default {};

    @AttributeDefinition(name = "getMobileNumberMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getMobileNumberMap()  default {};

    @AttributeDefinition(name = "getProfileFieldIdMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getProfileFieldIdMap()  default {};

    @AttributeDefinition(name = "getMainPhoneMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getMainPhoneMap()  default {};

    @AttributeDefinition(name = "getPhoneExtMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getPhoneExtMap()  default {};

    @AttributeDefinition(name = "getTitleMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getTitleMap()  default {};

    @AttributeDefinition(name = "getCompanyNameMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getCompanyNameMap()  default {};

    @AttributeDefinition(name = "getTradeStatusMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getTradeStatusMap()  default {};

    @AttributeDefinition(name = "getCompanyAddress1Map", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getCompanyAddress1Map()  default {};

    @AttributeDefinition(name = "getCompanyAddress2Map", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getCompanyAddress2Map()  default {};

    @AttributeDefinition(name = "getCompanyCityMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getCompanyCityMap()  default {};

    @AttributeDefinition(name = "getCompanyCountryMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getCompanyCountryMap()  default {};

    @AttributeDefinition(name = "getCompanyProvinceMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getCompanyProvinceMap()  default {};

    @AttributeDefinition(name = "getCompanyPostalMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getCompanyPostalMap()  default {};

    @AttributeDefinition(name = "getAnnualSalesMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getAnnualSalesMap()  default {};

    @AttributeDefinition(name = "getEmployeesMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getEmployeesMap()  default {};

    @AttributeDefinition(name = "getPainPointMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getPainPointMap()  default {};

    @AttributeDefinition(name = "getExternalUserIdMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getIndustryMap()  default {};

    @AttributeDefinition(name = "getExternalUserIdMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getMarketsIntMap()  default {};

    @AttributeDefinition(name = "getCreatedDateTimeFieldIdMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getCreatedDateTimeFieldIdMap()  default {};

    @AttributeDefinition(name = "getLanguageMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getLanguageMap()  default {};

    @AttributeDefinition(name = "getReferralUrlMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getReferralUrlMap()  default {};

    @AttributeDefinition(name = "getElqCustomerGuidMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getElqCustomerGuidMap()  default {};

    @AttributeDefinition(name = "getProductMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getProductFieldIdMap() default {};

    @AttributeDefinition(name = "getProductDescriptionMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getProductDescriptionFieldIdMap() default {};

    @AttributeDefinition(name = "getOldEmailMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getOldEmailMap() default {};

    @AttributeDefinition(name = "getEmailChangedMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getEmailChangedMap() default {};

    @AttributeDefinition(name = "getAemPathFieldIdMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getAemPathFieldIdMap() default {};

    @AttributeDefinition(name = "getCompanyProvinceAltMap", description = "**This field is needed to map the Internal Name. Does not have a corresponding field on Eloqua. Important: To simulate the ElquaID, array pos 1 must be unique.", type = AttributeType.STRING)
    String[] getCompanyProvinceAltMap()  default {"companyProvinceAlt","00"};

    @AttributeDefinition(name = "getCompanyProvinceInputMap", description = "**This field is needed to map the Internal Name. Does not have a corresponding field on Eloqua. Important: To simulate the ElquaID, array pos 1 must be unique.", type = AttributeType.STRING)
    String[] getCompanyProvinceInputMap()  default {"companyProvinceInput","01"};

    @AttributeDefinition(name = "getDataShareConsentMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id.", type = AttributeType.STRING)
    String[] getDataShareConsentMap()  default {};

    @AttributeDefinition(name = "getInitialTrafficSrcMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id.", type = AttributeType.STRING)
    String[] getInitialTrafficSrcMap() default {};

    @AttributeDefinition(name = "getFinalTrafficSrcMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id.", type = AttributeType.STRING)
    String[] getFinalTrafficSrcMap() default {};

    @AttributeDefinition(name = "getRenewalTrafficSrcMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id.", type = AttributeType.STRING)
    String[] getRenewalTrafficSrcMap() default {};

    @AttributeDefinition(name = "getRenewalUtmSourceMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id.", type = AttributeType.STRING)
    String[] getRenewalUtmSourceMap() default {};

    @AttributeDefinition(name = "getRenewalUtmMediumMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id.", type = AttributeType.STRING)
    String[] getRenewalUtmMediumMap() default {};

    @AttributeDefinition(name = "getRenewalUtmCampaignMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id.", type = AttributeType.STRING)
    String[] getRenewalUtmCampaignMap() default {};

    @AttributeDefinition(name = "getRenewalUtmContentMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id.", type = AttributeType.STRING)
    String[] getRenewalUtmContentMap() default {};

    @AttributeDefinition(name = "getRenewalUtmTermMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id.", type = AttributeType.STRING)
    String[] getRenewalUtmTermMap() default {};

    @AttributeDefinition(name = "getITMValueMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getITMValueMap()  default {};

    @AttributeDefinition(name = "getITMOtherMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getITMOtherMap()  default {};

    @AttributeDefinition(name = "getCompanyNameCanMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getCompanyNameCanMap()  default {};

    @AttributeDefinition(name = "getCompanyNameOtherMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getCompanyNameOtherMap()  default {};

    @AttributeDefinition(name = "getFiJobRoleMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getFiJobRoleMap() default {};

    @AttributeDefinition(name = "getFiJobRoleMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getFiOtherJobRoleMap() default {};
    
    @AttributeDefinition(name = "getFiClientsAnnualSalesMap", description = "Array containing the fields mapping. Important: Required Array pos 0 & 1. Array pos 0 = FormName, array pos 1 = ElquaID, array pos 2 = Progressive Profile CDO Id", type = AttributeType.STRING)
    String[] getFiClientsAnnualSalesMap() default {};
}