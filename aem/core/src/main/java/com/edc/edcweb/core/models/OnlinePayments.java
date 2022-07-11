package com.edc.edcweb.core.models;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.edc.edcweb.core.helpers.QueryStringHelper;
import com.edc.edcweb.core.onlinepayments.services.MonerisConfigurationService;

@Model(adaptables = SlingHttpServletRequest.class)
public class OnlinePayments {

    @Inject
    private MonerisConfigurationService monerisConfigurationService;

    @Self
    private SlingHttpServletRequest request;

    String javaScriptUrl;
    String environment;

    @PostConstruct
    public void initModel() {
        javaScriptUrl = monerisConfigurationService.getJavaScriptUrl();
        environment = monerisConfigurationService.getEnvironment();
    }

    public String getJavaScriptUrl() {
        return javaScriptUrl;
    }

    public String getEnvironment() {
        return environment;
    }

    /**
     * getParams Values are encoded using ISO_8859_1, but our servers are UTF_8 If
     * we read the parameter, JAVA will decode the value using UTF_8 and this will
     * create a weird character. Looping over the "raw" query string will not decode
     * the values, and will allow us to properly decode the value using using
     * ISO_8859_1
     * 
     * @return a Value Map with the parameter as key and the value as value
     */
    public Map<String, String> getParams() {
        return QueryStringHelper.getParamMap(request.getQueryString(), StandardCharsets.ISO_8859_1);
    }
}
