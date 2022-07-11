package com.edc.edcweb.core.components;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class UpcomingWBCEventsRetrieverComponent {

    @Inject
    private String makeFullWidth;

    @Inject
    @Default(values = "No upcoming virtual events at this moment.")
    private String errortitle;

    @Inject
    @Default(values = "Check back with us soon for new releases.")
    private String errortext;

    @Inject
    private String heading;

    @Inject
    @Default(values = "align-left")
    private String headingAlignment;

    @Inject
    private String[] paths;

    @Inject
    private String[] tags;

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getHeading() {
        return this.heading;
    }

    public void setHeadingAligment(String headingAlignment) {
        this.headingAlignment = headingAlignment;
    }

    public String getHeadingAlignment() {
        return this.headingAlignment;
    }

    public void setPaths(String[] paths) {
        this.paths = paths;
    }

    public String[] getPaths() {
        return this.paths;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String[] getTags() {
        return this.tags;
    }

    public void setMakeFullWidth(String makeFullWidth) {
        this.makeFullWidth = makeFullWidth;
    }

    public void setErrorTitle(String errortitle) {
        this.errortitle = errortitle;
    }

    public void setErrorText(String errortext) {
        this.errortext = errortext;
    }

    public String getMakeFullWidth() {
        return this.makeFullWidth;
    }

    public String getErrorTitle() {
        return this.errortitle;
    }

    public String getErrorText() {
        return this.errortext;
    }
}
