package com.edc.edcportal.core.helpers.constants;

import com.edc.edcportal.core.helpers.Constants;

public class ConstantsDatalayer extends Constants {

    private ConstantsDatalayer() { }

    public class DLProperties extends Constants.Properties {

        public static final String DATALAYER_ENV_DEV = "dev";
        public static final String DATALAYER_ENV_PROD = "prod";
        public static final String DATALAYER_ENV_QA = "qa";
        public static final String DATALAYER_ENV_STAGE = "stage";
        public static final String DATALAYER_ENV_TEST = "test";
        public static final String DATALAYER_TAG_SEPARATOR = ";";
    }
}
