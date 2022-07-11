package com.edc.edcweb.core.helpers.constants;

import com.edc.edcweb.core.helpers.Constants;

public class ConstantsAccessibility extends Constants {

    private ConstantsAccessibility() { }

    public class CAProperties extends Constants.Properties {
        public static final String ACCESSIBILITY_VIDEOPLAYBTN = "videoplaybtn";
        public static final String ACCESSIBILITY_VIDEOCLOSEBTN = "videoclosebtn";
        public static final String ACCESSIBILITY_VIDEONOTSUPPORT = "videonotsupport";
        public static final String ACCESSIBILITY_DIRECTIONS_TEXT = "directionsText";
        public static final String ACCESSIBILITY_RESULTS_TEXT = "resultsText";
        public static final String ACCESSIBILITY_NORESULTS_TEXT = "noresultsText";
        public static final String ACCESSIBILITY_SCROLLLEFT = "scrollleft";
        public static final String ACCESSIBILITY_SCROLLRIGHT = "scrollright";

        /* EHH filter component */
        public static final String ACCESSIBILITY_START_DIRECTIONS_TEXT = "startDirectionsText";
        public static final String ACCESSIBILITY_MIDDLE_DIRECTIONS_TEXT = "middleDirectionsText";
        public static final String ACCESSIBILITY_END_DIRECTIONS_TEXT = "endDirectionsText";
    }
}
