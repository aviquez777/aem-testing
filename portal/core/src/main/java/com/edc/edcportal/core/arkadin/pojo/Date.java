package com.edc.edcportal.core.arkadin.pojo;

import javax.xml.bind.annotation.XmlAttribute;

public class Date {
    private String dateType;
    private String fromDateTime;
    private String toDateTime;

    public String getDateType() {
        return dateType;
    }

    @XmlAttribute(name = "DateType")
    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public String getFromDateTime() {
        return fromDateTime;
    }

    @XmlAttribute(name = "FromDateTime")
    public void setFromDateTime(String fromDateTime) {
        this.fromDateTime = fromDateTime;
    }

    public String getToDateTime() {
        return toDateTime;
    }

    @XmlAttribute(name = "ToDateTime")
    public void setToDateTime(String toDateTime) {
        this.toDateTime = toDateTime;
    }
}
