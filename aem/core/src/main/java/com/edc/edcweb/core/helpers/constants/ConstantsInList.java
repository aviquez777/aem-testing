package com.edc.edcweb.core.helpers.constants;

import com.edc.edcweb.core.helpers.Constants;

public class ConstantsInList extends Constants {

    /**
     * Empty Constructor
     */

    private ConstantsInList() {
    }

    public class Paths
    {
        private Paths() { }

        public static final String FEEDBACK_FORM_SERVLET="/bin/feedbackFormServlet";
        public static final String FEEDBACK_FORM_TYPE = "edc/components/content/inlist/feedbackform";

        public static final String PROVIDER_PAGE_SELECTOR = "/provider.service";
        public static final String PROVIDER_PAGE_SELECTOR_ALIAS = "/fournisseur.service";
    }

    public  class InListProperties {
        public static final String INLIST_SERVICE = "service";
        public static final String ENGLISH_ABBR = "en";

        // Feedback form
        public static final String FEEDBACK_FORM_EMAIL = "email";
        public static final String FEEDBACK_FORM_EMAIL_BCC = "emailBcc";
        public static final String FEEDBACK_FORM_EMAIL_ADDRESS = "emailAddress";
        public static final String FEEDBACK_FORM_SUBJECT = "subject";
        public static final String FEEDBACK_FORM_COMMENT = "comment";
    }
}
