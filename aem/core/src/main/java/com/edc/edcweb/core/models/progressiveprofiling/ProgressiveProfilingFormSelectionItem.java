package com.edc.edcweb.core.models.progressiveprofiling;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

@Model(adaptables = Resource.class)
public class ProgressiveProfilingFormSelectionItem {

    @Inject
    @Named("displayText")
    @Optional
    private String displayText;

    @Inject
    @Named("eloquaValue")
    @Optional
    private String eloquaValue;

    @PostConstruct
    protected void init() {
        // Sonar Lint
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public String getEloquaValue() {
        return eloquaValue;
    }

    public void setEloquaValue(String eloquaValue) {
        this.eloquaValue = eloquaValue;
    }

}
