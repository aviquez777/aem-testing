package com.edc.edcportal.core.ci.pojo;

import java.io.Serializable;

public class CiSharedObject implements Serializable {
    /**
     * Object to maintain search data on Session
     */

    private static final long serialVersionUID = 6537094834966059950L;

    private CiCompanySearchDO ciCompanySearchDO;
    private String country;
    private String name;
    private String language;

    public CiCompanySearchDO getCiCompanySearchDO() {
        return ciCompanySearchDO;
    }

    public void setCiCompanySearchDO(CiCompanySearchDO ciCompanySearchDO) {
        this.ciCompanySearchDO = ciCompanySearchDO;
    }

    public String getCountry() { 
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

}
