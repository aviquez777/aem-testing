package com.edc.edcportal.core.helpers;

import java.util.UUID;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = SlingHttpServletRequest.class)
public class GenerateUuid {

    /**
     * Will generate a Universally Unique IDentifier (UUID).
     *
     * @return String Generated UUID.
     */
    public String getUuid() {
        return UUID.randomUUID().toString();
    }
}
