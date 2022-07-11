package com.edc.edcportal.core.services.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.services.FieldMappingConfigService;
import com.edc.edcportal.core.services.configuration.FieldMappingConfigServiceConfiguration;

@Component(immediate = true, service = FieldMappingConfigService.class, configurationPid = "com.edc.portal.core.services.configuration.impl.FieldMappingConfigServiceImpl")
@Designate(ocd = FieldMappingConfigServiceConfiguration.class)
public class FieldMappingConfigServiceImpl implements FieldMappingConfigService {

    private static final Logger logger = LoggerFactory.getLogger(FieldMappingConfigServiceImpl.class);

    private FieldMappingConfigServiceConfiguration config;

    @Activate
    @Modified
    protected void activate(FieldMappingConfigServiceConfiguration config) {
        this.config = config;
        logger.debug("Activated MyEDCConfigSystemService");
    }

    @Override
    public String[] getExternalUserIdMap() {
        return config.getExternalUserIdMap();
    }

    @Override
    public String[] getFirstNameMap() {
        return config.getFirstNameMap();
    }

    @Override
    public String[] getLastNameMap() {
        return config.getLastNameMap();
    }

    @Override
    public String[] getEmailIdMap() {
        return config.getEmailIdMap();
    }

    @Override
    public String[] getMobileNumberMap() {
        return config.getMobileNumberMap();
    }

    @Override
    public String[] getProfileFieldIdMap() {
        return config.getProfileFieldIdMap();
    }

    @Override
    public String[] getMainPhoneMap() {
        return config.getMainPhoneMap();
    }

    @Override
    public String[] getPhoneExtMap() {
        return config.getPhoneExtMap();
    }

    @Override
    public String[] getTitleMap() {
        return config.getTitleMap();
    }

    @Override
    public String[] getCompanyNameMap() {
        return config.getCompanyNameMap();
    }

    @Override
    public String[] getTradeStatusMap() {
        return config.getTradeStatusMap();
    }

    @Override
    public String[] getCompanyAddress1Map() {
        return config.getCompanyAddress1Map();
    }

    @Override
    public String[] getCompanyAddress2Map() {
        return config.getCompanyAddress2Map();
    }

    @Override
    public String[] getCompanyCityMap() {
        return config.getCompanyCityMap();
    }

    @Override
    public String[] getCompanyCountryMap() {
        return config.getCompanyCountryMap();
    }

    @Override
    public String[] getCompanyProvinceMap() {
        return config.getCompanyProvinceMap();
    }

    @Override
    public String[] getCompanyProvinceAltMap() {
        return config.getCompanyProvinceAltMap();
    }

    @Override
    public String[] getCompanyProvinceInputMap() {
        return config.getCompanyProvinceInputMap();
    }

    @Override
    public String[] getCompanyPostalMap() {
        return config.getCompanyPostalMap();
    }

    @Override
    public String[] getAnnualSalesMap() {
        return config.getAnnualSalesMap();
    }

    @Override
    public String[] getEmployeesMap() {
        return config.getEmployeesMap();
    }

    @Override
    public String[] getPainPointMap() {
        return config.getPainPointMap();
    }

    @Override
    public String[] getIndustryMap() {
        return config.getIndustryMap();
    }

    @Override
    public String[] getMarketsIntMap() {
        return config.getMarketsIntMap();
    }

    @Override
    public String[] getCreatedDateTimeFieldIdMap() {
        return config.getCreatedDateTimeFieldIdMap();
    }

    @Override
    public String[] getProductFieldIdMap() {
        return config.getProductFieldIdMap();
    }

    @Override
    public String[] getProductDescriptionFieldIdMap() {
        return config.getProductDescriptionFieldIdMap();
    }

    @Override
    public String[] getLanguageMap() {
        return config.getLanguageMap();
    }

    @Override
    public String[] getReferralUrlMap() {
        return config.getReferralUrlMap();
    }

    @Override
    public String[] getElqCustomerGuidMap() {
        return config.getElqCustomerGuidMap();
    }

    @Override
    public String[] getOldEmailMap() {
        return config.getOldEmailMap();
    }

    @Override
    public String[] getEmailChangedMap() {
        return config.getEmailChangedMap();
    }

    @Override
    public String[] getAemPathFieldIdMap() {
        return config.getAemPathFieldIdMap();
    }

    @Override
    public String[] getDataShareConsentMap() {
        return config.getDataShareConsentMap();
    }

    @Override
    public String[] getInitialTrafficSrcMap() {
        return config.getInitialTrafficSrcMap();
    }

    @Override
    public String[] getFinalTrafficSrcMap() {
        return config.getFinalTrafficSrcMap();
    }

    @Override
    public String[] getRenewalTrafficSrcMap() {
        return config.getRenewalTrafficSrcMap();
    }

    @Override
    public String[] getRenewalUtmSourceMap() {
        return config.getRenewalUtmSourceMap();
    }

    @Override
    public String[] getRenewalUtmMediumMap() {
        return config.getRenewalUtmMediumMap();
    }

    @Override
    public String[] getRenewalUtmCampaignMap() {
        return config.getRenewalUtmCampaignMap();
    }

    @Override
    public String[] getRenewalUtmContentMap() {
        return config.getRenewalUtmContentMap();
    }

    @Override
    public String[] getRenewalUtmTermMap() {
        return config.getRenewalUtmTermMap();
    }

    @Override
    public String[] getITMValueMap() {
        return config.getITMValueMap();
    }

    @Override
    public String[] getITMOtherMap() {
        return config.getITMOtherMap();
    }

    @Override
    public String[] getCompanyNameCanMap() {
        return config.getCompanyNameCanMap();
    }

    @Override
    public String[] getCompanyNameOtherMap() {
        return config.getCompanyNameOtherMap();
    }

    @Override
    public String[] getFiJobRoleMap() {
        return config.getFiJobRoleMap();
    }

    @Override
    public String[] getFiOtherJobRoleMap() {
        return config.getFiOtherJobRoleMap();
    }

    @Override
    public String[] getFiClientsAnnualSalesMap() {
        return config.getFiClientsAnnualSalesMap();
    }
}
