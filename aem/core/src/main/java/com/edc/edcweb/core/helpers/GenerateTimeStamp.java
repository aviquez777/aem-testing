package com.edc.edcweb.core.helpers;

import java.sql.Timestamp;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = SlingHttpServletRequest.class)

public class GenerateTimeStamp {

    private String strTimestamp;

    @PostConstruct
    public void initModel() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        strTimestamp = Long.toString(timestamp.getTime());
    }

    public String getStrTimestamp() {
        return strTimestamp;
    }

}
