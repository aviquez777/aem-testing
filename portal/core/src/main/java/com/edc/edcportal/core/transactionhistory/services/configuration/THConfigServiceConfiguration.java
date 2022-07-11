package com.edc.edcportal.core.transactionhistory.services.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Transaction History Service Configuration", description = "Transaction History for MyEDC integration")
public @interface THConfigServiceConfiguration {

    @AttributeDefinition(name = "Email Adress", description = "Get Email Adress Field Id", type = AttributeType.STRING)
    String getEmailAdressFieldId() default "";

    @AttributeDefinition(name = "ACC ExternalID AEM pathd", description = "Get EACC ExternalID | AEM path Field Id", type = AttributeType.STRING)
    String getExternalIDAEMPathFieldId() default "";

    @AttributeDefinition(name = "ACC ExternalID", description = "Get ACC ExternalID Field Id", type = AttributeType.STRING)
    String getExternalIDFieldId() default "";

    @AttributeDefinition(name = "AEM path", description = "Get AEM path Field Id", type = AttributeType.STRING)
    String getAEMpathFieldId() default "";

    @AttributeDefinition(name = "Counter", description = "Get Counter Field Id", type = AttributeType.STRING)
    String getCounterFieldId() default "";

    @AttributeDefinition(name = "Eloqua Traffic Source", description = "Get Eloqua Traffic Source Field Id", type = AttributeType.STRING)
    String getEloquaTrafficSourceFieldId() default "";

    @AttributeDefinition(name = "Webinar Partner T&C", description = "Webinar Partner T&C Field Id", type = AttributeType.STRING)
    String getParnersTCFieldId() default "";

    @AttributeDefinition(name = "Webinar Partner CASL", description = "Webinar Partner CASL Field Id", type = AttributeType.STRING)
    String getParnersCASLFieldId() default "";

    @AttributeDefinition(name = "Last accessed timestamp", description = "Last accessed timestamp Field Id", type = AttributeType.STRING)
    String getTimestampFieldId() default "";

    @AttributeDefinition(name = "Women In Trade", description = "Women In Trade Field Id", type = AttributeType.STRING)
    String getWitFieldId() default "";
}