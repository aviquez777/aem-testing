package com.edc.edcweb.core.models;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

/**
 * @author Monica Castillo
 * @version 1.0.0
 * @since 1.0.0
 *
 *
 * Model to retrieve information for time options, properties being text and value.
 *
 *
 */
@Model(adaptables = Resource.class)
public class TimeOptions
{

    @Inject
    @Named("timetext")
    @Optional
    private String timetext;

    @Inject
    @Named("fromtime")
    @Optional
    private String fromtime;

    @Inject
    @Named("totime")
    @Optional
    private String totime;


    /**
     *
     * @return the text for the dropdown option
     */
    public String getTimetext() {
        return this.timetext;
    }

    /**
     *
     * @return the start time for the dropdown option
     */
    public String getFromtime() {
        return this.fromtime;
    }

    /**
     *
     * @return  the end time for the dropdown option
     */
    public String getTotime() {
        return this.totime;
    }
}
