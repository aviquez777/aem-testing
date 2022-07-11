package com.edc.edcweb.core.datasources;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.commons.lang.ArrayUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.service.EDCSystemConfigurationService;
import com.edc.edcweb.core.telp.helpers.TelpConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = SlingHttpServletRequest.class)
public class BcapIncDivFieldsLOV {

    private static final Logger log = LoggerFactory.getLogger(BcapIncDivFieldsLOV.class);

    @Self
    private SlingHttpServletRequest request;

    @Inject
    EDCSystemConfigurationService systemConfigurationService;

    /**
     * Populates the Model with values from the systemConfigurationService's LOV
     */
    @PostConstruct
    public void initModel() {
        String[] bcapIncDiv = systemConfigurationService.getBcapIncDivFieldLOV();
        if (ArrayUtils.isNotEmpty(bcapIncDiv)) {
            final ResourceResolver resolver = request.getResourceResolver();
            Iterator<String> iterator = Arrays.asList(bcapIncDiv).iterator();
            @SuppressWarnings("unchecked")
            // Creating the Datasource Object for populating the drop-down control.
            DataSource ds = new SimpleDataSource(new TransformIterator(iterator, new Transformer() {
                @Override
                // Transforms the input object into output object
                public Object transform(Object o) {
                    String confivalue = (String) o;
                    String[] confivaluesArr = confivalue.split(TelpConstants.MULTIPLE_DELIMITER);
                    ValueMap vm = new ValueMapDecorator(new HashMap<String, Object>());
                    if (confivaluesArr.length == 2) {
                        vm.put(Constants.VALUE, confivaluesArr[0]);
                        vm.put(Constants.TEXT, confivaluesArr[1]);
                    } else {
                        log.error("Invalid values for Aarray {}", confivaluesArr);
                    }
                    return new ValueMapResource(resolver, new ResourceMetadata(),
                            Constants.Properties.NT_UNSTRUCTURED, vm);
                }
            }));
            this.request.setAttribute(DataSource.class.getName(), ds);
        }
    }
}
