package com.edc.edcweb.core.models;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;

@Model(adaptables = SlingHttpServletRequest.class)
public class MultiFieldPolicyReader {

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Page currentPage;

    @Inject
    @Optional
    private String multifieldName;

    @Inject
    @Optional
    private String fieldKeyName;

    @Inject
    @Optional
    private String fieldValueName;

    private Boolean policy = false;

    private Map<String, String> valueMap = new LinkedHashMap<>();

    private List<ValueMap> valueMapList = new LinkedList<>();

    /**
     * Utility to read from policies Receives the multi field name and variables to
     * retrieve from policy if no variables are sent no policy will be read
     */
    @PostConstruct
    public void initModel() {
        // policy properties
        ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(this.request, this.currentPage);
        if (contentPolicy != null && StringUtils.isNotBlank(multifieldName)) {
            policy = true;
            Boolean doMyValueMap = (StringUtils.isNotBlank(fieldKeyName) && StringUtils.isNotBlank(fieldValueName));
            Resource policyResource = contentPolicy.adaptTo(Resource.class);
            if (policyResource != null) {
                Resource multifield = policyResource.getChild(multifieldName);
                if (multifield != null) {
                    Iterator<Resource> fieldsIterator = multifield.listChildren();
                    while (fieldsIterator.hasNext()) {
                        Resource item = fieldsIterator.next();
                        valueMapList.add(item.getValueMap());
                        if (doMyValueMap) {
                            // Create the value map
                            ValueMap values = item.getValueMap();
                            String key = values.get(fieldKeyName, String.class);
                            String value = values.get(fieldValueName, String.class);
                            valueMap.put(key, value);
                        }
                    }
                }
            }
        }
    }

    public Boolean getPolicy() {
        return policy;
    }

    public Map<String, String> getValueMap() {
        return valueMap;
    }

    public List<ValueMap> getValueMapList() {
        return valueMapList;
    }

}
