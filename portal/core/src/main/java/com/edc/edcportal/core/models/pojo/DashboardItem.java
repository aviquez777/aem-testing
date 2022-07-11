package com.edc.edcportal.core.models.pojo;

import com.adobe.cq.social.calendar.client.api.Calendar;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

public class DashboardItem {
    private String title;
    private String description;
    private String url;
    private String image;
    private String imageAlign;
    private String imageAlt;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String contentType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String contentTypeName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String webinarStatusClass;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String webinarStatus;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String webinarDateText;
    @JsonIgnore
    private Calendar webinarDate;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageAlign() {
        return imageAlign;
    }

    public void setImageAlign(String imageAlign) {
        this.imageAlign = imageAlign;
    }

    public String getImageAlt() {
        return imageAlt;
    }

    public void setImageAlt(String imageAlt) {
        this.imageAlt = imageAlt;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentTypeName() {
        return contentTypeName;
    }

    public void setContentTypeName(String contentTypeName) {
        this.contentTypeName = contentTypeName;
    }

    public String getWebinarStatusClass() {
        return webinarStatusClass;
    }

    public void setWebinarStatusClass(String webinarStatusClass) {
        this.webinarStatusClass = webinarStatusClass;
    }

    public String getWebinarStatus() {
        return webinarStatus;
    }

    public void setWebinarStatus(String webinarStatus) {
        this.webinarStatus = webinarStatus;
    }

    public String getWebinarDateText() {
        return webinarDateText;
    }

    public void setWebinarDateText(String webinarDateText) {
        this.webinarDateText = webinarDateText;
    }

    public Calendar getWebinarDate() {
        return webinarDate;
    }

    public void setWebinarDate(Calendar webinarDate) {
        this.webinarDate = webinarDate;
    }

}
