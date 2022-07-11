package com.edc.edcweb.core.components;

import java.util.Calendar;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class UpcomingWBCEventCard {

    @Inject
    @Default(values = "")
    private String eventType;

    @Inject
    @Default(values = "")
    private String linkText;

    @Inject
    @Default(values = "")
    private String linkUrl;

    @Inject
    @Default(values = "")
    private String linkTarget;

    @Inject
    @Default(values = "")
    private String description;

    @Inject
    @Default(values = "")
    private String image;

    @Inject
    @Default(values = "")
    private String imageAlt;

    @Inject
    @Default(values = "")
    private String webinarDateText;

    @Inject
    @Default(values = "")
    private Calendar webinarStartDateTime;

    @Inject
    @Default(values = "")
    private Calendar webinarEndDateTime;

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public void setLinkTarget(String linkTarget) {
        this.linkTarget = linkTarget;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setImageAlt(String imageAlt) {
        this.imageAlt = imageAlt;
    }

    public void setWebinarDateText(String webinarDateText) {
        this.webinarDateText = webinarDateText;
    }

    public void setWebinarStartDateTime(Calendar webinarStartDateTime) {
        this.webinarStartDateTime = webinarStartDateTime;
    }

    public void setWebinarEndDateTime(Calendar webinarEndDateTime) {
        this.webinarEndDateTime = webinarEndDateTime;
    }

    public String getEventType() {
        return this.eventType;
    }

    public String getLinkText() {
        return this.linkText;
    }

    public String getLinkUrl() {
        return this.linkUrl;
    }

    public String getLinkTarget() {
        return this.linkTarget;
    }

    public String getDescription() {
        return this.description;
    }

    public String getImage() {
        return this.image;
    }

    public String getImageAlt() {
        return this.imageAlt;
    }

    public String getWebinarDateText() {
        return this.webinarDateText;
    }

    public Calendar getWebinarStartDateTime() {
        return this.webinarStartDateTime;
    }

    public Calendar getWebinarEndDateTime() {
        return this.webinarEndDateTime;
    }
}
