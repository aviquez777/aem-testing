package com.edc.edcweb.core.helpers.constants;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.edc.edcweb.core.helpers.Constants;

/**
 * <h1>ConstantsMyEdc</h1> The ConstantsMyEdc class contains constant definitions for MyEDC components.
 */

public class ConstantsMyEdc extends Constants {

    public static final Map<String, String> PATHSMAP = Stream.of(new Object[][]{ // NOSONAR legacy code
            {"profile.html", "profil.html"},
            {"home.html", "accueil.html"},
            {"register.html", "registre.html"},
            {"profile-edit.html", "modifiez-profil.html"},
            {"profile-type.html", "type-de-profil.html"},
            {"my-account","mon-compte"},
            {"profile-renewal.html", "profil-renouvellement.html"}
    }).collect(Collectors.toMap(data -> (String) data[0], data -> (String) data[1]));
    /**
     * Empty Constructor
     */
    private ConstantsMyEdc() {}

    /**
     * This method returns the Node name in case that it needs to be created.
     * 
     * @param none
     * @return String
     * 
     */
    public class NodeNames {
        public static final String INFO_BANNER_NODE_NAME = "infobanner";
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
        public static final String INFO_BANNER_RESOURCE_TYPE = "edc/components/content/myedc/infobanner";
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
        map.put("signintext", "edc/components/content/text");
        map.put("helptext", "edc/components/content/text");
        return map;
    }

    public class Paths {
        private Paths() { }

        public static final String MYEDC_CCONTENT = "content/myedc/";
        public static final String MYEDC_CCONTENT_FR = "content/myedc/fr";
        public static final String MYEDC_CCONTENT_EN = "content/myedc/en";
    }
}
