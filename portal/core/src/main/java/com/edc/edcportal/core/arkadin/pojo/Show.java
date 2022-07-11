package com.edc.edcportal.core.arkadin.pojo;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.google.common.collect.ImmutableList;

public class Show {
    private String showKey;
    private String regDate;
    private String title;
    private String uniqueUserID;
    private List<Date> dates;

    public String getShowKey() {
        return showKey;
    }

    @XmlAttribute(name = "ShowKey")
    public void setShowKey(String showKey) {
        this.showKey = showKey;
    }

    public String getRegDate() {
        return regDate;
    }

    @XmlAttribute(name = "RegDate")
    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUniqueUserID() {
        return uniqueUserID;
    }

    @XmlAttribute(name = "UniqueUserID")
    public void setUniqueUserID(String uniqueUserID) {
        this.uniqueUserID = uniqueUserID;
    }

    public List<Date> getDates() {
        // Task 22143 squid:S2384
        return dates != null ? ImmutableList.copyOf(dates) : dates;
    }

    // Task 22143 squid:S2384
    @SuppressWarnings("squid:S2384")
    @XmlElementWrapper(name = "Dates")
    @XmlElement(name = "Date")
    public void setDates(List<Date> dates) {
        /// we cannot store as an inmutable copy as we need to we need to modify the
        /// content on the property later.
        this.dates = dates;
    }

}
