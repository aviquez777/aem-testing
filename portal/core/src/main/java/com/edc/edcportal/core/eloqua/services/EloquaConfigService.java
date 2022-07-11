package com.edc.edcportal.core.eloqua.services;

/**
 * Eloqua configuration service
 *
 */

public interface EloquaConfigService {

    String getUrlToGetBaseUrl();

    String getTokenUrl();

    String getSiteName();

    String getClientId();

    String getClientSecret();

    String getUserName();

    String getPassword();

    String getMyEDCProfileCDOId();

    String getProgressiveProfileCDOId();

    String getDocumentHistoryCDOId();

    String getExtenalIdFieldId();

    String getFirstNameFieldId();

    String getLastNameFieldId();

    String getEmailIdFieldId();

    String getMobileNumberFieldId();

    String getProfileFieldId();

    String getCompanyIdFieldId();

    String getProvinceIdFieldId();

    String getMarketsIntFieldId();

    String getCreatedDateTimeFieldId();

    String getProductFieldId();

    String getProductDescriptionFieldId();

    String getGuidFieldId();

    String getGuidId();

    String getOldEmailId();

    String getEmailChangedId();

    String getTransAemPathFieldId();

    String getEndPointFallBack();

    String getLangdId();

    String getDataShareConsent();

    String getInitialTrafficSrc();

    String getFinalTrafficSrc();

    String getPainPointsFieldId();

    String getItmValueFieldId();

    String getItmOtherFieldId();

    String getCompanyNameCan();

    String getCompanyNameOther();

    String getFiJobRole();

    String getOtherFiJobRole();

    String getFiClientsAnnualSales();
}
