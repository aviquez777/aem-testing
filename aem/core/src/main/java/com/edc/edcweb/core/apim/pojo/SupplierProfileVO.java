package com.edc.edcweb.core.apim.pojo;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;

public class SupplierProfileVO {

    private String id;
    private String name;
    private String description;
    private String englishName;
    private String referralResponseTime;
    private String requestQuoteURL;
    private String[] transportationModes;
    private String[] services;
    private String[] industries;
    private String[] marketsServed;
    private String[] certifications;
    private String[] languagesServed;
    private List<Address> addresses = Collections.emptyList();
    private Map<String,String> contact;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String setEnglishName) {
        this.englishName = setEnglishName;
    }

    public String getReferralResponseTime() {
        return referralResponseTime;
    }

    public void setReferralResponseTime(String referralResponseTime) {
        this.referralResponseTime = referralResponseTime;
    }

    public String getRequestQuoteURL() {
        return requestQuoteURL;
    }

    public void setRequestQuoteURL(String requestQuoteURL) {
        this.requestQuoteURL = requestQuoteURL;
    }

    public String[] getTransportationModes() {
        return transportationModes.clone();
    }

    public void setTransportationModes(String[] transportationModes) {
        this.transportationModes = transportationModes.clone();
    }

    public String[] getServices() {
        return services.clone();
    }

    public void setServices(String[] services) {
        this.services = services.clone();
    }

    public String[] getIndustries() {
        return industries.clone();
    }

    public void setIndustries(String[] industries) {
        this.industries = industries.clone();
    }

    public String[] getMarketsServed() {
        return marketsServed.clone();
    }

    public void setMarketsServed(String[] marketsServed) {
        this.marketsServed = marketsServed.clone();
    }

    public String[] getCertifications() {
        return certifications.clone();
    }

    public void setCertifications(String[] certifications) {
        this.certifications = certifications.clone();
    }

    public String[] getLanguagesServed() {
        return languagesServed.clone();
    }

    public void setLanguagesServed(String[] languagesServed) {
        this.languagesServed = languagesServed.clone();
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        addresses = new ArrayList<>(addresses);
        this.addresses = Collections.unmodifiableList(addresses);
    }

    public Map<String, String> getContact() {
        return contact;
    }

    public void setContact(Map<String, String> contact) {
        this.contact = contact;
    }

    //Address
    public class Address {

        private String street;
        private String city;
        private String province;
        private String postCode;
        private String country;
        private boolean isHeadQuater = false;

        public String getStreet() {
            return street;
        }
        public void setStreet(String street) {
            this.street = street;
        }
        public String getCity() {
            return city;
        }
        public void setCity(String city) {
            this.city = city;
        }
        public String getProvince() {
            return province;
        }
        public void setProvince(String province) {
            this.province = province;
        }
        public String getPostCode() {
            return postCode;
        }
        public void setPostCode(String postCode) {
            this.postCode = postCode;
        }
        public String getCountry() {
            return country;
        }
        public void setCountry(String country) {
            this.country = country;
        }
        public boolean isHeadQuater() {
            return isHeadQuater;
        }
        public void setHeadQuater(boolean isHeadQuater) {
            this.isHeadQuater = isHeadQuater;
        }
    }
}
