package com.edc.edcweb.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Monica Castillo
 * @version 1.0.0
 * @since 1.0.0
 *
 *
 * Model to retrieve information for inquiry type options, properties being text and value.
 *
 *
 */
@Model(adaptables = Resource.class)
public class InquiryTypeOptions
{

    @Inject
    @Named("inquirytext")
    @Optional
    private String inquirytext;

    @Inject
    @Named("inquiryvalue")
    @Optional
    private String inquiryvalue;


    /**
     *
     * @return the text for the dropdown option
     */
    public String getInquirytext() {
        return inquirytext;
    }

    /**
     *
     * @return the value for the dropdown option
     */
    public String getInquiryvalue() {
        return inquiryvalue;
    }
}
