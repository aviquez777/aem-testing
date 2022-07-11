package com.edc.edcweb.core.models;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.ehh.EHHHelper;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.Map;

@Model(adaptables = SlingHttpServletRequest.class)
public class EhhTeamForm {
    private Map<String, String> markets = new LinkedHashMap<>();

    @Self
    private SlingHttpServletRequest request;

    @Inject
    @Optional
    private Page currentPage;

    @ScriptVariable
    private ValueMap properties;

    @PostConstruct
    protected void init() {
        markets = EHHHelper.getMarketsMap(properties.get("marketstag", String.class), currentPage, request);
    }

    public Map<String, String> getMarkets() {
        return markets;
    }
}
