package com.edc.edcweb.core.myedc.eloqua;

public class EloquaConnectionManagerConstants {

    public static final String API_VERSION_VARIABLE = "{version}";
    // **IMPORTANT** if version changes, will impact jsons structure
    public static final String API_VERSION = "2.0";
    public static final Integer ENDPOINT_CACHE_TIME = 14400;
    public static final String MULTI_VALUE_SEPARATOR = "::";
    public static final String UPDATE_APIM_PROFILE_TYPES = "canadian-company,non-canadian,fi,broker";

    public static final String COMPANY_PROVINCE_FIELD_NAME = "companyProvince";
    public static final String STATE_FIELD_NAME = "companyProvinceAlt";
    public static final String PROVINCE_INPUT_FIELD_NAME = "companyProvinceInput";
    public static final String LANGUAGE_INPUT_FIELD_NAME = "language";
    public static final String ELOQUA_GUID_FIELD_NAME = "elqCustomerGuid";
    public static final String COMPANY_POSTAL_FIELD_NAME = "companyPostal";

    public class CacheObjects {

        private CacheObjects() {
            // Sonar Lint
        }

        public static final String ELOQUA_ENDPOINT = "eloqua_endpoint";
        // changed key name because Progressive Profiling and MyEDC use the same key name but different object type
        public static final String ELOQUA_TOKEN = "eloqua_token_jsonobj";
        public static final String ELOQUA_CDO = "eloqua_cdo";
        public static final String SESSION_TOKEN = "session_token";
        public static final String ELOQUA_OBJECT_TOKEN = "eloqua_token";

    }

    public class EndPoints {

        private EndPoints() {
            // Sonar Lint
        }

        // baseUrl from service has the "/"
        public static final String CDO_ENDPOINT_ASSET = "assets/";
        public static final String CDO_ENDPOINT_DATA = "data/";
        public static final String CDO_ENDPOINT = "customObject/";

        // String CDO id (no "/") is between CDO_ENDPOINT and the following, so we need
        // the preceding "/"
        public static final String INSTANCE_ENDPOINT = "/instance";
        public static final String INSTANCES_ENDPOINT = "/instances";

        // note opening single quote
        public static final String INSTANCES_ENDPOINT_UNIQUE_CODE_QUERY_STRING = "?search='UniqueCode=";
        public static final String INSTANCES_ENDPOINT_EMAIL_QUERY_STRING = "?search='name=";
        // must uelenconde space
        public static final String INSTANCES_ENDPOINT_ORDER_BY_CREATED_BY_DESC = "&orderBy=updatedAt%20DESC";
    }

    public class JsonKeys {
        private JsonKeys() {
            // Sonar Lint
        }

        public static final String BASE_URL_URLS = "urls";
        public static final String BASE_URL_APIS = "apis";
        public static final String BASE_URL_REST = "rest";
        public static final String BASE_URL_STANDARD = "standard";

        public static final String ACCESS_TOKEN = "access_token";
        public static final String ACCESS_TOKEN_EXPIRES = "expires_in";

        public static final String SEARCH_RECORD_ELEMENTS = "elements";
        public static final String SEARCH_TOTAL = "total";
        public static final String FIELD_VALUES = "fieldValues";
        public static final String ID = "id";
        public static final String VALUE = "value";
        public static final String CONTACT_ID = "contactId";
        public static final String INTERNAL_NAME = "internalName";
        public static final String TYPE = "type";
        public static final String COD = "CustomObjectData";
        public static final String FIELD_VALUE = "FieldValue";
        public static final String FIELDS = "fields";
        public static final String CREATED_AT = "createdAt";
    }

    public class BodyRequests {
        private BodyRequests() {
            // Sonar Lint
        }

        public static final String GET_TOKEN_JSON = "{\"grant_type\":\"password\", \"scope\":\"full\", \"username\":\"%1$s\", \"password\":\"%2$s\"}";

    }

}
