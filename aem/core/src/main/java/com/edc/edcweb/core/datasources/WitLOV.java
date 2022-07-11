package com.edc.edcweb.core.datasources;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.models.PageHeroBanner;
import com.edc.edcweb.core.service.EDCSystemConfigurationService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.commons.lang.ArrayUtils;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.api.resource.ResourceResolver;

@Model(adaptables = SlingHttpServletRequest.class)
public class WitLOV {

    @Self
    private SlingHttpServletRequest request;

    @Inject
    EDCSystemConfigurationService systemConfigurationService;


    /**
     * Populates the Model with values from the systemConfigurationService's LOV
     */
    @PostConstruct
    public void initModel() {
        String[] witLOV = PageHeroBanner.getSelfIdMultifieldValues();                 
        if (ArrayUtils.isEmpty(witLOV)) {
            witLOV = systemConfigurationService.getWitWebinarLOV();
        }
        if (ArrayUtils.isNotEmpty(witLOV)) {
            final ResourceResolver resolver = request.getResourceResolver();
            Iterator<String> iterator = Arrays.asList(witLOV).iterator();
            @SuppressWarnings("unchecked")
            // Creating the Datasource Object for populating the drop-down control.
            DataSource ds = new SimpleDataSource(new TransformIterator(iterator, new Transformer() {
                @Override
                // Transforms the input object into output object
                public Object transform(Object o) {
                    String witvalue = (String) o;
                    // Allocating memory to Map
                    ValueMap vm = new ValueMapDecorator(new HashMap<String, Object>());
                    vm.put(Constants.TEXT, witvalue);
                    // Leave the Please Select value empty to force selection
                    if (Constants.PLEASE_SELECT.equalsIgnoreCase(witvalue)) {
                        witvalue = "";
                    }
                    vm.put(Constants.VALUE, witvalue);
                    return new ValueMapResource(resolver, new ResourceMetadata(), Constants.Properties.NT_UNSTRUCTURED,
                            vm);
                }
            }));
            this.request.setAttribute(DataSource.class.getName(), ds);
        }
    }    
}
