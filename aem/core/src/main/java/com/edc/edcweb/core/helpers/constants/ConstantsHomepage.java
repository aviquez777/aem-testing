package com.edc.edcweb.core.helpers.constants;

import com.edc.edcweb.core.helpers.Constants;


/**
 * <h1>ConstantsHomepage</h1> The ConstantsHomepage class contains constant definitions for Homepage components.
 **/

public class ConstantsHomepage extends Constants {

    /**
     * Empty constructor
     */
    private ConstantsHomepage() {}
    
    
    /**
     * listProperties s list's sizes.
     *
     */
    public class listProperties {
        // Max Number of elements to retrieve.
        // There may be 2 items on the main cards and always 5 for the right (LATEST_ARTICLES) list. 
        // Start with 7 and drop the last two items as necessary
        public static final int LATEST_ARTICLES_LIST_SIZE = 7;
        public static final int LATEST_ARTICLES_MIN_SIZE = 5;
    }

}
