package com.edc.edcportal.core.helpers;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;

import com.google.common.collect.ImmutableList;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <h1>MultifieldReaderHelper</h1> The MultifieldReaderHelper class receive the
 * multifield variable name and returns its values in a list for easy access
 */

@Model(adaptables = SlingHttpServletRequest.class)
public class MultifieldReaderHelper {

    @Inject
    private Resource resource;

    @Inject
    private String multifield;

    private List<ValueMap> fields;

    /**
     * This is the init method which reads the node when invoked Sets the fields
     * variable to be returned when the getter is invoked
     *
     * @param String
     *            multifield;.
     * @return Nothing.
     */

    @PostConstruct
    protected void init() {
        Resource fieldsContainerResource = resource.getChild(multifield);
        if (fieldsContainerResource != null) {
            fields = new ArrayList<>();
            Iterator<Resource> fieldsIterator = fieldsContainerResource.listChildren();
            while (fieldsIterator.hasNext()) {
                fields.add(fieldsIterator.next().getValueMap());
            }
        }
    }

    /**
     * This is the getter method which makes use of addNum method.
     *
     * @return List<ValueMap> with the values on each multifield node. null if the
     *         given parameter has no children.
     */

    public List<ValueMap> getFields() {
        // Task 22143 squid:S2384
        return fields != null ? ImmutableList.copyOf(fields) : fields;
    }
}