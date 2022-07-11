package com.edc.edcportal.core.arkadin.pojo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.ImmutableList;

@XmlRootElement(name = "APIStatus")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ArkadinUserActivityPO {
    private String code;
    private String diag;
    private List<Show> shows;

    public String getCode() {
        return code;
    }

    @XmlAttribute(name = "Code")
    public void setCode(String code) {
        this.code = code;
    }

    public String getDiag() {
        return diag;
    }

    @XmlAttribute(name = "Diag")
    public void setDiag(String diag) {
        this.diag = diag;
    }

    public List<Show> getShows() {
     // Task 22143 squid:S2384
     return shows != null ? ImmutableList.copyOf(shows) : shows;
    }

    // Task 22143 squid:S2384
    @SuppressWarnings("squid:S2384")
    @XmlElementWrapper(name = "Shows") 
    @XmlElement(name = "Show")
    public void setShows(List<Show> shows) {
        /// we cannot store as an inmutable copy as we need to we need to modify the
        /// content on the property later.
        this.shows = shows;
    }

}
