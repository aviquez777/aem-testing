package com.edc.edcweb.core.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;

@Model(adaptables = SlingHttpServletRequest.class)
public class PolicyReader {

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Page currentPage;

    private Boolean policy = false;

    private Map<String, String> labels = new HashMap<>();



    @PostConstruct
    public void initModel() {
        // policy properties
        ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(this.request, this.currentPage);
        if (contentPolicy != null) {
            policy = true;
            ValueMap properties = contentPolicy.getProperties();
            for (Entry<String, Object> e : properties.entrySet()) {
                String key = e.getKey();
                if (!key.startsWith("jcr:") && !key.startsWith("sling:")) {
                    String value = e.getValue().toString();
                    labels.put(key, value);
                }
            }
        }

    }

    public Map<String, String> getLabels() {
        return new TreeMap<>(labels);
    }

    public Boolean getPolicy() {
        return policy;
    }


}
