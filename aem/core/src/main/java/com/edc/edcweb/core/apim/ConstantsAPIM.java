package com.edc.edcweb.core.apim;

import com.edc.edcweb.core.helpers.Constants;

public class ConstantsAPIM extends Constants {

    private ConstantsAPIM() {
    }

    public  class APIMProperties {

        // APIM Json attributes
        public static final String APIM_JSON_PROP_ACCESSTOKEN = "access_token";
        public static final String APIM_JSON_PROP_TOKENTYPE = "token_type";
        public static final String APIM_JSON_PROP_EXPIN = "expires_in";
        public static final String APIM_JSON_PROP_EXPON = "expires_on";
        public static final String APIM_JSON_PROP_EXTEXPIN = "ext_expires_in";
        public static final String APIM_JSON_PROP_NOTBEFORE = "not_before";
        public static final String APIM_JSON_PROP_RESOURCE = "resource";


    }

    public class InListJSONProperties {

        // Supplier profile JSON properties
        public static final String JSON_PROP_DELIMITER = "|";
        public static final String JSON_PROP_RESULT = "Result";
        public static final String JSON_PROP_SUPPLIER = "Supplier";
        public static final String JSON_PROP_ID = "Id";
        public static final String JSON_PROP_SUPPLIERID = JSON_PROP_SUPPLIER + JSON_PROP_ID;
        public static final String JSON_PROP_NAME = "Name";
        public static final String JSON_PROP_CODE = "Code";
        public static final String JSON_PROP_DESCRIPTION = "Description";
        public static final String JSON_PROP_SERVICETYPEID = "ServiceTypeId";
        
        
        public static final String JSON_PROP_SUPPLIERNAME = JSON_PROP_SUPPLIER + JSON_PROP_NAME;
        public static final String JSON_PROP_SUPPLIERNAME_EN = JSON_PROP_SUPPLIERNAME + "English";
        public static final String JSON_PROP_SUPPLIERDESCRIPTION = JSON_PROP_SUPPLIER + JSON_PROP_DESCRIPTION;
        public static final String JSON_PROP_SERVICES = "Services";
        public static final String JSON_PROP_SERVICEID = "ServiceId";
        public static final String JSON_PROP_MODESOFTRANSPORTATION = "ModesOfTransportation";
        public static final String JSON_PROP_MODESOFTRANSPORTATIONID = "ServiceId";
        public static final String JSON_PROP_INDUSTRIES = "Industries";
        public static final String JSON_PROP_INDUSTRYID = "IndustryId";
        public static final String JSON_PROP_INDUSTRIESSERVED = JSON_PROP_INDUSTRIES + "Served";
        public static final String JSON_PROP_MARKETSSERVED = "MarketsServed";
        public static final String JSON_PROP_MARKETID = "MarketId";
        public static final String JSON_PROP_CERTIFICATIONS = "Certifications";
        public static final String JSON_PROP_CERTIFICATIONS_NAME = "CertificationName";
        public static final String JSON_PROP_LANGUAGES = "Languages";
        public static final String JSON_PROP_RESPONSETIME = "ReferalResponseTime";
        public static final String JSON_PROP_RESPONSETIMES = "ReferalResponseTimes";
        public static final String JSON_PROP_DISPLAYVALUE = "DisplayValue";
        public static final String JSON_PROP_PREFERREDMETHOD = "PreferredContactChannel";
        public static final String JSON_PROP_EMAIL = "Email";
        public static final String JSON_PROP_EMAIL_LOWERCASE = "email";
        public static final String JSON_PROP_WEBSITE = "Website";
        public static final String JSON_PROP_WEBSITE_LOWERCASE = "website";
        public static final String JSON_PROP_PHONENUMBER = "PhoneNumber";
        public static final String JSON_PROP_PHONE = "phone";
        public static final String JSON_PROP_REQUESTQUOTEURL = "RequestQuoteURL";
        public static final String JSON_PROP_MAINADDRESS = "MainAddress";
        public static final String JSON_PROP_ADDRESS_STREET = "StreetAddress";
        public static final String JSON_PROP_ADDRESS_CITY = "City";
        public static final String JSON_PROP_ADDRESS_PROVINCE = "Province";
        public static final String JSON_PROP_ADDRESS_COUNTRY = "Country";
        public static final String JSON_PROP_ADDRESS_POSTALCODE = "PostalCode";
        public static final String JSON_PROP_ADDRESS_OTHERADDRESSES = "OtherAddresses";
        public static final String JSON_PROP_FILTER = "Filter";
        public static final String JSON_PROP_VALUES = "Values";
        public static final String JSON_PROP_SERVICETYPES = "ServiceTypes";
        public static final String JSON_PROP_FILTER_VALUES = JSON_PROP_FILTER + JSON_PROP_VALUES;
        public static final String JSON_PROP_FILTER_SUPPLIER = "Supplier";
        public static final String JSON_PROP_FILTER_SUPPLIERCARDS = JSON_PROP_FILTER_SUPPLIER + "ShortCards";
        public static final String JSON_PROP_FILTER_NUMBEROFLOCATIONS = "NumberOfOtherLocations";
        public static final String JSON_PROP_FILTER_CATEGORYNAME = "FilterSectionName";
        public static final String JSON_PROP_FILTER_QUOTERESPONSETIMES = "QuoteResponseTimes";
        public static final String JSON_PROP_FILTER_QUOTERESPONSETIMESID = "ReferalResponseTimeId";
        public static final String JSON_PROP_FILTER_MATCHALL = "IsFilterMatchAll";
    }

    /**
     * Header Key that must be present on all requests
     *
     * 
     */
    public enum HeaderParams {
        OCP_APIM_SUB_KEY("Ocp-apim-subscription-key");

        private String headerValue;

        HeaderParams(String headerValue) {
            this.headerValue = headerValue;
        }

        public String getHeaderValue() {
            return headerValue;
        }
    }
}
