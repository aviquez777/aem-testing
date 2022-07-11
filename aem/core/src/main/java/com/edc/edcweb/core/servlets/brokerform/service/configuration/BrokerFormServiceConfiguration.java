package com.edc.edcweb.core.servlets.brokerform.service.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Broker Form Service Configuration", description = "Broker Form Service configuration integration")
public @interface BrokerFormServiceConfiguration {

    @AttributeDefinition(name = "Email from", description = "Specify the email account who send the email", type = AttributeType.STRING)
    String getEmailFrom();

    @AttributeDefinition(name = "Email to", description = "Specify the email account who receive the email", type = AttributeType.STRING)
    String getEmailTo();

    @AttributeDefinition(name = "Email CC", description = "Specify the email account who receive the email", type = AttributeType.STRING)
    String getEmailCc();
}
