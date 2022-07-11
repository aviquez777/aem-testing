package com.edc.edcweb.core.components.interactionbutton;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Model(adaptables = { Resource.class,
        SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class IBAttributesHandler {

    @Inject
    @Default(values = " ")
    private String attributes;

    @Inject
    @Optional
    private String dataUrlQs;

    private Map<String, String> attributesMap;

    @PostConstruct
    protected void init() {
        this.attributesMap = new HashMap<>();
        if (StringUtils.isNotBlank(this.attributes)) {
            String resultantString = buildIncomingString();
            List<String> attribsList = Arrays.asList(resultantString.split(","));
            for (int i = 0; i < attribsList.size(); i++) {
                String[] attribs = attribsList.get(i).split("=");
                if (attribs.length > 1) {
                    this.attributesMap.put(attribs[0], attribs[1]);
                }
            }
            // fix for data-url query string problems
            if (StringUtils.isNotBlank(dataUrlQs)) {
                this.attributesMap.put("data-url", dataUrlQs);
            }
        }
    }

    private String buildIncomingString() {
        int delimeterCounter = 0;
        StringBuilder incomingString = new StringBuilder(this.attributes);
        for (int i = 0; i < incomingString.length(); i++) {
            char c = this.attributes.charAt(i);
            if (c == '=') {
                delimeterCounter++;
            }
            if (delimeterCounter == 2) {
                delimeterCounter = 0;
                incomingString.setCharAt(i, ',');
            }
        }
        return incomingString.toString();
    }

    public Map<String, String> getAttributeMap() {
        return this.attributesMap;
    }
}