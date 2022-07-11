package com.edc.edcweb.core.helpers.constants;

import com.edc.edcweb.core.helpers.Constants;

public class ConstantsEmailSender extends Constants {

    /**
     * Empty Constructor
     */
    private ConstantsEmailSender() {
    }

    public class Properties {
        public static final String EMAIL_TO = "emailTo";
        public static final String EMAIL_CC = "emailCc";
        public static final String EMAIL_BCC = "emailBcc";
        public static final String SUBJECT = "subject";
        public static final String MESSAGE = "message";
        public static final String EMAIL_FROM = "emailFrom";
        public static final String ALLOWED_MIME_TYPES = "allowedMimeTypes";
        public static final String ATTACHES = "attaches";
        public static final String ATTACHES_FILTER = "attachesFilter";
        public static final String EXCEPTION_MESSAGE = "Email Exception";
    }
}
