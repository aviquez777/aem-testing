package com.edc.edcweb.core.onlinepayments.services;

public interface MonerisConfigurationService {

    String getMonerisUrl();

    String getCADStoreId();

    String getCADApiToken();

    String getCADCheckoutId();

    String getUSDStoreId();

    String getUSDApiToken();

    String getUSDCheckoutId();

    String getEnvironment();

    String getJavaScriptUrl();
}
