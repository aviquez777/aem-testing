package com.edc.edcweb.core.apim.pojo;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;

public class SupplierCardsAndFiltersVO {

    private List<Supplier> suppliers = Collections.emptyList();
    private Map<String, FilterCategory> filterCategories;
    private List<String> availableCategories = Collections.emptyList();


    public class FilterCategory {

        private String title;
        private boolean matchAll;
        private Map<String,String> options;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean getMatchAll() {
            return matchAll;
        }

        public void setMatchAll(boolean matchAll) {
            this.matchAll = matchAll;
        }

        public Map<String, String> getOptions() {
            return options;
        }

        public void setOptions(Map<String, String> options) {
            this.options = options;
        }
    }

    public class Supplier {

        private String id;
        private String name;
        private String englishName;
        private String briefDescription;
        private String city;
        private String country;
        private String numberOfLocations;
        private String responseTimeId;
        private String serviceTypeIds;
        private String[] services;
        private String servicesIds;
        private String transportationModesIds;
        private String marketsServedIds;
        private String supplierIndustriesIds;

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

        public String getEnglishName() {
            return englishName;
        }

        public void setEnglishName(String englishName) {
            this.englishName = englishName;
        }

        public String getBriefDescription() {
            return briefDescription;
        }

        public void setBriefDescription(String briefDescription) {
            this.briefDescription = briefDescription;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getNumberOfLocations() {
            return numberOfLocations;
        }

        public void setNumberOfLocations(String numberOfLocations) {
            this.numberOfLocations = numberOfLocations;
        }

        public String getResponseTimeId() {
            return responseTimeId;
        }

        public void setResponseTimeId(String responseTimeId) {
            this.responseTimeId = responseTimeId;
        }

        public String getServiceTypeIds() {
            return serviceTypeIds;
        }

        public void setServiceTypeIds(String serviceTypeIds) {
            this.serviceTypeIds = serviceTypeIds;
        }

        public String[] getServices() {
            return services.clone();
        }

        public void setServices(String[] services) {
            this.services = services.clone();
        }

        public String getServicesIds() {
            return servicesIds;
        }

        public void setServicesIds(String servicesIds) {
            this.servicesIds = servicesIds;
        }

        public String getTransportationModesIds() {
            return transportationModesIds;
        }

        public void setTransportationModesIds(String transportationModesIds) {
            this.transportationModesIds = transportationModesIds;
        }

        public String getMarketsServedIds() {
            return marketsServedIds;
        }

        public void setMarketsServedIds(String marketsServedIds) {
            this.marketsServedIds = marketsServedIds;
        }

        public String getSupplierIndustriesIds() {
            return supplierIndustriesIds;
        }

        public void setSupplierIndustriesIds(String supplierIndustriesIds) {
            this.supplierIndustriesIds = supplierIndustriesIds;
        }
    }


    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(List<Supplier> suppliers) {
        availableCategories = new ArrayList<>(availableCategories);
        this.suppliers = Collections.unmodifiableList(suppliers);
    }

    public Map<String, FilterCategory> getFilterCategories() {
        return filterCategories;
    }

    public void setFilterCategories(Map<String, FilterCategory> filterCategories) {
        this.filterCategories = filterCategories;
    }

    public List<String> getAvailableCategories() {
        return availableCategories;
    }

    public void setAvailableCategories(List<String> availableCategories) {
        availableCategories = new ArrayList<>(availableCategories);
        this.availableCategories = Collections.unmodifiableList(availableCategories);
    }
}
