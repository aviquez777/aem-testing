package com.edc.edcweb.core.apim.services;

import com.edc.edcweb.core.apim.pojo.APIMTokenDO;

public interface APIMDAOService {
    APIMTokenDO getAccessToken(boolean forceRefresh);
    String getConfigProxyURL();
    String getConfigSubscriptionKey();
    
    String getAccessTokenURL();
    String getClientId();
    String getClientSecret();
    String getGrandType();
    String getResource();
    String getProxyURL();
    String getSubscriptionKey();
    
    String getServicePointInListSupplierFilter();
    String getServicePointInListSupplierProfile();
}
