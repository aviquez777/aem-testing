package com.edc.edcweb.core.helpers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerNameResolverHelper {

    static final Logger log = LoggerFactory.getLogger(ServerNameResolverHelper.class);

    private ServerNameResolverHelper() {
        // SonarLint
    }

    /**
     * 
     * @param slingSettingsService
     * @return
     */
    public static Map<String, String> getServerNames(SlingSettingsService slingSettingsService) {
        Map<String, String> serverNames = new HashMap<>();
        Set<String> runModes = slingSettingsService.getRunModes();
        // don't change names for local
        if (runModes != null && !runModes.contains("local")) {
            String author = "";
            String dispatcher = "";
            if (runModes.contains("dev")) {
                author = "dev-65a";
                dispatcher = "dev";
            }
            if (runModes.contains("test")) {
                author = "test-65";
                dispatcher = "qac";
            }
            if (runModes.contains("stage")) {
                author = "stage-65";
                dispatcher = "ppr";
            }
            if (runModes.contains("prod")) {
                author = "prod-65";
            }
            author = "author-edc-".concat(author).concat(".adobecqms.net");
            dispatcher = "www".concat(dispatcher).concat(".edc.ca");
            serverNames.put("author", author);
            serverNames.put("dispatcher", dispatcher);
        }
        return serverNames;
    }

    public static String getServerName(String url) {
        String serverName = "";
        try {
            URI uri = new URI(url);
            serverName = uri.getHost();
            int portNum = uri.getPort();
            if (portNum != -1) {
                serverName = serverName.concat(":").concat(Integer.toString(portNum));
            }
        } catch (URISyntaxException e) {
            log.error("ServerNameResolverHelper getServerName error {}", e.getStackTrace());
        }
        return serverName;
    }

}
