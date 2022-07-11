package com.edc.edcweb.core.onlinepayments.services.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "EDC On Line Payments Configuration", description = "EDC On Line Payments Configuration")
public @interface MonerisServiceConfiguration {

    @AttributeDefinition(name = "Moneris Url", description = "Moneris Url", type = AttributeType.STRING)
    String getMonerisUrl();

    @AttributeDefinition(name = "Moneris CAD Store Id", description = "Moneris CAN Dollars Store Id", type = AttributeType.STRING)
    String getCADStoreId();

    @AttributeDefinition(name = "Moneris CAD API Token", description = "Moneris CAN Dollars API Token", type = AttributeType.STRING)
    String getCADApiToken();

    @AttributeDefinition(name = "Moneris CAD Checkout Id", description = "Moneris CAN Dollars Checkout Id", type = AttributeType.STRING)
    String getCADCheckoutId();

    @AttributeDefinition(name = "Moneris USD Store Id", description = "Moneris US Dollars Store Id", type = AttributeType.STRING)
    String getUSDStoreId();

    @AttributeDefinition(name = "Moneris USD API Token", description = "Moneris US DollarsAPI Token", type = AttributeType.STRING)
    String getUSDApiToken();

    @AttributeDefinition(name = "Moneris USD Checkout Id", description = "Moneris US Dollars Checkout Id", type = AttributeType.STRING)
    String getUSDCheckoutId();
    
    @AttributeDefinition(name = "Moneris Environment", description = "Moneris Environment", type = AttributeType.STRING)
    String getEnvironment() default "qa";

    @AttributeDefinition(name = "Moneris Library JavasScript URL", description = "Moneris Library JavasScript URL", type = AttributeType.STRING)
    String getJavaScriptUrl();

}
