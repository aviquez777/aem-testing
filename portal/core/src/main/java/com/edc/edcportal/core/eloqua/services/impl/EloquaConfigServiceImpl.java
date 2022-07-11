package com.edc.edcportal.core.eloqua.services.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.eloqua.services.EloquaConfigService;
import com.edc.edcportal.core.eloqua.services.configuration.EloquaConfigServiceConfiguration;
import com.edc.edcportal.core.helpers.FieldMapUtils;
import com.edc.edcportal.core.services.FieldMappingConfigService;

@Component(immediate = true, service = EloquaConfigService.class, configurationPid = "com.edc.portal.core.eloqua.services.configuration.impl.EloquaConfigServiceImpl")

@Designate(ocd = EloquaConfigServiceConfiguration.class)
public class EloquaConfigServiceImpl implements EloquaConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EloquaConfigServiceImpl.class);

    private EloquaConfigServiceConfiguration config;

    @Reference
    private FieldMappingConfigService fieldMappingConfigService;

    @Activate
    @Modified
    protected void activate(EloquaConfigServiceConfiguration config) {
        this.config = config;
        LOGGER.debug("Activated EloquaConfigurationService");
    }

    @Override
    public String getClientId() {
        return config.getClientId();
    }

    @Override
    public String getUrlToGetBaseUrl() {
        return config.getUrlToGetBaseUrl();
    }

    @Override
    public String getClientSecret() {
        return config.getClientSecret();
    }

    @Override
    public String getTokenUrl() {
        return config.getTokenUrl();
    }

    @Override
    public String getSiteName() {
        return config.getSiteName();
    }

    @Override
    public String getUserName() {
        return config.getUserName();
    }

    @Override
    public String getPassword() {
        return config.getPassword();
    }

    @Override
    public String getProgressiveProfileCDOId() {
        return config.getProgressiveProfileCDOId();
    }

    @Override
    public String getDocumentHistoryCDOId() {
        return config.getDocumentHistoryCDOId();
    }

    @Override
    public String getMyEDCProfileCDOId() {
        return config.getMyEDCProfileCDOId();
    }

    @Override
    public String getEndPointFallBack() {
        return config.getEndPointFallBack();
    }

    /*
     * Methods below are read from the fieldMappingConfigService and are not on the
     * configuration xml file. This will maintain any eloquaid related requests on
     * the EloquaConfigService
     */
    @Override
    public String getExtenalIdFieldId() {
        String[] arr = fieldMappingConfigService.getExternalUserIdMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    public String getFirstNameFieldId() {
        String[] arr = fieldMappingConfigService.getFirstNameMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getLastNameFieldId() {
        String[] arr = fieldMappingConfigService.getLastNameMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getEmailIdFieldId() {
        String[] arr = fieldMappingConfigService.getEmailIdMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getMobileNumberFieldId() {
        String[] arr = fieldMappingConfigService.getMobileNumberMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getProfileFieldId() {
        String[] arr = fieldMappingConfigService.getProfileFieldIdMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getCompanyIdFieldId() {
        String[] arr = fieldMappingConfigService.getCompanyNameMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getProvinceIdFieldId() {
        String[] arr = fieldMappingConfigService.getCompanyProvinceMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getCreatedDateTimeFieldId() {
        String[] arr = fieldMappingConfigService.getCreatedDateTimeFieldIdMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getProductFieldId() {
        String[] arr = fieldMappingConfigService.getProductFieldIdMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getProductDescriptionFieldId() {
        String[] arr = fieldMappingConfigService.getProductDescriptionFieldIdMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getMarketsIntFieldId() {
        String[] arr = fieldMappingConfigService.getMarketsIntMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getGuidFieldId() {
        String[] arr = fieldMappingConfigService.getElqCustomerGuidMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getOldEmailId() {
        String[] arr = fieldMappingConfigService.getOldEmailMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getEmailChangedId() {
        String[] arr = fieldMappingConfigService.getEmailChangedMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getTransAemPathFieldId() {
        String[] arr = fieldMappingConfigService.getAemPathFieldIdMap();
        return FieldMapUtils.getTransId(arr);
    }

    @Override
    public String getLangdId() {
        String[] arr = fieldMappingConfigService.getLanguageMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getGuidId() {
        String[] arr = fieldMappingConfigService.getElqCustomerGuidMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getDataShareConsent() {
        String[] arr = fieldMappingConfigService.getDataShareConsentMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getInitialTrafficSrc() {
        String[] arr = fieldMappingConfigService.getInitialTrafficSrcMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getFinalTrafficSrc() {
        String[] arr = fieldMappingConfigService.getFinalTrafficSrcMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getPainPointsFieldId() {
        String[] arr = fieldMappingConfigService.getPainPointMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getItmValueFieldId() {
        String[] arr = fieldMappingConfigService.getITMValueMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getItmOtherFieldId() {
        String[] arr = fieldMappingConfigService.getITMOtherMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getCompanyNameCan() {
        String[] arr = fieldMappingConfigService.getCompanyNameCanMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getCompanyNameOther() {
        String[] arr = fieldMappingConfigService.getCompanyNameOtherMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getFiJobRole() {
        String[] arr = fieldMappingConfigService.getFiJobRoleMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    @Override
    public String getOtherFiJobRole() {
        String[] arr = fieldMappingConfigService.getFiOtherJobRoleMap();
        return FieldMapUtils.getEloquaId(arr);
    }
    
    @Override
    public String getFiClientsAnnualSales() {
        String[] arr = fieldMappingConfigService.getFiClientsAnnualSalesMap();
        return FieldMapUtils.getEloquaId(arr);
    }

    
}