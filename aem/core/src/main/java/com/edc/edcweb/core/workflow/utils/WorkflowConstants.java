package com.edc.edcweb.core.workflow.utils;

import com.edc.edcweb.core.helpers.Constants;

/**
 * WorkflowConstants: Constants used on work flows
 *
 */

public class WorkflowConstants {

    private WorkflowConstants() {
        // Sonar Lint
    }

    public class Paths {
        private Paths() {
            // SonarQube
        }

        public static final String AEM_INBOX = "/aem/inbox";
        public static final String TEMPLATE_DEFAULT = "/conf/global/settings/workflow/notification/email/edc/en.txt";
    }

    public static final String PROCESS_ARGS = "PROCESS_ARGS";
    public static final String ARGS_DELIMTER = ",";
    public static final String ARGS_NAME_VALUE_DELIMTER = "=";
    public static final String ARGS_USER_GROUP_ID_KEY = "userGroupId";
    public static final String ARGS_SUBJECT_KEY = "subject";
    public static final String ARGS_TEMPLATE_KEY = "template";
    public static final String ARGS_EMAIL_KEY = "emailType";

    public static final String REPLACE_EOL = "[\r|\n]";
    public static final String REPLACE_BR = "<br>";

    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String EMAIL = "email";
    public static final String COMMENT = "comment";
    public static final String SPACE = " ";
    public static final String EMPTY_STRING = "";

    public static final String ACTIVATE_TIME = " Activate Time: ";
    public static final String PUBLILSH_DATE_FIELD_NAME = "publishDate";

    public static final String INITIAL_AUTHOR = "initiator";
    public static final String TEMPLATE_VAR_PAGE_TITLE = "pageTitle";
    public static final String TEMPLATE_VAR_MESSAGE = "message";
    public static final String TEMPLATE_VAR_USER = "user";
    public static final String TEMPLATE_VAR_INBOX_URL = "inboxUrl";
    public static final String TEMPLATE_VAR_SUBJECT = "subject";
    public static final String TEMPLATE_VAR_WORKFLOW_TITLE = "workflowTitle";
    public static final String TEMPLATE_VAR_DEFAULT_WORKFLOW_TITLE = "No title Provided";

    public static final String AEM_EDITOR = "/editor.html".concat(Constants.Paths.CONTENT_EDC);
    public static final String AEM_VIEW_AS_PUBLISHED_QS = "?wcmmode=disabled";

    public static final String TEMPLATE_VAR_AUTHOR_URL = "authorUrl";
    public static final String TEMPLATE_VAR_VIEW_AS_PUBLISHED_URL = "viewAsPublishedUrl";
    public static final String TEMPLATE_VAR_PUBLISHED_URL = "publishedUrl";

    public static final String EMAIL_FROM_ADDRESS = "aem-notification@amsmail.adobecqms.net";
    public static final String EMAIL_SUBJECT_REPLACE_TITLE = "-title-of-page-";
    public static final String EMAIL_SUBJECT = "No Subject Defined ".concat(EMAIL_SUBJECT_REPLACE_TITLE);

    public static final String DEFAULT_TEMPLATE = "/conf/global/settings/workflow/notification/email/edc/default/en.txt";

    public static final String EMAIL_TYPE_NOTIFICATION = "notification";
    public static final String EMAIL_TYPE_FYI = "fyi";
    public static final String EMAIL_TYPE_NONE = "none";

    public static final String HISTORY_USER_FULL_NAME_KEY = "userFullName";
    public static final String HISTORY_MESSAGE_KEY = "message";
    public static final String HISTORY_EXTRA_MESSAGE_KEY = "extraMessage";
    public static final String HISTORY_STEP_TITLE = "_title";
    public static final String HISTORY_TAKES_OWNERSHIP = "Takes Ownership";
    public static final String HISTORY_NO_MESSAGE_ENTERED = "Message box was intentionally left blank";

    public static final String HTML_LESS_THAN_ENTITY = "&lt;";
    public static final String HTML_GREATER_THAN_ENTITY = "&gt;";
    public static final String HTML_LEFT_DOUBLE_QUOTE_ENTITY = "&ldquo;";
    public static final String HTML_RIGHT_DOUBLE_QUOTE_ENTITY = "&lrdquo";
    
    public static final String ADMIN_USER = "admin";

}