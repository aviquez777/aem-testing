package com.edc.edcportal.core.ci.pojo.items;

import java.io.Serializable;

public class CiCompanySearchItemResult implements Serializable {
    /*
     * POJO to Unmarshal JSON
     */

    private static final long serialVersionUID = -870693941258980236L;

    private String companyId;
    private String companyName;
    private String countryName;
    private String province;
    private String city;
    private String streetAddress;
    private String postalCode;
    private String industry;

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}
