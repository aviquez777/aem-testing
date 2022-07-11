package com.edc.edcweb.core.helpers.constants;

import java.util.LinkedHashMap;
import java.util.Map;

import com.edc.edcweb.core.helpers.Constants;

/**
 * <h1>ConstantsMEA</h1> The ConstantsMEA class contains constant definitions for MEA components.
 */

public class ConstantsMEA extends Constants {

    /**
     * Empty Constructor
     */
    private ConstantsMEA() {}

    /**
     * This method returns the Node name in case that it needs to be created.
     * 
     * @param none
     * @return String
     * 
     */
    public class NodeNames {
        public static final String CARD_GRID_NODE_NAME = "contactprofile";
    }

    /**
     * This method returns the Component's Resource Type in case that it needs to be
     * created.
     * 
     * @param none
     * @return String
     * 
     */
    public class ResourceTypes {
        public static final String CONTACT_PROFILE_RESOURCE_TYPE = "edc/components/content/mea/contactprofile";
    }

    /**
     * This method returns the Profile labels beng returned from the Components
     * Policy
     * 
     * @param none
     * @return String
     * 
     */
    public class ProfileLabels {
        public static final String EST = "est";
        public static final String HOURS = "hour";
    }

    /**
     * This method returns a Map<String, String> to create the node resources.
     * Also is used to create to embed the resources on the HTL
     * 
     * @param none
     * @return  Map<String, String> resources()
     * 
     */
    public static Map<String, String> resources() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("text1", "edc/components/content/text");
        map.put("pullquote", "edc/components/content/article/pullquote");
        map.put("text2", "edc/components/content/text");
        return map;
    }

}
