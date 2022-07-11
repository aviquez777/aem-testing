package com.edc.edcweb.core.restful;

public class RestClientConstants {

    public static final String STATUS_CODE = "StatusCode";
    public static final String ERROR_MSG = "ErrorMessage";
    public static final String ERROR_TXT = ERROR_MSG; // BAckwards compatibility
    public static final String AUTHORIZATION = "Authorization";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String ACCEPT = "Accept";
    public static final String CHAR_SET_TEXT = "charset";
    public static final String CONTENT_LEGTH = "Content-Length";
    public static final String UTF_8_CHAR_SET = "UTF-8";
    public static final String BACK_SLASH = "\\";
    public static final String FORWARD_SLASH = "/";
    public static final String DOUBLE_BACK_SLASH = "\\\\";
    public static final String QUOTE = "\"";
    public static final String SPACE = " ";
    public static final String EQUAL_SIGN = "=";
    public static final String COLON = ":";
    public static final String COLON_SPACE = COLON+SPACE;
    public static final String SEMI_COLON = ";";
    public static final String SEMI_COLON_SPACE = SEMI_COLON+SPACE;
    public static final String OPEN_BRACKET = "{";
    public static final String CLOSE_BRACKET = "}";
    public static final String COMMA = ",";
    public static final String MULTIPART_FORM_DATA_BOUNDARY = "multipart/form-data; boundary=";
    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    public static final String MULTIPART_FORM_DATA_NAME = "form-data; name=\"";
    public static final String MULTIPART_FORM_DATA_FILE_NAME = "\" ; filename=\"";

    public static final String BOUNDARY_TEXT = "----WebKitFormBoundary";
    public static final String USER_AGENT_KEY = "User-Agent";
    public static final String USER_AGENT_VALUE = "ACN / EDC / AEM JAVA User Agent";
    public static final String TEXT_PLAIN_CHARCTER_SET = "text/plain charset=";
    public static final String APPLICATION_JSON_CHARACTER_SET = APPLICATION_JSON+SEMI_COLON_SPACE+CHAR_SET_TEXT+EQUAL_SIGN;
    public static final String MULTIPART = "multipart";

    public static final int ERROR_STATUS = 500;
    public static final int OK_STATUS = 200;

    public static final String CACHE_CONTROL_HEADER = "Cache-Control";
    public static final String CACHE_CONTROL_HEADER_VALUE = "no-cache, no-store, must-revalidate";
    public static final String PRAGMA_HEADER = "Pragma";
    public static final String PRAGMA_HEADER_NO_CACHE_VALUE = "no-cache";
    public static final String EXPIRES_HEADER = "Expires";
    public static final String EXTENDED_ERROR = "extendedError";

    // Task 221435 CQRules:ConnectionTimeoutMechanism
    // 30 seconds as sometimes the API takes a while to answer
    public static final int CONNECT_TIMEOUT = 30000;
    // Separate values if necessary
    public static final int READ_TIMEOUT = CONNECT_TIMEOUT;


    public class Methods {
        private Methods() {
            // Add a private constructor to hide the implicit public one.
        }

        public static final String METHOD_GET = "GET";
        public static final String METHOD_POST = "POST";
        public static final String METHOD_PUT = "PUT";
    }

    public class AuthMethods {
        private AuthMethods() {
            // Add a private constructor to hide the implicit public one.
        }

        // add space at the end for easier concatenation
        public static final String BASIC_AUTH = "Basic ";
        public static final String BEARER_TOKEN = "Bearer ";
    }
}
